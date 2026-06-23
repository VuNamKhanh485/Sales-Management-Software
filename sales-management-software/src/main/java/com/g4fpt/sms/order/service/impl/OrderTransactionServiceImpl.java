package com.g4fpt.sms.order.service.impl;

import com.g4fpt.sms.customer.entity.Customer;
import com.g4fpt.sms.customer.repository.CustomerRepository;
import com.g4fpt.sms.order.dto.POSCartItemRequest;
import com.g4fpt.sms.order.dto.POSCheckoutRequest;
import com.g4fpt.sms.order.entity.OrderTransaction;
import com.g4fpt.sms.order.entity.OrderTransactionDetail;
import com.g4fpt.sms.order.repository.OrderTransactionRepository;
import com.g4fpt.sms.order.service.OrderTransactionService;
import com.g4fpt.sms.product.entity.ProductUnit;
import com.g4fpt.sms.product.repository.ProductUnitRepository;
import com.g4fpt.sms.voucher.entity.Voucher;
import com.g4fpt.sms.voucher.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderTransactionServiceImpl implements OrderTransactionService {

    private final OrderTransactionRepository orderTransactionRepository;
    private final ProductUnitRepository productUnitRepository;
    private final CustomerRepository customerRepository;
    private final VoucherRepository voucherRepository;

    @Override
    @Transactional
    public OrderTransaction processCheckout(POSCheckoutRequest request) {

        // 1. Tính tổng tiền hàng
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (POSCartItemRequest item : request.getItems()) {
            ProductUnit pu = productUnitRepository.findById(item.getProductUnitId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
            totalAmount = totalAmount.add(
                    pu.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
            );
        }

        // 2. Tính VAT
        BigDecimal vatRate = request.getVatRate() != null
                ? request.getVatRate()
                : new BigDecimal("0.08");
        BigDecimal vatAmount = totalAmount.multiply(vatRate)
                .setScale(0, RoundingMode.HALF_UP);

        // 3. Tính giảm giá từ voucher
        BigDecimal discountAmount = BigDecimal.ZERO;
        Voucher voucher = null;
        if (request.getVoucherCode() != null && !request.getVoucherCode().isBlank()) {
            voucher = validateVoucher(request.getVoucherCode(), totalAmount);
            discountAmount = calculateVoucherDiscount(voucher, totalAmount);
        }

        // 4. Tính tiền khách phải trả
        BigDecimal finalAmount = totalAmount.add(vatAmount).subtract(discountAmount)
                .max(BigDecimal.ZERO);

        // 5. Tính tiền thừa
        BigDecimal paidAmount = request.getPaidAmount() != null
                ? request.getPaidAmount()
                : finalAmount;
        BigDecimal changeAmount = paidAmount.subtract(finalAmount).max(BigDecimal.ZERO);

        // 6. Tạo OrderTransaction
        OrderTransaction order = OrderTransaction.builder()
                .branchId(request.getBranchId())
                .createdBy(request.getEmployeeId())
                .code("ORD-" + System.currentTimeMillis())
                .totalAmount(totalAmount)
                .discountAmount(discountAmount)
                .finalAmount(finalAmount)
                .paidAmount(paidAmount)
                .changeAmount(changeAmount)
                .status("COMPLETED")
                .transactionType("SALE")
                .paymentMethodId(request.getPaymentMethodId())
                .note(request.getNote())
                .build();

        // 7. Gán khách hàng
        if (request.getCustomerId() != null) {
            Customer customer = customerRepository.findById(request.getCustomerId())
                    .orElse(null);
            if (customer != null) {
                order.setCustomer(customer);
                // Cộng điểm: 10,000đ = 1 điểm
                int pointEarned = finalAmount.divide(
                        new BigDecimal("10000"), 0, RoundingMode.FLOOR).intValue();
                customer.setTotalPoint(customer.getTotalPoint() + pointEarned);
                customer.setTotalRevenue(customer.getTotalRevenue().add(finalAmount));
            }
        }

        // 8. Gán voucher
        if (voucher != null) {
            order.setVoucher(voucher);
        }

        // 9. Tạo OrderTransactionDetail
        for (POSCartItemRequest item : request.getItems()) {
            ProductUnit pu = productUnitRepository.findById(item.getProductUnitId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

            OrderTransactionDetail detail = OrderTransactionDetail.builder()
                    .orderTransaction(order)
                    .productUnit(pu)
                    .quantity(item.getQuantity())
                    .salePrice(pu.getPrice())
                    .discountAmount(BigDecimal.ZERO)
                    .totalAmount(pu.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                    .build();

            order.getDetails().add(detail);
        }

        return orderTransactionRepository.save(order);
    }

    @Override
    public Voucher validateVoucher(String code, BigDecimal totalAmount) {
        Voucher voucher = voucherRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Mã voucher không tồn tại!"));

        if (voucher.getStatus().name().equals("INACTIVE")) {
            throw new RuntimeException("Voucher đã bị vô hiệu hóa!");
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(voucher.getStartAt()) || now.isAfter(voucher.getEndAt())) {
            throw new RuntimeException("Voucher đã hết hạn hoặc chưa đến ngày sử dụng!");
        }

        if (totalAmount.compareTo(voucher.getMinOrderAmount()) < 0) {
            throw new RuntimeException("Đơn hàng chưa đạt giá trị tối thiểu "
                    + voucher.getMinOrderAmount() + "đ để dùng voucher này!");
        }

        return voucher;
    }

    private BigDecimal calculateVoucherDiscount(Voucher voucher, BigDecimal totalAmount) {
        BigDecimal discount;

        if (voucher.getDiscountType().name().equals("PERCENT")) {
            discount = totalAmount.multiply(voucher.getDiscountValue())
                    .divide(new BigDecimal("100"), 0, RoundingMode.HALF_UP);
            // Giới hạn max discount nếu có
            if (voucher.getMaxDiscountAmount() != null) {
                discount = discount.min(voucher.getMaxDiscountAmount());
            }
        } else {
            // AMOUNT
            discount = voucher.getDiscountValue();
        }

        return discount.min(totalAmount); // không giảm quá tổng tiền
    }
}