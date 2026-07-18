package com.g4fpt.sms.order.service.impl;

import com.g4fpt.sms.customer.entity.Customer;
import com.g4fpt.sms.customer.repository.CustomerRepository;
import com.g4fpt.sms.inventory.entity.Inventory;
import com.g4fpt.sms.inventory.repository.InventoryRepository;
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
import com.g4fpt.sms.customer.service.CustomerService;

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
    private final CustomerService customerService;
    private final InventoryRepository inventoryRepository;

    /** 1 điểm = 200đ */
    private static final int POINT_VALUE = 200;

    @Override
    @Transactional
    public OrderTransaction processCheckout(POSCheckoutRequest request) {

        // Tính tổng tiền hàng
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (POSCartItemRequest item : request.getItems()) {
            ProductUnit pu = productUnitRepository.findById(item.getProductUnitId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
            totalAmount = totalAmount.add(
                    pu.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
            );
        }

        // Tính VAT
        BigDecimal vatRate = request.getVatRate() != null
                ? request.getVatRate()
                : new BigDecimal("0.02");
        BigDecimal vatAmount = totalAmount.multiply(vatRate)
                .setScale(0, RoundingMode.HALF_UP);

        // Tính giảm giá từ voucher
        BigDecimal discountAmount = BigDecimal.ZERO;
        Voucher voucher = null;
        if (request.getVoucherCode() != null && !request.getVoucherCode().isBlank()) {
            voucher = validateVoucher(request.getVoucherCode(), totalAmount, request.getCustomerId());
            discountAmount = calculateVoucherDiscount(voucher, totalAmount);
        }

        // Tính tiền giảm từ điểm
        BigDecimal pointDiscount = BigDecimal.ZERO;
        int pointsUsed = 0;
        if (request.isUsePoints() && request.getCustomerId() != null) {
            Customer customer = customerRepository.findById(request.getCustomerId()).orElse(null);
            if (customer != null && customer.getStatus() == com.g4fpt.sms.customer.enums.CustomerStatus.ACTIVE) {
                int availablePoints = customer.getTotalPoint() - customer.getUsedPoint();
                BigDecimal amountAfterVat = totalAmount.add(vatAmount).subtract(discountAmount).max(BigDecimal.ZERO);
                int maxPointsCanUse = amountAfterVat.divide(
                        BigDecimal.valueOf(POINT_VALUE), 0, RoundingMode.FLOOR).intValue();
                pointsUsed = Math.min(availablePoints, maxPointsCanUse);
                pointDiscount = BigDecimal.valueOf(pointsUsed * POINT_VALUE);
            }
        }

        // Tính tiền khách phải trả
        BigDecimal finalAmount = totalAmount.add(vatAmount)
                .subtract(discountAmount)
                .subtract(pointDiscount)
                .max(BigDecimal.ZERO);

        // Tính tiền thừa
        BigDecimal paidAmount = request.getPaidAmount() != null
                ? request.getPaidAmount()
                : finalAmount;
        BigDecimal changeAmount = paidAmount.subtract(finalAmount).max(BigDecimal.ZERO);

        // Tạo OrderTransaction
        String orderCode = "ORD-" + java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        OrderTransaction order = OrderTransaction.builder()
                .branchId(request.getBranchId())
                .createdBy(request.getEmployeeId())
                .code(orderCode)
                .totalAmount(totalAmount)
                .discountAmount(discountAmount)
                .finalAmount(finalAmount)
                .paidAmount(paidAmount)
                .changeAmount(changeAmount)
                .pointsUsed(pointsUsed > 0 ? pointsUsed : null)
                .pointDiscount(pointDiscount.compareTo(BigDecimal.ZERO) > 0 ? pointDiscount : null)
                .status("COMPLETED")
                .transactionType("SALE")
                .paymentMethodId(request.getPaymentMethodId())
                .note(request.getNote())
                .build();

        // Gán khách hàng và cập nhật điểm/doanh thu
        if (request.getCustomerId() != null) {
            Customer customer = customerRepository.findById(request.getCustomerId())
                    .orElse(null);
            if (customer != null) {
                if (customer.getStatus() != com.g4fpt.sms.customer.enums.CustomerStatus.ACTIVE) {
                    throw new RuntimeException("Khách hàng này hiện đang ngừng hoạt động hoặc không tồn tại!");
                }
                order.setCustomer(customer);

                // Trừ điểm đã dùng
                if (pointsUsed > 0) {
                    customer.setUsedPoint(customer.getUsedPoint() + pointsUsed);
                }

                // Cộng điểm mới: chỉ tính trên số tiền thực tế khách trả (đã trừ discount & pointDiscount)
                // 10,000đ = 1 điểm
                BigDecimal amountForPointEarning = finalAmount; // đã trừ hết giảm giá
                int pointEarned = amountForPointEarning.divide(
                        new BigDecimal("10000"), 0, RoundingMode.FLOOR).intValue();
                if (pointEarned > 0) {
                    customer.setTotalPoint(customer.getTotalPoint() + pointEarned);
                }
                customer.setTotalRevenue(customer.getTotalRevenue().add(finalAmount));
            }
        }

        // Gán voucher cho đơn hàng
        if (voucher != null) {
            order.setVoucher(voucher);
        }

        // Tạo chi tiết đơn hàng và trừ tồn kho
        for (POSCartItemRequest item : request.getItems()) {
            ProductUnit pu = productUnitRepository.findById(item.getProductUnitId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

            if (pu.getProduct() == null || pu.getProduct().getStatus() != com.g4fpt.sms.product.enums.ProductStatus.ACTIVE) {
                throw new RuntimeException("Sản phẩm '" + (pu.getProduct() != null ? pu.getProduct().getName() : "Không tên") + "' đã ngưng hoạt động hoặc không tồn tại!");
            }

            Long branchId = request.getBranchId();
            Inventory inventory = inventoryRepository.findByBranchIdAndProductUnitId(branchId, pu.getId())
                    .orElseThrow(() -> new RuntimeException("Sản phẩm '" + pu.getProduct().getName()
                            + "' [" + (pu.getUnit() != null ? pu.getUnit().getName() : "")
                            + "] chưa được khai báo tồn kho tại chi nhánh này!"));

            if (inventory.getStock() < item.getQuantity()) {
                throw new RuntimeException("Sản phẩm '" + pu.getProduct().getName()
                        + "' [" + (pu.getUnit() != null ? pu.getUnit().getName() : "")
                        + "] không đủ tồn kho! (Yêu cầu: " + item.getQuantity()
                        + ", Hiện có: " + inventory.getStock() + ")");
            }

            inventory.setStock(inventory.getStock() - item.getQuantity());
            inventoryRepository.save(inventory);

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

        OrderTransaction savedOrder = orderTransactionRepository.save(order);
        if (request.getCustomerId() != null) {
            customerService.updateCustomerRank(request.getCustomerId());
        }
        return savedOrder;
    }

    @Override
    public Voucher validateVoucher(String code, BigDecimal totalAmount, Long customerId) {
        if (customerId == null) {
            throw new RuntimeException("Voucher chỉ áp dụng cho khách hàng thành viên. Vui lòng chọn khách hàng!");
        }

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

        // Kiểm tra điều kiện hạng thẻ của khách hàng
        if (voucher.getCustomerRank() != null) {
            if (customerId == null) {
                throw new RuntimeException("Voucher này chỉ dành cho khách hàng hạng "
                        + voucher.getCustomerRank().getName() + " trở lên!");
            }
            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin khách hàng!"));
            if (customer.getStatus() != com.g4fpt.sms.customer.enums.CustomerStatus.ACTIVE) {
                throw new RuntimeException("Khách hàng này hiện đang ngừng hoạt động!");
            }

            if (customer.getCustomerRank() == null) {
                if (voucher.getCustomerRank().getConditionTotalRevenue().compareTo(BigDecimal.ZERO) > 0) {
                    throw new RuntimeException("Voucher này chỉ dành cho khách hàng hạng "
                            + voucher.getCustomerRank().getName() + " trở lên!");
                }
            } else {
                if (customer.getCustomerRank().getConditionTotalRevenue()
                        .compareTo(voucher.getCustomerRank().getConditionTotalRevenue()) < 0) {
                    throw new RuntimeException("Voucher này chỉ dành cho khách hàng hạng "
                            + voucher.getCustomerRank().getName() + " trở lên!");
                }
            }
        }

        return voucher;
    }

    @Override
    public BigDecimal calculateVoucherDiscount(String code, BigDecimal totalAmount, Long customerId) {
        Voucher v = validateVoucher(code, totalAmount, customerId);
        return calculateVoucherDiscount(v, totalAmount);
    }

    private BigDecimal calculateVoucherDiscount(Voucher voucher, BigDecimal totalAmount) {
        BigDecimal discount;
        if (voucher.getDiscountType().name().equals("PERCENT")) {
            discount = totalAmount.multiply(voucher.getDiscountValue())
                    .divide(new BigDecimal("100"), 0, RoundingMode.HALF_UP);
            if (voucher.getMaxDiscountAmount() != null) {
                discount = discount.min(voucher.getMaxDiscountAmount());
            }
        } else {
            discount = voucher.getDiscountValue();
        }
        return discount.min(totalAmount);
    }
}