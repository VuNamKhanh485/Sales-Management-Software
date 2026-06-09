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
import com.g4fpt.sms.voucher.enums.DiscountType;
import com.g4fpt.sms.voucher.enums.VoucherStatus;
import com.g4fpt.sms.voucher.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderTransactionServiceImpl implements OrderTransactionService {

    private final OrderTransactionRepository orderRepo;
    private final ProductUnitRepository productUnitRepo;
    private final CustomerRepository customerRepo;
    private final VoucherRepository voucherRepo;

    @Override
    @Transactional
    public OrderTransaction processCheckout(POSCheckoutRequest request) {

        OrderTransaction order = new OrderTransaction();
        order.setBranchId(request.getBranchId());
        order.setCreatedBy(request.getEmployeeId());
        order.setPaymentMethodId(request.getPaymentMethodId());
        order.setCode("POS" + System.currentTimeMillis());
        order.setTransactionType("SALE");
        order.setNote(request.getNote());
        order.setPaidAmount(request.getPaidAmount() != null ? request.getPaidAmount() : BigDecimal.ZERO);

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (POSCartItemRequest itemReq : request.getItems()) {
            ProductUnit pu = productUnitRepo.findById(itemReq.getProductUnitId())
                    .orElseThrow(() -> new RuntimeException("Lỗi: Sản phẩm không tồn tại!"));

            BigDecimal salePrice = pu.getPrice();
            BigDecimal itemDiscount = itemReq.getItemDiscount() != null ? itemReq.getItemDiscount() : BigDecimal.ZERO;
            BigDecimal lineTotal = salePrice.multiply(BigDecimal.valueOf(itemReq.getQuantity())).subtract(itemDiscount);

            totalAmount = totalAmount.add(lineTotal);

            OrderTransactionDetail detail = OrderTransactionDetail.builder()
                    .orderTransaction(order)
                    .productUnit(pu)
                    .quantity(itemReq.getQuantity())
                    .salePrice(salePrice)
                    .discountAmount(itemDiscount)
                    .totalAmount(lineTotal)
                    .build();

            order.getDetails().add(detail);
        }
        order.setTotalAmount(totalAmount);

        BigDecimal totalDiscount = BigDecimal.ZERO;
        Customer customer = null;

        if (request.getCustomerId() != null) {
            customer = customerRepo.findById(request.getCustomerId()).orElse(null);
            if (customer != null) {
                order.setCustomer(customer);
                if (customer.getCustomerRank() != null) {
                    BigDecimal rankDiscount = totalAmount.multiply(customer.getCustomerRank().getDiscountRate())
                            .divide(BigDecimal.valueOf(100));
                    totalDiscount = totalDiscount.add(rankDiscount);
                }
            }
        }

        if (request.getVoucherCode() != null && !request.getVoucherCode().isBlank()) {
            Voucher voucher = voucherRepo.findByCode(request.getVoucherCode().trim())
                    .orElseThrow(() -> new RuntimeException("Voucher không hợp lệ!"));

            if (voucher.getStatus() == VoucherStatus.ACTIVE && voucher.getMinOrderAmount().compareTo(totalAmount) <= 0) {
                order.setVoucher(voucher);
                BigDecimal vDiscount = voucher.getDiscountType() == DiscountType.AMOUNT
                        ? voucher.getDiscountValue()
                        : totalAmount.multiply(voucher.getDiscountValue()).divide(BigDecimal.valueOf(100));

                if (voucher.getMaxDiscountAmount() != null && vDiscount.compareTo(voucher.getMaxDiscountAmount()) > 0) {
                    vDiscount = voucher.getMaxDiscountAmount();
                }
                totalDiscount = totalDiscount.add(vDiscount);
            }
        }
        order.setDiscountAmount(totalDiscount);

        BigDecimal finalAmount = totalAmount.subtract(totalDiscount);
        if (finalAmount.compareTo(BigDecimal.ZERO) < 0) finalAmount = BigDecimal.ZERO;
        order.setFinalAmount(finalAmount);

        BigDecimal changeAmount = order.getPaidAmount().subtract(finalAmount);
        if (changeAmount.compareTo(BigDecimal.ZERO) < 0 && request.getPaymentMethodId() == 1L) {
            throw new RuntimeException("Lỗi: Khách đưa chưa đủ tiền (" + order.getPaidAmount() + " < " + finalAmount + ")");
        }
        order.setChangeAmount(changeAmount.compareTo(BigDecimal.ZERO) > 0 ? changeAmount : BigDecimal.ZERO);

        if (customer != null) {
            customer.setTotalRevenue(customer.getTotalRevenue().add(finalAmount));
            customer.setTotalPoint(customer.getTotalPoint() + finalAmount.divide(BigDecimal.valueOf(10000)).intValue());
            customerRepo.save(customer);
        }

        return orderRepo.save(order);
    }
}