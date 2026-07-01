package com.g4fpt.sms.order.controller;

import com.g4fpt.sms.branch.repository.BranchRepository;
import com.g4fpt.sms.customer.entity.Customer;
import com.g4fpt.sms.customer.repository.CustomerRepository;
import com.g4fpt.sms.customer.enums.CustomerStatus;
import com.g4fpt.sms.order.dto.*;
import com.g4fpt.sms.order.entity.OrderTransaction;
import com.g4fpt.sms.order.repository.OrderTransactionRepository;
import com.g4fpt.sms.order.service.OrderTransactionService;
import com.g4fpt.sms.product.entity.Category;
import com.g4fpt.sms.product.entity.ProductUnit;
import com.g4fpt.sms.product.enums.ProductStatus;
import com.g4fpt.sms.product.repository.CategoryRepository;
import com.g4fpt.sms.product.repository.ProductUnitRepository;
import com.g4fpt.sms.voucher.entity.Voucher;
import com.g4fpt.sms.voucher.repository.VoucherRepository;
import com.g4fpt.sms.inventory.entity.Inventory;
import com.g4fpt.sms.inventory.repository.InventoryRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.g4fpt.sms.auth.security.CustomUserDetails;
import java.time.LocalDate;
import java.time.LocalTime;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/pos")
@SessionAttributes("posSession")
@RequiredArgsConstructor
public class PosController {

    private final OrderTransactionService posService;
    private final ProductUnitRepository productUnitRepo;
    private final CategoryRepository categoryRepository;
    private final CustomerRepository customerRepository;
    private final OrderTransactionRepository orderTransactionRepository;
    private final BranchRepository branchRepository;
    private final VoucherRepository voucherRepository;
    private final InventoryRepository inventoryRepository;

    @ModelAttribute("posSession")
    public PosSessionData setupSession() {
        return new PosSessionData();
    }

    // =============================================
    // 1. Màn hình POS chính
    // =============================================
    @GetMapping
    public String showPosScreen(
            @ModelAttribute("posSession") PosSessionData session,
            @RequestParam(required = false) Long successOrderId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model) {

        if (session.getActiveBranchId() == null && userDetails != null) {
            Long defaultBranchId = userDetails.getBranchId();
            session.setActiveBranchId(defaultBranchId != null ? defaultBranchId : 1L);
        }

        if (session.getActiveBranchId() != null) {
            branchRepository.findById(session.getActiveBranchId()).ifPresent(branch -> {
                model.addAttribute("activeBranch", branch);
            });
        }

        boolean isOwner = userDetails != null && userDetails.hasRole("OWNER");
        model.addAttribute("isOwner", isOwner);
        if (isOwner) {
            model.addAttribute("branches", branchRepository.findAll());
        }

        // Đổi tên từ "session" thành "posData"
        model.addAttribute("posData", session);
        model.addAttribute("cart", session.getActiveCart());

        List<Category> categories = categoryRepository.findAll().stream()
                .filter(c -> c.getStatus() == com.g4fpt.sms.product.enums.CategoryStatus.ACTIVE)
                .collect(Collectors.toList());
        model.addAttribute("categories", categories);

        if (successOrderId != null) {
            orderTransactionRepository.findById(successOrderId).ifPresent(order -> {
                model.addAttribute("successOrder", order);
                if (order.getBranchId() != null) {
                    branchRepository.findById(order.getBranchId()).ifPresent(branch -> {
                        model.addAttribute("successBranch", branch);
                    });
                }
            });
        }

        populateDropdownUnits(session, model);
        return "order/pos";
    }

    // =============================================
    // 2. Thêm đơn mới
    // =============================================
    @GetMapping("/new-order")
    public String newOrder(
            @ModelAttribute("posSession") PosSessionData session,
            RedirectAttributes ra) {

        if (!session.canAddOrder()) {
            ra.addFlashAttribute("error", "Tối đa 5 đơn hàng cùng lúc!");
        } else {
            session.addNewOrder();
        }
        return "redirect:/pos";
    }

    // =============================================
    // 3. Chuyển đơn
    // =============================================
    @GetMapping("/switch/{index}")
    public String switchOrder(
            @PathVariable int index,
            @ModelAttribute("posSession") PosSessionData session) {

        session.switchOrder(index);
        return "redirect:/pos";
    }

    @GetMapping("/change-branch")
    public String changeBranch(
            @RequestParam Long branchId,
            @ModelAttribute("posSession") PosSessionData session,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails != null && userDetails.hasRole("OWNER")) {
            session.setActiveBranchId(branchId);
        }
        return "redirect:/pos";
    }

    // =============================================
    // 4. Đóng đơn
    // =============================================
    @GetMapping("/close/{index}")
    public String closeOrder(
            @PathVariable int index,
            @ModelAttribute("posSession") PosSessionData session) {

        session.removeOrder(index);
        return "redirect:/pos";
    }

    // =============================================
    // 5. Thêm sản phẩm bằng SKU/barcode
    // =============================================
    @GetMapping("/add")
    public String addToCart(
            @RequestParam String keyword,
            @ModelAttribute("posSession") PosSessionData session,
            RedirectAttributes ra) {

        String kw = keyword.trim();
        ProductUnit pu = productUnitRepo.findBySku(kw)
                .orElseGet(() -> productUnitRepo.findByBarcodeUnit(kw).orElse(null));

        if (pu != null && pu.getProduct() != null && pu.getProduct().getStatus() == ProductStatus.ACTIVE) {
            addProductToCart(session.getActiveCart(), pu);
        } else {
            ra.addFlashAttribute("error", "Không tìm thấy sản phẩm hoặc sản phẩm đã ngưng hoạt động: " + kw);
        }
        return "redirect:/pos";
    }

    // =============================================
    // 6. Thêm sản phẩm bằng ID (từ modal)
    // =============================================
    @GetMapping("/add-by-id")
    public String addToCartById(
            @RequestParam Long productUnitId,
            @ModelAttribute("posSession") PosSessionData session,
            RedirectAttributes ra) {

        ProductUnit pu = productUnitRepo.findById(productUnitId).orElse(null);
        if (pu != null && pu.getProduct() != null && pu.getProduct().getStatus() == ProductStatus.ACTIVE) {
            addProductToCart(session.getActiveCart(), pu);
        } else {
            ra.addFlashAttribute("error", "Không tìm thấy sản phẩm hoặc sản phẩm đã ngưng hoạt động!");
        }
        return "redirect:/pos";
    }

    // =============================================
    // 7. Cập nhật số lượng
    // =============================================
    @GetMapping("/update-qty")
    public String updateQuantity(
            @RequestParam int index,
            @RequestParam int quantity,
            @ModelAttribute("posSession") PosSessionData session) {

        PosCart cart = session.getActiveCart();
        if (index >= 0 && index < cart.getItems().size()) {
            if (quantity <= 0) {
                cart.getItems().remove(index);
            } else {
                cart.getItems().get(index).setQuantity(quantity);
            }
        }
        return "redirect:/pos";
    }

    @GetMapping("/update-unit")
    public String updateUnit(
            @RequestParam int index,
            @RequestParam Long productUnitId,
            @ModelAttribute("posSession") PosSessionData session) {

        PosCart cart = session.getActiveCart();
        if (index >= 0 && index < cart.getItems().size()) {
            ProductUnit pu = productUnitRepo.findById(productUnitId).orElse(null);
            if (pu != null) {
                PosCartItem item = cart.getItems().get(index);
                item.setProductUnitId(pu.getId());
                item.setSku(pu.getSku());
                item.setPrice(pu.getPrice());
                item.setUnitName(pu.getUnit() != null ? pu.getUnit().getName() : "");
            }
        }
        return "redirect:/pos";
    }

    // =============================================
    // 8. Xóa sản phẩm
    // =============================================
    @GetMapping("/remove")
    public String removeCartItem(
            @RequestParam int index,
            @ModelAttribute("posSession") PosSessionData session) {

        PosCart cart = session.getActiveCart();
        if (index >= 0 && index < cart.getItems().size()) {
            cart.getItems().remove(index);
        }
        return "redirect:/pos";
    }

    // =============================================
    // 9. Xóa hết giỏ
    // =============================================
    @GetMapping("/clear")
    public String clearCart(@ModelAttribute("posSession") PosSessionData session) {
        PosCart cart = session.getActiveCart();
        cart.getItems().clear();
        cart.setCustomerId(null);
        cart.setCustomerName(null);
        cart.setCustomerPhone(null);
        cart.setVoucherCode(null);
        cart.setVoucherDiscount(BigDecimal.ZERO);
        cart.setGivenAmount(BigDecimal.ZERO);
        return "redirect:/pos";
    }

    @GetMapping("/search-customer")
    public String findCustomers(
            @RequestParam String phone,
            @ModelAttribute("posSession") PosSessionData session,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model) {

        String keyword = phone.trim();
        if (keyword.isEmpty()) {
            return "redirect:/pos";
        }

        // Tìm kiếm khách hàng
        org.springframework.data.domain.Page<Customer> page = customerRepository
                .searchActiveByPhoneOrName(
                        CustomerStatus.ACTIVE.name(), keyword,
                        org.springframework.data.domain.PageRequest.of(0, 5));

        List<Customer> result = page.getContent();
        if (result.isEmpty()) {
            model.addAttribute("customerSearchError", "Không tìm thấy khách hàng");
        } else {
            // Force init lazy-loaded customerRank to avoid LazyInitializationException
            result.forEach(c -> {
                if (c.getCustomerRank() != null) {
                    c.getCustomerRank().getName();
                }
            });
            model.addAttribute("customerSearchResults", result);
        }
        model.addAttribute("searchedPhone", phone);

        // === Setup các model attributes giống showPosScreen ===
        if (session.getActiveBranchId() == null && userDetails != null) {
            Long defaultBranchId = userDetails.getBranchId();
            session.setActiveBranchId(defaultBranchId != null ? defaultBranchId : 1L);
        }
        if (session.getActiveBranchId() != null) {
            branchRepository.findById(session.getActiveBranchId()).ifPresent(branch -> {
                model.addAttribute("activeBranch", branch);
            });
        }
        boolean isOwner = userDetails != null && userDetails.hasRole("OWNER");
        model.addAttribute("isOwner", isOwner);
        if (isOwner) {
            model.addAttribute("branches", branchRepository.findAll());
        }
        model.addAttribute("posData", session);
        model.addAttribute("cart", session.getActiveCart());
        List<Category> categories = categoryRepository.findAll().stream()
                .filter(c -> c.getStatus() == com.g4fpt.sms.product.enums.CategoryStatus.ACTIVE)
                .collect(Collectors.toList());
        model.addAttribute("categories", categories);

        populateDropdownUnits(session, model);
        return "order/pos";
    }

    @GetMapping("/sales-history")
    public String getSalesHistory(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) String date,
            Model model) {

        if (userDetails == null) {
            return "redirect:/login";
        }

        boolean isOwner = userDetails.hasRole("OWNER");
        LocalDate localDate = (date != null && !date.isBlank())
                ? LocalDate.parse(date)
                : LocalDate.now();

        LocalDateTime startOfDay = localDate.atStartOfDay();
        LocalDateTime endOfDay = localDate.atTime(LocalTime.MAX);

        List<OrderTransaction> orders;
        if (isOwner) {
            orders = orderTransactionRepository.findByDateRange(startOfDay, endOfDay);
        } else {
            orders = orderTransactionRepository.findByCreatedByAndCreatedAtBetweenOrderByCreatedAtDesc(
                    userDetails.getEmployee().getId(), startOfDay, endOfDay);
        }

        Map<Long, String> branchNames = new HashMap<>();
        orders.forEach(order -> {
            if (order.getBranchId() != null) {
                branchNames.computeIfAbsent(order.getBranchId(),
                        id -> branchRepository.findById(id).map(b -> b.getName()).orElse("SMS STORE"));
            }
        });

        model.addAttribute("isOwner", isOwner);
        model.addAttribute("orders", orders);
        model.addAttribute("branchNames", branchNames);
        model.addAttribute("filterDate", localDate.toString());

        return "order/pos-history";
    }

    @GetMapping("/sales-history/{id}")
    public String getOrderDetail(@PathVariable Long id, Model model) {
        OrderTransaction order = orderTransactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        model.addAttribute("order", order);

        String branchName = "SMS STORE";
        String branchAddress = "123 Đường Láng, Đống Đa, Hà Nội";
        String branchPhone = "0987 654 321";
        if (order.getBranchId() != null) {
            var branchOpt = branchRepository.findById(order.getBranchId());
            if (branchOpt.isPresent()) {
                branchName = branchOpt.get().getName();
                branchAddress = branchOpt.get().getAddress();
                branchPhone = branchOpt.get().getPhone();
            }
        }
        model.addAttribute("branchName", branchName);
        model.addAttribute("branchAddress", branchAddress);
        model.addAttribute("branchPhone", branchPhone);

        return "order/pos-history-detail";
    }

    // =============================================
    // 11. Gán khách hàng
    // =============================================
    @GetMapping("/set-customer")
    public String setCustomer(
            @RequestParam Long customerId,
            @RequestParam String customerName,
            @RequestParam String customerPhone,
            @ModelAttribute("posSession") PosSessionData session,
            RedirectAttributes ra) {

        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null || customer.getStatus() != com.g4fpt.sms.customer.enums.CustomerStatus.ACTIVE) {
            ra.addFlashAttribute("error", "Khách hàng này hiện đang ngừng hoạt động hoặc không tồn tại!");
            return "redirect:/pos";
        }

        PosCart cart = session.getActiveCart();
        cart.setCustomerId(customerId);
        cart.setCustomerName(customerName);
        cart.setCustomerPhone(customerPhone);
        return "redirect:/pos";
    }

    // =============================================
    // 12. Xóa khách hàng
    // =============================================
    @GetMapping("/remove-customer")
    public String removeCustomer(@ModelAttribute("posSession") PosSessionData session) {
        PosCart cart = session.getActiveCart();
        cart.setCustomerId(null);
        cart.setCustomerName(null);
        cart.setCustomerPhone(null);
        return "redirect:/pos";
    }

    // =============================================
    // 13. API kiểm tra voucher
    // =============================================
    @PostMapping("/apply-voucher")
    public String applyVoucher(
            @RequestParam String code,
            @ModelAttribute("posSession") PosSessionData session,
            RedirectAttributes ra) {

        PosCart cart = session.getActiveCart();
        try {
            Voucher voucher = posService.validateVoucher(code, cart.getTotalAmount(), cart.getCustomerId());
            BigDecimal discount;
            if (voucher.getDiscountType().name().equals("PERCENT")) {
                discount = cart.getTotalAmount()
                        .multiply(voucher.getDiscountValue())
                        .divide(new BigDecimal("100"));
                if (voucher.getMaxDiscountAmount() != null) {
                    discount = discount.min(voucher.getMaxDiscountAmount());
                }
            } else {
                discount = voucher.getDiscountValue();
            }
            cart.setVoucherCode(code);
            cart.setVoucherDiscount(discount);
            ra.addFlashAttribute("voucherSuccess", "Áp dụng thành công! Giảm " + discount + "đ");
        } catch (Exception e) {
            ra.addFlashAttribute("voucherError", e.getMessage());
        }
        return "redirect:/pos";
    }

    // =============================================
    // 14. Xóa voucher
    // =============================================
    @GetMapping("/remove-voucher")
    public String removeVoucher(@ModelAttribute("posSession") PosSessionData session) {
        PosCart cart = session.getActiveCart();
        cart.setVoucherCode(null);
        cart.setVoucherDiscount(BigDecimal.ZERO);
        return "redirect:/pos";
    }

    // =============================================
    // 15. API lấy sản phẩm theo category (cho modal)
    // =============================================
    @GetMapping("/product-list")
    public String getProductList(
            @ModelAttribute("posSession") PosSessionData session,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            Model model) {

        List<ProductUnit> all = productUnitRepo.findAll().stream()
                .filter(pu -> pu.getProduct() != null
                        && pu.getProduct().getStatus() == ProductStatus.ACTIVE
                        && pu.getProduct().getCategory() != null
                        && pu.getProduct().getCategory().getStatus() == com.g4fpt.sms.product.enums.CategoryStatus.ACTIVE)
                .collect(Collectors.toList());

        if (keyword != null && !keyword.isBlank()) {
            String kw = keyword.trim().toLowerCase();
            all = all.stream()
                    .filter(pu -> pu.getProduct().getName().toLowerCase().contains(kw)
                            || pu.getSku().toLowerCase().contains(kw))
                    .collect(Collectors.toList());
        } else if (categoryId != null) {
            all = all.stream()
                    .filter(pu -> pu.getProduct().getCategory() != null
                            && pu.getProduct().getCategory().getId().equals(categoryId))
                    .collect(Collectors.toList());
        }

        // Lấy tồn kho thực tế cho từng sản phẩm tại chi nhánh đang chọn
        Long branchId = session.getActiveBranchId();
        Map<Long, Integer> stockMap = new HashMap<>();
        if (branchId != null) {
            for (ProductUnit pu : all) {
                int stock = inventoryRepository.findByBranchIdAndProductUnitId(branchId, pu.getId())
                        .map(Inventory::getStock)
                        .orElse(0);
                stockMap.put(pu.getId(), stock);
            }
        }

        model.addAttribute("products", all);
        model.addAttribute("stockMap", stockMap);
        
        List<Category> categories = categoryRepository.findAll().stream()
                .filter(c -> c.getStatus() == com.g4fpt.sms.product.enums.CategoryStatus.ACTIVE)
                .collect(Collectors.toList());
        model.addAttribute("categories", categories);
        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryId", categoryId);
        return "order/pos-product-list";
    }

    // =============================================
    // 15b. API lấy danh sách voucher khả dụng theo khách hàng
    // =============================================
    @GetMapping("/voucher-list")
    public String getAvailableVouchers(
            @ModelAttribute("posSession") PosSessionData session,
            Model model) {

        Long customerId = session.getActiveCart().getCustomerId();
        if (customerId == null) {
            model.addAttribute("vouchers", new java.util.ArrayList<>());
            return "order/pos-voucher-list";
        }

        LocalDateTime now = LocalDateTime.now();
        List<Voucher> activeVouchers = voucherRepository.findAll().stream()
                .filter(v -> v.getStatus() == com.g4fpt.sms.voucher.enums.VoucherStatus.ACTIVE)
                .filter(v -> v.getStartAt().isBefore(now) && v.getEndAt().isAfter(now))
                .collect(Collectors.toList());

        Customer customer = customerRepository.findById(customerId).orElse(null);
        final Customer finalCust = customer;
        
        List<Voucher> result = activeVouchers.stream()
                .filter(v -> {
                    if (v.getCustomerRank() == null) return true;
                    if (finalCust == null || finalCust.getCustomerRank() == null) {
                        return v.getCustomerRank().getConditionTotalRevenue().compareTo(BigDecimal.ZERO) == 0;
                    }
                    return finalCust.getCustomerRank().getConditionTotalRevenue()
                            .compareTo(v.getCustomerRank().getConditionTotalRevenue()) >= 0;
                })
                .collect(Collectors.toList());

        model.addAttribute("vouchers", result);
        return "order/pos-voucher-list";
    }

    // =============================================
    // 16. Thanh toán
    // =============================================
    @PostMapping("/checkout")
    public String checkoutOrder(
            @ModelAttribute("posSession") PosSessionData session,
            @RequestParam(required = false) String note,
            @RequestParam Long paymentMethodId,
            @RequestParam BigDecimal givenAmount,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes ra) {

        PosCart cart = session.getActiveCart();
        if (cart.getItems().isEmpty()) {
            ra.addFlashAttribute("error", "Giỏ hàng trống!");
            return "redirect:/pos";
        }

        try {
            POSCheckoutRequest request = new POSCheckoutRequest();
            if (userDetails != null && userDetails.getEmployee() != null) {
                request.setEmployeeId(userDetails.getEmployee().getId());
                Long branchId = session.getActiveBranchId();
                if (branchId == null) {
                    branchId = userDetails.getBranchId();
                }
                request.setBranchId(branchId != null ? branchId : 1L);
            } else {
                request.setEmployeeId(1L);
                request.setBranchId(session.getActiveBranchId() != null ? session.getActiveBranchId() : 1L);
            }
            request.setCustomerId(cart.getCustomerId());
            request.setVoucherCode(cart.getVoucherCode());
            request.setPaymentMethodId(paymentMethodId);
            request.setPaidAmount(givenAmount);
            request.setVatRate(cart.getVatRate());
            request.setNote(note);
            request.setItems(cart.getItems().stream().map(item -> {
                POSCartItemRequest req = new POSCartItemRequest();
                req.setProductUnitId(item.getProductUnitId());
                req.setQuantity(item.getQuantity());
                return req;
            }).collect(Collectors.toList()));

            OrderTransaction savedOrder = posService.processCheckout(request);

            // Đóng đơn hiện tại sau khi thanh toán
            session.removeOrder(session.getActiveIndex());

            ra.addFlashAttribute("success",
                    "Thanh toán thành công! Mã đơn: " + savedOrder.getCode());
            return "redirect:/pos?successOrderId=" + savedOrder.getId();

        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/pos";
        }
    }

    // =============================================
    // Helper
    // =============================================
    private void addProductToCart(PosCart cart, ProductUnit pu) {
        PosCartItem existing = cart.getItems().stream()
                .filter(i -> i.getProductUnitId().equals(pu.getId()))
                .findFirst().orElse(null);

        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + 1);
        } else {
            PosCartItem newItem = new PosCartItem();
            newItem.setProductUnitId(pu.getId());
            newItem.setSku(pu.getSku());
            newItem.setName(pu.getProduct().getName());
            newItem.setPrice(pu.getPrice());
            newItem.setQuantity(1);
            newItem.setUnitName(pu.getUnit() != null ? pu.getUnit().getName() : "");
            newItem.setImageUrl(pu.getProduct().getImageUrl());
            cart.getItems().add(newItem);
        }
    }

    private void populateDropdownUnits(PosSessionData session, Model model) {
        Map<Long, List<ProductUnit>> cartItemUnitsMap = new HashMap<>();
        for (PosCartItem item : session.getActiveCart().getItems()) {
            productUnitRepo.findById(item.getProductUnitId()).ifPresent(pu -> {
                if (pu.getProduct() != null) {
                    List<ProductUnit> units = productUnitRepo.findByProductIdWithUnit(pu.getProduct().getId());
                    cartItemUnitsMap.put(item.getProductUnitId(), units);
                }
            });
        }
        model.addAttribute("cartItemUnitsMap", cartItemUnitsMap);
    }
}