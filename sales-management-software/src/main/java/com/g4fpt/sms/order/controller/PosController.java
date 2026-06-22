package com.g4fpt.sms.order.controller;

import com.g4fpt.sms.branch.repository.BranchRepository;
import com.g4fpt.sms.customer.entity.Customer;
import com.g4fpt.sms.customer.repository.CustomerRepository;
import com.g4fpt.sms.order.dto.*;
import com.g4fpt.sms.order.entity.OrderTransaction;
import com.g4fpt.sms.order.repository.OrderTransactionRepository;
import com.g4fpt.sms.order.service.OrderTransactionService;
import com.g4fpt.sms.product.entity.Category;
import com.g4fpt.sms.product.entity.ProductUnit;
import com.g4fpt.sms.product.entity.Inventory;
import com.g4fpt.sms.product.enums.ProductStatus;
import com.g4fpt.sms.product.repository.CategoryRepository;
import com.g4fpt.sms.product.repository.ProductUnitRepository;
import com.g4fpt.sms.product.repository.InventoryRepository;
import com.g4fpt.sms.voucher.entity.Voucher;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
            Model model) {

        // Đổi tên từ "session" thành "posData"
        model.addAttribute("posData", session);
        model.addAttribute("cart", session.getActiveCart());

        List<Category> categories = categoryRepository.findAll();
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

        if (pu != null) {
            addProductToCart(session.getActiveCart(), pu);
        } else {
            ra.addFlashAttribute("error", "Không tìm thấy sản phẩm: " + kw);
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
        if (pu != null) {
            addProductToCart(session.getActiveCart(), pu);
        } else {
            ra.addFlashAttribute("error", "Không tìm thấy sản phẩm");
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

    // =============================================
    // 10. API tìm khách hàng
    // =============================================
    @GetMapping("/api/customer")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> findCustomer(
            @RequestParam String phone) {

        Map<String, Object> result = new HashMap<>();
        String keyword = phone.trim();
        Customer customer = customerRepository.findByPhone(keyword)
                .orElseGet(() -> {
                    org.springframework.data.domain.Page<Customer> page = 
                        customerRepository.findByPhoneContainingOrFullNameContainingIgnoreCase(keyword, keyword, org.springframework.data.domain.PageRequest.of(0, 1));
                    return page.isEmpty() ? null : page.getContent().get(0);
                });

        if (customer != null) {
            result.put("found", true);
            result.put("id", customer.getId());
            result.put("name", customer.getFullName());
            result.put("phone", customer.getPhone());
            result.put("point", customer.getTotalPoint() - customer.getUsedPoint());
            result.put("rank", customer.getCustomerRank() != null
                    ? customer.getCustomerRank().getName() : "Thường");
        } else {
            result.put("found", false);
        }
        return ResponseEntity.ok(result);
    }

    // =============================================
    // 11. Gán khách hàng
    // =============================================
    @GetMapping("/set-customer")
    public String setCustomer(
            @RequestParam Long customerId,
            @RequestParam String customerName,
            @RequestParam String customerPhone,
            @ModelAttribute("posSession") PosSessionData session) {

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
    @GetMapping("/api/voucher")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkVoucher(
            @RequestParam String code,
            @ModelAttribute("posSession") PosSessionData session) {

        Map<String, Object> result = new HashMap<>();
        PosCart cart = session.getActiveCart();
        try {
            Voucher voucher = posService.validateVoucher(code, cart.getTotalAmount());
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
            result.put("success", true);
            result.put("discount", discount);
            result.put("message", "Áp dụng thành công! Giảm " + discount + "đ");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return ResponseEntity.ok(result);
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
    @GetMapping("/api/products")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getProducts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword) {

        List<ProductUnit> all = productUnitRepo.findAll().stream()
                .filter(pu -> pu.getProduct() != null
                        && pu.getProduct().getStatus() == ProductStatus.ACTIVE)
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

        List<Map<String, Object>> result = all.stream().map(pu -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", pu.getId());
            map.put("name", pu.getProduct().getName());
            map.put("sku", pu.getSku());
            map.put("price", pu.getPrice());
            map.put("imageUrl", pu.getProduct().getImageUrl());
            map.put("unitName", pu.getUnit() != null ? pu.getUnit().getName() : "");
            map.put("category", pu.getProduct().getCategory() != null
                    ? pu.getProduct().getCategory().getName() : "");
            
            // Lấy tồn kho (mặc định 100 để test nếu chưa có bản ghi)
            Integer stock = inventoryRepository.findByBranchIdAndProductUnitId(1L, pu.getId())
                    .map(Inventory::getStock)
                    .orElse(100);
            map.put("stock", stock);
            
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
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
            RedirectAttributes ra) {

        PosCart cart = session.getActiveCart();
        if (cart.getItems().isEmpty()) {
            ra.addFlashAttribute("error", "Giỏ hàng trống!");
            return "redirect:/pos";
        }

        try {
            POSCheckoutRequest request = new POSCheckoutRequest();
            request.setBranchId(1L);
            request.setEmployeeId(1L);
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
}