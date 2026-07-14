package com.g4fpt.sms.order.controller;

import com.g4fpt.sms.order.dto.*;
import com.g4fpt.sms.order.entity.OrderTransaction;
import com.g4fpt.sms.order.repository.OrderTransactionRepository;
import com.g4fpt.sms.order.service.OrderTransactionService;
import com.g4fpt.sms.product.entity.ProductUnit;
import com.g4fpt.sms.product.repository.ProductUnitRepository;
import com.g4fpt.sms.voucher.repository.VoucherRepository;
import com.g4fpt.sms.inventory.repository.InventoryRepository;
import com.g4fpt.sms.customer.repository.CustomerRepository;
import com.g4fpt.sms.branch.repository.BranchRepository;
import com.g4fpt.sms.auth.security.CustomUserDetails;
import com.g4fpt.sms.product.enums.ProductStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/pos")
@SessionAttributes("posSession")
@RequiredArgsConstructor
public class PosController {

    private final OrderTransactionService posService;
    private final ProductUnitRepository productUnitRepo;
    private final com.g4fpt.sms.product.repository.CategoryRepository categoryRepository;
    private final CustomerRepository customerRepository;
    private final OrderTransactionRepository orderRepo;
    private final BranchRepository branchRepo;
    private final com.g4fpt.sms.voucher.repository.VoucherRepository voucherRepository;
    private final InventoryRepository inventoryRepository;

    @ModelAttribute("posSession")
    public PosSessionData setupSession() {
        return new PosSessionData();
    }

    // ============== Main ==============
    @GetMapping
    public String showPosScreen(@ModelAttribute("posSession") PosSessionData session,
            @RequestParam(required = false) Long successOrderId,
            @AuthenticationPrincipal CustomUserDetails user, Model model) {
        initSessionBranch(session, user);
        buildPosModel(session, user, model);
        if (successOrderId != null)
            orderRepo.findById(successOrderId).ifPresent(order -> {
                model.addAttribute("successOrder", order);
                if (order.getBranchId() != null)
                    branchRepo.findById(order.getBranchId()).ifPresent(b -> model.addAttribute("successBranch", b));
            });
        return "order/pos";
    }

    // ---------- Order tabs ----------
    @GetMapping("/new-order")
    public String newOrder(@ModelAttribute("posSession") PosSessionData s, RedirectAttributes ra) {
        if (!s.canAddOrder())
            ra.addFlashAttribute("error", "Tối đa 5 đơn hàng cùng lúc!");
        else
            s.addNewOrder();
        return "redirect:/pos";
    }

    @GetMapping("/switch/{index}")
    public String switchOrder(@PathVariable int index, @ModelAttribute("posSession") PosSessionData s) {
        s.switchOrder(index);
        return "redirect:/pos";
    }

    @GetMapping("/close/{index}")
    public String closeOrder(@PathVariable int index, @ModelAttribute("posSession") PosSessionData s) {
        s.removeOrder(index);
        return "redirect:/pos";
    }

    @GetMapping("/clear")
    public String clearCart(@ModelAttribute("posSession") PosSessionData s) {
        var cart = s.getActiveCart();
        cart.getItems().clear();
        cart.setCustomerId(null);
        cart.setCustomerName(null);
        cart.setCustomerPhone(null);
        cart.setCustomerAvailablePoints(0);
        cart.setUsePoints(false);
        cart.setVoucherCode(null);
        cart.setVoucherDiscount(BigDecimal.ZERO);
        cart.setGivenAmount(BigDecimal.ZERO);
        return "redirect:/pos";
    }

    // ---------- Cart (add/update/remove items) ----------
    @GetMapping("/add")
    public String addToCart(@RequestParam("keyword") String keyword, @ModelAttribute("posSession") PosSessionData s,
            RedirectAttributes ra) {
        String kw = keyword.trim();
        ProductUnit pu = productUnitRepo.findBySku(kw)
                .orElseGet(() -> productUnitRepo.findByBarcodeUnit(kw).orElse(null));
        if (pu != null && pu.getProduct() != null && pu.getProduct().getStatus() == ProductStatus.ACTIVE)
            addProductToCart(s.getActiveCart(), pu);
        else
            ra.addFlashAttribute("error", "Không tìm thấy sản phẩm hoặc sản phẩm đã ngưng hoạt động: " + kw);
        return "redirect:/pos";
    }

    @GetMapping("/add-by-id")
    public String addToCartById(@RequestParam Long productUnitId,
            @ModelAttribute("posSession") PosSessionData s, RedirectAttributes ra) {
        ProductUnit pu = productUnitRepo.findById(productUnitId).orElse(null);
        if (pu != null && pu.getProduct() != null && pu.getProduct().getStatus() == ProductStatus.ACTIVE) {
            addProductToCart(s.getActiveCart(), pu);
            ra.addFlashAttribute("success", "Đã thêm sản phẩm \"" + pu.getProduct().getName() + "\" vào giỏ hàng!");
        } else
            ra.addFlashAttribute("error", "Không tìm thấy sản phẩm hoặc sản phẩm đã ngưng hoạt động!");
        return "redirect:/pos";
    }

    @GetMapping("/update-qty")
    public String updateQuantity(@RequestParam int index, @RequestParam int quantity,
            @ModelAttribute("posSession") PosSessionData s) {
        var items = s.getActiveCart().getItems();
        if (index >= 0 && index < items.size()) {
            if (quantity <= 0)
                items.remove(index);
            else
                items.get(index).setQuantity(quantity);
        }
        return "redirect:/pos";
    }

    @GetMapping("/update-unit")
    public String updateUnit(@RequestParam int index, @RequestParam Long productUnitId,
            @ModelAttribute("posSession") PosSessionData s) {
        var items = s.getActiveCart().getItems();
        if (index >= 0 && index < items.size())
            productUnitRepo.findById(productUnitId).ifPresent(pu -> {
                var item = items.get(index);
                item.setProductUnitId(pu.getId());
                item.setSku(pu.getSku());
                item.setPrice(pu.getPrice());
                item.setUnitName(pu.getUnit() != null ? pu.getUnit().getName() : "");
            });
        return "redirect:/pos";
    }

    @GetMapping("/remove")
    public String removeCartItem(@RequestParam int index, @ModelAttribute("posSession") PosSessionData s) {
        var items = s.getActiveCart().getItems();
        if (index >= 0 && index < items.size())
            items.remove(index);
        return "redirect:/pos";
    }

    // ---------- Customer ----------
    @GetMapping("/search-customer")
    public String findCustomers(@RequestParam String phone, @ModelAttribute("posSession") PosSessionData session,
            @AuthenticationPrincipal CustomUserDetails user, Model model) {
        String kw = phone.trim().replaceAll("\\s+", " ");
        if (!kw.isEmpty()) {
            var result = customerRepository
                    .searchActiveByPhoneOrName(kw, org.springframework.data.domain.PageRequest.of(0, 5)).getContent();
            if (result.isEmpty())
                model.addAttribute("customerSearchError", "Không tìm thấy khách hàng");
            else
                model.addAttribute("customerSearchResults", result);
        }
        model.addAttribute("searchedPhone", phone);
        initSessionBranch(session, user);
        buildPosModel(session, user, model);
        return "order/pos";
    }

    @GetMapping("/set-customer")
    public String setCustomer(@RequestParam Long customerId, @RequestParam String customerName,
            @RequestParam String customerPhone, @ModelAttribute("posSession") PosSessionData s,
            RedirectAttributes ra) {
        var c = customerRepository.findById(customerId).orElse(null);
        if (c == null || c.getStatus() != com.g4fpt.sms.customer.enums.CustomerStatus.ACTIVE) {
            ra.addFlashAttribute("error", "Khách hàng này hiện đang ngừng hoạt động hoặc không tồn tại!");
            return "redirect:/pos";
        }
        var cart = s.getActiveCart();
        cart.setCustomerId(customerId);
        cart.setCustomerName(customerName);
        cart.setCustomerPhone(customerPhone);
        // Tổng điểm khả dụng = totalPoint - usedPoint
        int availablePoints = c.getTotalPoint() - c.getUsedPoint();
        cart.setCustomerAvailablePoints(Math.max(0, availablePoints));
        cart.setUsePoints(false);
        return "redirect:/pos";
    }

    @GetMapping("/remove-customer")
    public String removeCustomer(@ModelAttribute("posSession") PosSessionData s) {
        var cart = s.getActiveCart();
        cart.setCustomerId(null);
        cart.setCustomerName(null);
        cart.setCustomerPhone(null);
        cart.setCustomerAvailablePoints(0);
        cart.setUsePoints(false);
        return "redirect:/pos";
    }

    // ---------- Voucher ----------
    @GetMapping("/apply-voucher")
    public String applyVoucher(@RequestParam String code, @ModelAttribute("posSession") PosSessionData s,
            RedirectAttributes ra) {
        var cart = s.getActiveCart();
        try {
            var discount = posService.calculateVoucherDiscount(code, cart.getTotalAmount(), cart.getCustomerId());
            cart.setVoucherCode(code);
            cart.setVoucherDiscount(discount);
            ra.addFlashAttribute("voucherSuccess", "Áp dụng thành công! Giảm " + discount + "đ");
        } catch (Exception e) {
            ra.addFlashAttribute("voucherError", e.getMessage());
        }
        return "redirect:/pos";
    }

    @GetMapping("/remove-voucher")
    public String removeVoucher(@ModelAttribute("posSession") PosSessionData s) {
        var cart = s.getActiveCart();
        cart.setVoucherCode(null);
        cart.setVoucherDiscount(BigDecimal.ZERO);
        return "redirect:/pos";
    }

    // ---------- Points ----------
    @GetMapping("/toggle-points")
    public String togglePoints(@ModelAttribute("posSession") PosSessionData s,
            @RequestParam boolean enabled, RedirectAttributes ra) {
        var cart = s.getActiveCart();
        if (cart.getCustomerId() == null) {
            ra.addFlashAttribute("error", "Vui lòng chọn khách hàng trước khi áp dụng điểm!");
            return "redirect:/pos";
        }
        cart.setUsePoints(enabled);
        return "redirect:/pos";
    }

    // ---------- Branch ----------
    @GetMapping("/change-branch")
    public String changeBranch(@RequestParam Long branchId, @ModelAttribute("posSession") PosSessionData s,
            @AuthenticationPrincipal CustomUserDetails user) {
        if (user != null && user.hasRole("OWNER"))
            s.setActiveBranchId(branchId);
        return "redirect:/pos";
    }

    // ---------- Product list (iframe modal) ----------
    @GetMapping("/product-list")
    public String getProductList(@ModelAttribute("posSession") PosSessionData s,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword, Model model) {
        String kw = (keyword != null && !keyword.isBlank()) ? keyword.trim() : null;
        var products = productUnitRepo.searchActiveProductUnits(categoryId, kw);
        Long branchId = s.getActiveBranchId();
        if (branchId == null) {
            branchId = branchRepo.findAll().stream().findFirst()
                    .map(com.g4fpt.sms.branch.entity.Branch::getId).orElse(null);
            s.setActiveBranchId(branchId);
        }
        Map<Long, Integer> stockMap = new HashMap<>();
        if (branchId != null) {
            final Long bId = branchId;
            products.forEach(pu -> stockMap.put(pu.getId(),
                    inventoryRepository.findByBranchIdAndProductUnitId(bId, pu.getId())
                            .map(inv -> inv.getStock()).orElse(0)));
        }
        model.addAttribute("products", products);
        model.addAttribute("stockMap", stockMap);
        model.addAttribute("categories", categoryRepository.findAll().stream()
                .filter(c -> c.getStatus() == com.g4fpt.sms.product.enums.CategoryStatus.ACTIVE).toList());
        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryId", categoryId);
        return "order/pos-product-list";
    }

    // ---------- Voucher list (iframe modal) ----------
    @GetMapping("/voucher-list")
    public String getAvailableVouchers(@ModelAttribute("posSession") PosSessionData s, Model model) {
        Long customerId = s.getActiveCart().getCustomerId();
        if (customerId == null) {
            model.addAttribute("vouchers", List.of());
            return "order/pos-voucher-list";
        }
        var now = java.time.LocalDateTime.now();
        var customer = customerRepository.findById(customerId).orElse(null);
        var vouchers = voucherRepository.findAll().stream()
                .filter(v -> v.getStatus() == com.g4fpt.sms.voucher.enums.VoucherStatus.ACTIVE
                        && v.getStartAt().isBefore(now) && v.getEndAt().isAfter(now))
                .filter(v -> v.getCustomerRank() == null
                        || (customer != null && customer.getCustomerRank() != null
                                && customer.getCustomerRank().getConditionTotalRevenue()
                                        .compareTo(v.getCustomerRank().getConditionTotalRevenue()) >= 0))
                .toList();
        model.addAttribute("vouchers", vouchers);
        return "order/pos-voucher-list";
    }

    // ---------- Sales history ----------
    @GetMapping("/sales-history")
    public String getSalesHistory(@AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(required = false) String date, Model model) {
        if (user == null)
            return "redirect:/login";
        boolean isOwner = user.hasRole("OWNER");
        var ld = (date != null && !date.isBlank()) ? java.time.LocalDate.parse(date) : java.time.LocalDate.now();
        var orders = isOwner
                ? orderRepo.findByDateRange(ld.atStartOfDay(), ld.atTime(java.time.LocalTime.MAX))
                : orderRepo.findByCreatedByAndDateRange(
                        user.getEmployee().getId(), ld.atStartOfDay(), ld.atTime(java.time.LocalTime.MAX));
        var branchIds = orders.stream().map(com.g4fpt.sms.order.entity.OrderTransaction::getBranchId)
                .filter(Objects::nonNull).collect(Collectors.toSet());
        var branchNames = branchRepo.findAllById(branchIds).stream()
                .collect(Collectors.toMap(com.g4fpt.sms.branch.entity.Branch::getId,
                        com.g4fpt.sms.branch.entity.Branch::getName));
        model.addAttribute("isOwner", isOwner);
        model.addAttribute("orders", orders);
        model.addAttribute("branchNames", branchNames);
        model.addAttribute("filterDate", ld.toString());
        return "order/pos-history";
    }

    @GetMapping("/sales-history/{id}")
    public String getOrderDetail(@PathVariable Long id, Model model) {
        var order = orderRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
        model.addAttribute("order", order);
        var branch = order.getBranchId() != null
                ? branchRepo.findById(order.getBranchId()).orElse(null)
                : null;
        model.addAttribute("branchName", branch != null ? branch.getName() : "");
        model.addAttribute("branchAddress", branch != null ? branch.getAddress() : "");
        model.addAttribute("branchPhone", branch != null ? branch.getPhone() : "");
        return "order/pos-history-detail";
    }

    // ---------- Checkout ----------
    @PostMapping("/checkout")
    public String checkout(@ModelAttribute("posSession") PosSessionData s,
            @RequestParam(required = false) String note,
            @RequestParam Long paymentMethodId,
            @RequestParam BigDecimal givenAmount,
            @AuthenticationPrincipal CustomUserDetails user,
            RedirectAttributes ra) {
        var cart = s.getActiveCart();
        if (cart.getItems().isEmpty()) {
            ra.addFlashAttribute("error", "Giỏ hàng trống!");
            return "redirect:/pos";
        }
        try {
            var req = buildCheckoutRequest(s, paymentMethodId, givenAmount, note, user);
            var saved = posService.processCheckout(req);
            s.removeOrder(s.getActiveIndex());
            ra.addFlashAttribute("success", "Thanh toán thành công! Mã đơn: " + saved.getCode());
            return "redirect:/pos?successOrderId=" + saved.getId();
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/pos";
        }
    }

    // ============== Private helpers ==============
    private void initSessionBranch(PosSessionData s, CustomUserDetails user) {
        if (s.getActiveBranchId() == null) {
            if (user != null && user.getBranchId() != null)
                s.setActiveBranchId(user.getBranchId());
            else
                branchRepo.findAll().stream().findFirst().ifPresent(b -> s.setActiveBranchId(b.getId()));
        }
    }

    private void buildPosModel(PosSessionData s, CustomUserDetails user, Model model) {
        if (s.getActiveBranchId() != null)
            branchRepo.findById(s.getActiveBranchId()).ifPresent(b -> model.addAttribute("activeBranch", b));
        boolean isOwner = user != null && user.hasRole("OWNER");
        model.addAttribute("isOwner", isOwner);
        if (isOwner)
            model.addAttribute("branches", branchRepo.findAll());
        model.addAttribute("posData", s);
        model.addAttribute("cart", s.getActiveCart());
        model.addAttribute("categories", categoryRepository.findAll().stream()
                .filter(c -> c.getStatus() == com.g4fpt.sms.product.enums.CategoryStatus.ACTIVE).toList());
        populateDropdownUnits(s, model);
    }

    private void addProductToCart(PosCart cart, ProductUnit pu) {
        cart.getItems().stream()
                .filter(i -> i.getProductUnitId().equals(pu.getId()))
                .findFirst()
                .ifPresentOrElse(
                        existing -> existing.setQuantity(existing.getQuantity() + 1),
                        () -> {
                            PosCartItem item = new PosCartItem();
                            item.setProductUnitId(pu.getId());
                            item.setSku(pu.getSku());
                            item.setName(pu.getProduct().getName());
                            item.setPrice(pu.getPrice());
                            item.setQuantity(1);
                            item.setUnitName(pu.getUnit() != null ? pu.getUnit().getName() : "");
                            item.setImageUrl(pu.getProduct().getImageUrl());
                            cart.getItems().add(item);
                        });
    }

    private void populateDropdownUnits(PosSessionData s, Model model) {
        Map<Long, List<ProductUnit>> map = new HashMap<>();
        s.getActiveCart().getItems().forEach(item -> productUnitRepo.findById(item.getProductUnitId()).ifPresent(pu -> {
            if (pu.getProduct() != null)
                map.put(item.getProductUnitId(), productUnitRepo.findByProductIdWithUnit(pu.getProduct().getId()));
        }));
        model.addAttribute("cartItemUnitsMap", map);
    }

    private POSCheckoutRequest buildCheckoutRequest(PosSessionData s, Long paymentMethodId,
            BigDecimal givenAmount, String note,
            CustomUserDetails user) {
        var cart = s.getActiveCart();
        var req = new POSCheckoutRequest();
        if (user == null || user.getEmployee() == null)
            throw new RuntimeException("Không tìm thấy thông tin nhân viên!");
        req.setEmployeeId(user.getEmployee().getId());
        if (s.getActiveBranchId() == null)
            throw new RuntimeException("Vui lòng chọn chi nhánh trước khi thanh toán!");
        req.setBranchId(s.getActiveBranchId());
        req.setCustomerId(cart.getCustomerId());
        req.setVoucherCode(cart.getVoucherCode());
        req.setPaymentMethodId(paymentMethodId);
        req.setPaidAmount(givenAmount);
        req.setVatRate(cart.getVatRate());
        req.setNote(note);
        req.setUsePoints(cart.isUsePoints());
        req.setItems(cart.getItems().stream().map(item -> {
            var r = new POSCartItemRequest();
            r.setProductUnitId(item.getProductUnitId());
            r.setQuantity(item.getQuantity());
            return r;
        }).toList());
        return req;
    }
}