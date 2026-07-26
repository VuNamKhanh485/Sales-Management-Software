package com.g4fpt.sms.order.controller;

import com.g4fpt.sms.auth.dto.SessionUser;
import com.g4fpt.sms.auth.util.SessionConstants;
import com.g4fpt.sms.branch.entity.Branch;
import com.g4fpt.sms.order.dto.*;
import com.g4fpt.sms.order.entity.OrderTransaction;
import com.g4fpt.sms.order.repository.OrderTransactionRepository;
import com.g4fpt.sms.order.service.OrderTransactionService;
import com.g4fpt.sms.product.entity.Category;
import com.g4fpt.sms.product.entity.ProductUnit;
import com.g4fpt.sms.product.repository.ProductUnitRepository;
import com.g4fpt.sms.voucher.entity.Voucher;
import com.g4fpt.sms.voucher.repository.VoucherRepository;
import com.g4fpt.sms.inventory.repository.InventoryRepository;
import com.g4fpt.sms.customer.repository.CustomerRepository;
import com.g4fpt.sms.branch.repository.BranchRepository;
import com.g4fpt.sms.product.enums.ProductStatus;
import com.g4fpt.sms.inventory.entity.Inventory;
import com.g4fpt.sms.customer.entity.Customer;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import com.g4fpt.sms.product.repository.CategoryRepository;
import com.g4fpt.sms.branch.entity.BranchStatus;
import com.g4fpt.sms.product.enums.CategoryStatus;
import com.g4fpt.sms.voucher.enums.VoucherStatus;
import com.g4fpt.sms.customer.enums.CustomerStatus;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import org.springframework.data.domain.PageRequest;

@Controller
@RequestMapping("/pos")
@SessionAttributes("posSession")
@RequiredArgsConstructor
public class PosController {

    private final OrderTransactionService posService;
    private final ProductUnitRepository productUnitRepo;
    private final CategoryRepository categoryRepository;
    private final CustomerRepository customerRepository;
    private final OrderTransactionRepository orderRepo;
    private final BranchRepository branchRepo;
    private final VoucherRepository voucherRepository;
    private final InventoryRepository inventoryRepository;

    @ModelAttribute("posSession")
    public PosSessionData setupSession() {
        return new PosSessionData();
    }

    // Hiển thị màn hình POS chính
    @GetMapping
    public String showPosScreen(
            @ModelAttribute("posSession") PosSessionData session,
            @RequestParam(required = false) Long successOrderId,
            HttpSession httpSession, 
            Model model) {
            
        SessionUser user = (SessionUser) httpSession.getAttribute(SessionConstants.LOGGED_IN_USER);
        
        initSessionBranch(session, user);
        buildPosModel(session, user, model);
        
        if (successOrderId != null) {
            OrderTransaction order = orderRepo.findById(successOrderId).orElse(null);
            
            if (order != null) {
                model.addAttribute("successOrder", order);
                
                if (order.getBranchId() != null) {
                    var branch = branchRepo.findById(order.getBranchId()).orElse(null);
                    if (branch != null) {
                        model.addAttribute("successBranch", branch);
                    }
                }
            }
        }
        
        return "order/pos";
    }

    // Quản lý các tab đơn hàng
    @GetMapping("/new-order")
    public String newOrder(@ModelAttribute("posSession") PosSessionData s, RedirectAttributes ra) {
        if (!s.canAddOrder()) {
            ra.addFlashAttribute("error", "Tối đa 5 đơn hàng cùng lúc!");
        } else {
            s.addNewOrder();
        }
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

    // Thêm, sửa, xóa sản phẩm trong giỏ hàng
    @GetMapping("/add")
    public String addToCart(@RequestParam("keyword") String keyword, @ModelAttribute("posSession") PosSessionData s,
            RedirectAttributes ra) {
        String kw = keyword.trim();
        
        ProductUnit pu = productUnitRepo.findBySku(kw).orElse(null);
        if (pu == null) {
            pu = productUnitRepo.findByBarcodeUnit(kw).orElse(null);
        }
        
        if (pu != null && pu.getProduct() != null && pu.getProduct().getStatus() == ProductStatus.ACTIVE) {
            addProductToCart(s.getActiveCart(), pu);
        } else {
            ra.addFlashAttribute("error", "Không tìm thấy sản phẩm hoặc sản phẩm đã ngưng hoạt động: " + kw);
        }
        return "redirect:/pos";
    }

    @GetMapping("/add-by-id")
    public String addToCartById(@RequestParam Long productUnitId,
            @ModelAttribute("posSession") PosSessionData s, RedirectAttributes ra) {
        ProductUnit pu = productUnitRepo.findById(productUnitId).orElse(null);
        if (pu != null && pu.getProduct() != null && pu.getProduct().getStatus() == ProductStatus.ACTIVE) {
            addProductToCart(s.getActiveCart(), pu);
            ra.addFlashAttribute("success", "Đã thêm sản phẩm: " + pu.getProduct().getName());
        } else {
            ra.addFlashAttribute("error", "Không tìm thấy sản phẩm hoặc sản phẩm đã ngưng hoạt động!");
        }
        return "redirect:/pos";
    }

    @GetMapping("/update-qty")
    public String updateQuantity(@RequestParam int index, @RequestParam int quantity,
            @ModelAttribute("posSession") PosSessionData s) {
        var items = s.getActiveCart().getItems();
        if (index >= 0 && index < items.size()) {
            if (quantity <= 0) {
                items.remove(index);
            } else {
                items.get(index).setQuantity(quantity);
            }
        }
        return "redirect:/pos";
    }

    @GetMapping("/update-unit")
    public String updateUnit(@RequestParam int index, @RequestParam Long productUnitId,
            @ModelAttribute("posSession") PosSessionData s) {
        var items = s.getActiveCart().getItems();
        if (index >= 0 && index < items.size()) {
            ProductUnit pu = productUnitRepo.findById(productUnitId).orElse(null);
            if (pu != null) {
                var item = items.get(index);
                item.setProductUnitId(pu.getId());
                item.setSku(pu.getSku());
                item.setPrice(pu.getPrice());
                if (pu.getUnit() != null) {
                    item.setUnitName(pu.getUnit().getName());
                } else {
                    item.setUnitName("");
                }
            }
        }
        return "redirect:/pos";
    }

    @GetMapping("/remove")
    public String removeCartItem(@RequestParam int index, @ModelAttribute("posSession") PosSessionData s) {
        var items = s.getActiveCart().getItems();
        if (index >= 0 && index < items.size()) {
            items.remove(index);
        }
        return "redirect:/pos";
    }

    // Tìm kiếm và chọn khách hàng cho đơn hàng
    @GetMapping("/search-customer")
    public String findCustomers(@RequestParam String phone, @ModelAttribute("posSession") PosSessionData session,
            HttpSession httpSession, Model model) {
        SessionUser user = (SessionUser) httpSession.getAttribute(SessionConstants.LOGGED_IN_USER);
        String kw = phone.trim().replaceAll("\\s+", " ");
        if (!kw.isEmpty()) {
            var result = customerRepository.searchActiveByPhoneOrName(kw, PageRequest.of(0, 5)).getContent();
            if (result.isEmpty()) {
                model.addAttribute("customerSearchError", "Không tìm thấy khách hàng");
            } else {
                model.addAttribute("customerSearchResults", result);
            }
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
        if (c == null || c.getStatus() != CustomerStatus.ACTIVE) {
            ra.addFlashAttribute("error", "Khách hàng này hiện đang ngừng hoạt động hoặc không tồn tại!");
            return "redirect:/pos";
        }
        var cart = s.getActiveCart();
        cart.setCustomerId(customerId);
        cart.setCustomerName(customerName);
        cart.setCustomerPhone(customerPhone);
        
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

    // Áp dụng và gỡ mã giảm giá
    @GetMapping("/apply-voucher")
    public String applyVoucher(@RequestParam String code, @ModelAttribute("posSession") PosSessionData s,
            RedirectAttributes ra) {
        var cart = s.getActiveCart();
        try {
            var totalWithVat = cart.getTotalAmount().add(cart.getVatAmount());
            var discount = posService.calculateVoucherDiscount(code, totalWithVat, cart.getCustomerId());
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

    // Bật/tắt sử dụng điểm tích lũy
    @GetMapping("/toggle-points")
    public String togglePoints(@ModelAttribute("posSession") PosSessionData s,
            @RequestParam boolean enabled, RedirectAttributes ra) {
        var cart = s.getActiveCart();
        if (cart.getCustomerId() == null) {
            ra.addFlashAttribute("error", "Vui lòng chọn khách hàng trước khi áp dụng điểm!");
            return "redirect:/pos";
        }
        if (enabled && cart.getCustomerAvailablePoints() <= 0) {
            ra.addFlashAttribute("error", "Khách hàng không có điểm tích luỹ khả dụng!");
            return "redirect:/pos";
        }
        cart.setUsePoints(enabled);
        return "redirect:/pos";
    }

    // Đổi chi nhánh làm việc
    @GetMapping("/change-branch")
    public String changeBranch(@RequestParam Long branchId, @ModelAttribute("posSession") PosSessionData s,
            HttpSession httpSession) {
        SessionUser user = (SessionUser) httpSession.getAttribute(SessionConstants.LOGGED_IN_USER);
        if (user != null && user.hasRole("OWNER")) {
            s.setActiveBranchId(branchId);
        }
        return "redirect:/pos";
    }

    // Danh sách sản phẩm
    @GetMapping("/product-list")
    public String getProductList(@ModelAttribute("posSession") PosSessionData s,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword, Model model) {
        String kw = (keyword != null && !keyword.isBlank()) ? keyword.trim() : null;
        var products = productUnitRepo.searchActiveProductUnits(categoryId, kw);
        
        Long branchId = s.getActiveBranchId();
        if (branchId == null) {
            List<Branch> branches = branchRepo.findAll();
            for (Branch b : branches) {
                if (b.getStatus() == BranchStatus.ACTIVE) {
                    branchId = b.getId();
                    break;
                }
            }
            s.setActiveBranchId(branchId);
        }
        
        Map<Long, Integer> stockMap = new HashMap<>();
        var inStockProducts = new ArrayList<>(products);
        inStockProducts.clear();
        if (branchId != null) {
            final Long bId = branchId;
            for (var pu : products) {
                Inventory inv = inventoryRepository.findByBranchIdAndProductUnitId(bId, pu.getId()).orElse(null);
                int stock = (inv != null) ? inv.getStock() : 0;
                if (stock > 0) {
                    stockMap.put(pu.getId(), stock);
                    inStockProducts.add(pu);
                }
            }
        }
        
        model.addAttribute("products", inStockProducts);
        model.addAttribute("stockMap", stockMap);
        
        List<Category> allCategories = categoryRepository.findAll();
        List<Category> activeCategories = new ArrayList<>();
        for (Category c : allCategories) {
            if (c.getStatus() == CategoryStatus.ACTIVE) {
                activeCategories.add(c);
            }
        }
        
        model.addAttribute("categories", activeCategories);
        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryId", categoryId);
        return "order/pos-product-list";
    }

    // Danh sách voucher
    @GetMapping("/voucher-list")
    public String getAvailableVouchers(@ModelAttribute("posSession") PosSessionData s, Model model) {
        Long customerId = s.getActiveCart().getCustomerId();
        if (customerId == null) {
            model.addAttribute("vouchers", new ArrayList<>());
            return "order/pos-voucher-list";
        }
        
        var now = LocalDateTime.now();
        Customer customer = customerRepository.findById(customerId).orElse(null);
        List<Voucher> allVouchers = voucherRepository.findAll();
        List<Voucher> activeVouchers = new ArrayList<>();
        
        for (Voucher v : allVouchers) {
            if (v.getStatus() == VoucherStatus.ACTIVE
                    && v.getStartAt().isBefore(now) && v.getEndAt().isAfter(now)) {
                
                if (v.getCustomerRank() == null) {
                    activeVouchers.add(v);
                } else if (customer != null) {
                    BigDecimal customerRevenueCondition = BigDecimal.ZERO;
                    if (customer.getCustomerRank() != null) {
                        customerRevenueCondition = customer.getCustomerRank().getConditionTotalRevenue();
                    }
                    if (customerRevenueCondition.compareTo(v.getCustomerRank().getConditionTotalRevenue()) >= 0) {
                        activeVouchers.add(v);
                    }
                }
            }
        }
        
        model.addAttribute("vouchers", activeVouchers);
        return "order/pos-voucher-list";
    }

    // Xem lịch sử bán hàng
    @GetMapping("/sales-history")
    public String getSalesHistory(HttpSession httpSession,
            @RequestParam(required = false) String date, Model model) {
        SessionUser user = (SessionUser) httpSession.getAttribute(SessionConstants.LOGGED_IN_USER);
        if (user == null) {
            return "redirect:/login";
        }
            
        boolean isOwner = user.hasRole("OWNER");
        var ld = (date != null && !date.isBlank()) ? LocalDate.parse(date) : LocalDate.now();
        
        List<OrderTransaction> orders;
        if (isOwner) {
            orders = orderRepo.findByDateRange(ld.atStartOfDay(), ld.atTime(LocalTime.MAX));
        } else {
            orders = orderRepo.findByCreatedByAndDateRange(
                    user.getId(), ld.atStartOfDay(), ld.atTime(LocalTime.MAX));
        }
        
        Set<Long> branchIds = new HashSet<>();
        for (OrderTransaction order : orders) {
            if (order.getBranchId() != null) {
                branchIds.add(order.getBranchId());
            }
        }
        
        List<Branch> branches = branchRepo.findAllById(branchIds);
        Map<Long, String> branchNames = new HashMap<>();
        for (Branch b : branches) {
            branchNames.put(b.getId(), b.getName());
        }
        
        model.addAttribute("isOwner", isOwner);
        model.addAttribute("orders", orders);
        model.addAttribute("branchNames", branchNames);
        model.addAttribute("filterDate", ld.toString());
        return "order/pos-history";
    }

    @GetMapping("/sales-history/{id}")
    public String getOrderDetail(@PathVariable Long id, Model model) {
        OrderTransaction order = orderRepo.findById(id).orElse(null);
        if (order == null) {
            throw new RuntimeException("Không tìm thấy đơn hàng");
        }
        
        model.addAttribute("order", order);
        
        Branch branch = null;
        if (order.getBranchId() != null) {
            branch = branchRepo.findById(order.getBranchId()).orElse(null);
        }
        
        if (branch != null) {
            model.addAttribute("branchName", branch.getName());
            model.addAttribute("branchAddress", branch.getAddress());
            model.addAttribute("branchPhone", branch.getPhone());
        } else {
            model.addAttribute("branchName", "");
            model.addAttribute("branchAddress", "");
            model.addAttribute("branchPhone", "");
        }
        return "order/pos-history-detail";
    }

    // Xử lý thanh toán
    @PostMapping("/checkout")
    public String checkout(@ModelAttribute("posSession") PosSessionData s,
            @RequestParam(required = false) String note,
            @RequestParam Long paymentMethodId,
            @RequestParam BigDecimal givenAmount,
            HttpSession httpSession,
            RedirectAttributes ra) {
        SessionUser user = (SessionUser) httpSession.getAttribute(SessionConstants.LOGGED_IN_USER);
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
    
    // Khởi tạo chi nhánh
    private void initSessionBranch(PosSessionData s, SessionUser user) {
        if (s.getActiveBranchId() == null) {
            if (user != null && user.getBranchId() != null) {
                s.setActiveBranchId(user.getBranchId());
            } else {
                List<Branch> branches = branchRepo.findAll();
                for (Branch b : branches) {
                    if (b.getStatus() == BranchStatus.ACTIVE) {
                        s.setActiveBranchId(b.getId());
                        break;
                    }
                }
            }
        }
    }

    // Load dữ liệu POS
    private void buildPosModel(PosSessionData s, SessionUser user, Model model) {
        if (s.getActiveBranchId() != null) {
            Branch b = branchRepo.findById(s.getActiveBranchId()).orElse(null);
            if (b != null) {
                model.addAttribute("activeBranch", b);
            }
        }
        
        boolean isOwner = (user != null && user.hasRole("OWNER"));
        model.addAttribute("isOwner", isOwner);
        
        if (isOwner) {
            List<Branch> allBranches = branchRepo.findAll();
            List<Branch> activeBranches = new ArrayList<>();
            for (Branch b : allBranches) {
                if (b.getStatus() == BranchStatus.ACTIVE) {
                    activeBranches.add(b);
                }
            }
            model.addAttribute("branches", activeBranches);
        }
        
        model.addAttribute("posData", s);
        model.addAttribute("cart", s.getActiveCart());
        
        List<Category> allCategories = categoryRepository.findAll();
        List<Category> activeCategories = new ArrayList<>();
        for (Category c : allCategories) {
            if (c.getStatus() == CategoryStatus.ACTIVE) {
                activeCategories.add(c);
            }
        }
        model.addAttribute("categories", activeCategories);
        
        populateDropdownUnits(s, model);
    }

    // Thêm sản phẩm
    private void addProductToCart(PosCart cart, ProductUnit pu) {
        PosCartItem existingItem = null;
        for (PosCartItem item : cart.getItems()) {
            if (item.getProductUnitId().equals(pu.getId())) {
                existingItem = item;
                break;
            }
        }
        
        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + 1);
        } else {
            PosCartItem item = new PosCartItem();
            item.setProductUnitId(pu.getId());
            item.setSku(pu.getSku());
            item.setName(pu.getProduct().getName());
            item.setPrice(pu.getPrice());
            item.setQuantity(1);
            if (pu.getUnit() != null) {
                item.setUnitName(pu.getUnit().getName());
            } else {
                item.setUnitName("");
            }
            item.setImageUrl(pu.getProduct().getImageName());
            cart.getItems().add(item);
        }
    }

    // Load các đơn vị
    private void populateDropdownUnits(PosSessionData s, Model model) {
        Map<Long, List<ProductUnit>> map = new HashMap<>();
        for (PosCartItem item : s.getActiveCart().getItems()) {
            ProductUnit pu = productUnitRepo.findById(item.getProductUnitId()).orElse(null);
            if (pu != null && pu.getProduct() != null) {
                List<ProductUnit> units = productUnitRepo.findByProductIdWithUnit(pu.getProduct().getId());
                map.put(item.getProductUnitId(), units);
            }
        }
        model.addAttribute("cartItemUnitsMap", map);
    }

    // Chuẩn bị thanh toán
    private POSCheckoutRequest buildCheckoutRequest(PosSessionData s, Long paymentMethodId,
            BigDecimal givenAmount, String note,
            SessionUser user) {
        var cart = s.getActiveCart();
        var req = new POSCheckoutRequest();
        
        if (user == null) {
            throw new RuntimeException("Không tìm thấy thông tin nhân viên!");
        }
        req.setEmployeeId(user.getId());
        
        if (s.getActiveBranchId() == null) {
            throw new RuntimeException("Vui lòng chọn chi nhánh trước khi thanh toán!");
        }
        
        req.setBranchId(s.getActiveBranchId());
        req.setCustomerId(cart.getCustomerId());
        req.setVoucherCode(cart.getVoucherCode());
        req.setPaymentMethodId(paymentMethodId);
        req.setPaidAmount(givenAmount);
        req.setVatRate(cart.getVatRate());
        req.setNote(note);
        req.setUsePoints(cart.isUsePoints());
        
        List<POSCartItemRequest> items = new ArrayList<>();
        for (PosCartItem item : cart.getItems()) {
            POSCartItemRequest r = new POSCartItemRequest();
            r.setProductUnitId(item.getProductUnitId());
            r.setQuantity(item.getQuantity());
            items.add(r);
        }
        req.setItems(items);
        
        return req;
    }
}
