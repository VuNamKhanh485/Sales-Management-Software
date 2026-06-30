package com.g4fpt.sms.order.service.impl;

import com.g4fpt.sms.customer.entity.Customer;
import com.g4fpt.sms.customer.repository.CustomerRepository;
import com.g4fpt.sms.customer.service.CustomerService;
import com.g4fpt.sms.order.dto.POSCartItemRequest;
import com.g4fpt.sms.order.dto.POSCheckoutRequest;
import com.g4fpt.sms.order.entity.OrderTransaction;
import com.g4fpt.sms.order.entity.OrderTransactionDetail;
import com.g4fpt.sms.order.repository.OrderTransactionRepository;
import com.g4fpt.sms.product.entity.ProductUnit;
import com.g4fpt.sms.product.repository.ProductUnitRepository;
import com.g4fpt.sms.voucher.entity.Voucher;
import com.g4fpt.sms.voucher.enums.DiscountType;
import com.g4fpt.sms.voucher.enums.VoucherStatus;
import com.g4fpt.sms.voucher.repository.VoucherRepository;
import com.g4fpt.sms.inventory.repository.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderTransactionServiceImplTest {

    @Mock
    private OrderTransactionRepository orderTransactionRepository;

    @Mock
    private ProductUnitRepository productUnitRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private VoucherRepository voucherRepository;

    @Mock
    private CustomerService customerService;

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private OrderTransactionServiceImpl orderService;

    private ProductUnit sampleProductUnit;
    private POSCheckoutRequest checkoutRequest;
    private POSCartItemRequest cartItemRequest;

    @BeforeEach
    void setUp() {
        sampleProductUnit = new ProductUnit();
        sampleProductUnit.setId(1L);
        sampleProductUnit.setPrice(new BigDecimal("100000")); // 100,000 VND
        sampleProductUnit.setSku("PROD-001");
        sampleProductUnit.setBarcodeUnit("1234567890123");
        sampleProductUnit.setConventionValue(1);

        cartItemRequest = new POSCartItemRequest();
        cartItemRequest.setProductUnitId(1L);
        cartItemRequest.setQuantity(2); // Total = 200,000 VND

        checkoutRequest = new POSCheckoutRequest();
        checkoutRequest.setBranchId(1L);
        checkoutRequest.setEmployeeId(10L);
        checkoutRequest.setPaymentMethodId(2L);
        checkoutRequest.setVatRate(new BigDecimal("0.08")); // 8% VAT
        checkoutRequest.setItems(Collections.singletonList(cartItemRequest));

        lenient().when(inventoryRepository.findByBranchIdAndProductUnitId(anyLong(), anyLong()))
                .thenAnswer(invocation -> {
                    com.g4fpt.sms.inventory.entity.Inventory inv = new com.g4fpt.sms.inventory.entity.Inventory();
                    inv.setStock(100);
                    return Optional.of(inv);
                });
    }

    @Test
    void processCheckout_success_noVoucher_noCustomer() {
        // Arrange
        when(productUnitRepository.findById(1L)).thenReturn(Optional.of(sampleProductUnit));
        when(orderTransactionRepository.save(any(OrderTransaction.class))).thenAnswer(invocation -> {
            OrderTransaction order = invocation.getArgument(0);
            order.setId(100L);
            return order;
        });

        // Act
        OrderTransaction result = orderService.processCheckout(checkoutRequest);

        // Assert
        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals(new BigDecimal("200000"), result.getTotalAmount()); // 100k * 2
        assertEquals(BigDecimal.ZERO, result.getDiscountAmount());
        assertEquals(new BigDecimal("216000"), result.getFinalAmount());
        assertEquals(new BigDecimal("216000"), result.getPaidAmount());
        assertEquals(BigDecimal.ZERO, result.getChangeAmount());
        assertEquals("COMPLETED", result.getStatus());
        assertEquals("SALE", result.getTransactionType());
        assertEquals(1, result.getDetails().size());

        OrderTransactionDetail detail = result.getDetails().get(0);
        assertEquals(sampleProductUnit, detail.getProductUnit());
        assertEquals(2, detail.getQuantity());
        assertEquals(new BigDecimal("100000"), detail.getSalePrice());
        assertEquals(new BigDecimal("200000"), detail.getTotalAmount());

        verify(productUnitRepository, times(2)).findById(1L); // Called in calculating total amount and mapping details
        verify(orderTransactionRepository, times(1)).save(any(OrderTransaction.class));
    }

    @Test
    void processCheckout_success_withVoucherPercentage() {
        // Arrange
        checkoutRequest.setCustomerId(1L);
        checkoutRequest.setVoucherCode("PERCENT10");
        checkoutRequest.setPaidAmount(new BigDecimal("250000")); // Given amount

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setTotalPoint(10);
        customer.setTotalRevenue(new BigDecimal("500000"));

        Voucher voucher = Voucher.builder()
                .id(1L)
                .code("PERCENT10")
                .name("Giảm 10%")
                .discountType(DiscountType.PERCENT)
                .discountValue(new BigDecimal("10")) // 10%
                .minOrderAmount(new BigDecimal("100000"))
                .maxDiscountAmount(new BigDecimal("15000")) // Max discount is 15,000 VND
                .startAt(LocalDateTime.now().minusDays(1))
                .endAt(LocalDateTime.now().plusDays(1))
                .status(VoucherStatus.ACTIVE)
                .build();

        when(productUnitRepository.findById(1L)).thenReturn(Optional.of(sampleProductUnit));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(voucherRepository.findByCode("PERCENT10")).thenReturn(Optional.of(voucher));
        when(orderTransactionRepository.save(any(OrderTransaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        OrderTransaction result = orderService.processCheckout(checkoutRequest);

        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("200000"), result.getTotalAmount());
        // Calculation:
        // total = 200,000
        // VAT = 200,000 * 0.08 = 16,000
        // discount = total * 10% = 20,000, capped at maxDiscountAmount (15,000) -> discount = 15,000
        // finalAmount = 200,000 + 16,000 - 15,000 = 201,000
        // paidAmount = 250,000
        // changeAmount = 250,000 - 201,000 = 49,000
        assertEquals(new BigDecimal("15000"), result.getDiscountAmount());
        assertEquals(new BigDecimal("201000"), result.getFinalAmount());
        assertEquals(new BigDecimal("250000"), result.getPaidAmount());
        assertEquals(new BigDecimal("49000"), result.getChangeAmount());
        assertEquals(voucher, result.getVoucher());
    }

    @Test
    void processCheckout_success_withVoucherAmount() {
        // Arrange
        checkoutRequest.setCustomerId(1L);
        checkoutRequest.setVoucherCode("FLAT30K");

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setTotalPoint(10);
        customer.setTotalRevenue(new BigDecimal("500000"));

        Voucher voucher = Voucher.builder()
                .id(2L)
                .code("FLAT30K")
                .name("Giảm 30K")
                .discountType(DiscountType.AMOUNT)
                .discountValue(new BigDecimal("30000"))
                .minOrderAmount(new BigDecimal("150000"))
                .startAt(LocalDateTime.now().minusDays(1))
                .endAt(LocalDateTime.now().plusDays(1))
                .status(VoucherStatus.ACTIVE)
                .build();

        when(productUnitRepository.findById(1L)).thenReturn(Optional.of(sampleProductUnit));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(voucherRepository.findByCode("FLAT30K")).thenReturn(Optional.of(voucher));
        when(orderTransactionRepository.save(any(OrderTransaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        OrderTransaction result = orderService.processCheckout(checkoutRequest);

        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("200000"), result.getTotalAmount());
        // Calculation:
        // total = 200,000
        // VAT = 200,000 * 0.08 = 16,000
        // discount = 30,000
        // finalAmount = 200,000 + 16,000 - 30,000 = 186,000
        assertEquals(new BigDecimal("30000"), result.getDiscountAmount());
        assertEquals(new BigDecimal("186000"), result.getFinalAmount());
    }

    @Test
    void processCheckout_success_withCustomer() {
        // Arrange
        checkoutRequest.setCustomerId(5L);

        Customer customer = new Customer();
        customer.setId(5L);
        customer.setFullName("Nguyen Van A");
        customer.setTotalPoint(10);
        customer.setTotalRevenue(new BigDecimal("500000"));

        when(productUnitRepository.findById(1L)).thenReturn(Optional.of(sampleProductUnit));
        when(customerRepository.findById(5L)).thenReturn(Optional.of(customer));
        when(orderTransactionRepository.save(any(OrderTransaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        OrderTransaction result = orderService.processCheckout(checkoutRequest);

        // Assert
        assertNotNull(result);
        assertEquals(customer, result.getCustomer());

        // Calculation:
        // finalAmount = 216,000
        // Point earned = 216,000 / 10,000 = 21 points
        // New points = 10 + 21 = 31 points
        // New revenue = 500,000 + 216,000 = 716,000
        assertEquals(31, customer.getTotalPoint());
        assertEquals(new BigDecimal("716000"), customer.getTotalRevenue());
    }

    @Test
    void processCheckout_productNotFound() {
        // Arrange
        when(productUnitRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> orderService.processCheckout(checkoutRequest));
        assertEquals("Không tìm thấy sản phẩm", exception.getMessage());
    }

    @Test
    void validateVoucher_notFound() {
        // Arrange
        when(voucherRepository.findByCode("INVALID")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> orderService.validateVoucher("INVALID", new BigDecimal("200000"), 1L));
        assertEquals("Mã voucher không tồn tại!", exception.getMessage());
    }

    @Test
    void validateVoucher_inactive() {
        // Arrange
        Voucher voucher = Voucher.builder()
                .code("INACTIVE10")
                .status(VoucherStatus.INACTIVE)
                .build();
        when(voucherRepository.findByCode("INACTIVE10")).thenReturn(Optional.of(voucher));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> orderService.validateVoucher("INACTIVE10", new BigDecimal("200000"), 1L));
        assertEquals("Voucher đã bị vô hiệu hóa!", exception.getMessage());
    }

    @Test
    void validateVoucher_expired() {
        // Arrange
        Voucher voucher = Voucher.builder()
                .code("EXPIRED10")
                .status(VoucherStatus.ACTIVE)
                .startAt(LocalDateTime.now().minusDays(5))
                .endAt(LocalDateTime.now().minusDays(1)) // Expired 1 day ago
                .build();
        when(voucherRepository.findByCode("EXPIRED10")).thenReturn(Optional.of(voucher));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> orderService.validateVoucher("EXPIRED10", new BigDecimal("200000"), 1L));
        assertEquals("Voucher đã hết hạn hoặc chưa đến ngày sử dụng!", exception.getMessage());
    }

    @Test
    void validateVoucher_minOrderAmountNotMet() {
        // Arrange
        Voucher voucher = Voucher.builder()
                .code("MIN500K")
                .status(VoucherStatus.ACTIVE)
                .startAt(LocalDateTime.now().minusDays(1))
                .endAt(LocalDateTime.now().plusDays(1))
                .minOrderAmount(new BigDecimal("500000"))
                .build();
        when(voucherRepository.findByCode("MIN500K")).thenReturn(Optional.of(voucher));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> orderService.validateVoucher("MIN500K", new BigDecimal("200000"), 1L));
        assertTrue(exception.getMessage().contains("Đơn hàng chưa đạt giá trị tối thiểu"));
    }
}
