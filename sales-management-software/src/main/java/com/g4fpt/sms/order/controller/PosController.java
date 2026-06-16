package com.g4fpt.sms.order.controller;

import com.g4fpt.sms.customer.entity.Customer;
import com.g4fpt.sms.customer.repository.CustomerRepository;
import com.g4fpt.sms.order.dto.POSCartItemRequest;
import com.g4fpt.sms.order.dto.POSCheckoutRequest;
import com.g4fpt.sms.order.dto.PosCart;
import com.g4fpt.sms.order.dto.PosCartItem;
import com.g4fpt.sms.order.entity.OrderTransaction;
import com.g4fpt.sms.order.service.OrderTransactionService;
import com.g4fpt.sms.product.entity.Category;
import com.g4fpt.sms.product.entity.ProductUnit;
import com.g4fpt.sms.product.enums.ProductStatus;
import com.g4fpt.sms.product.repository.CategoryRepository;
import com.g4fpt.sms.product.repository.ProductUnitRepository;
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
@SessionAttributes("sessionCart")
@RequiredArgsConstructor
public class PosController {

    private final OrderTransactionService posService;
    private final ProductUnitRepository productUnitRepo;
    private final CategoryRepository categoryRepository;
    private final CustomerRepository customerRepository;

    @ModelAttribute("sessionCart")
    public PosCart setupCart() {
        return new PosCart();
    }

    // =============================================
    // 1. Màn hình POS chính
    // =============================================
    @GetMapping
    public String showPosScreen(
            @ModelAttribute("sessionCart") PosCart cart,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "keyword", required = false) String keyword,
            Model model) {

        // Danh sách category cho tab
        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("keyword", keyword);

        // Lấy tất cả ProductUnit có product ACTIVE
        List<ProductUnit> allProducts = productUnitRepo.findAll()
                .stream()
                .filter(pu -> pu.getProduct() != null
                        && pu.getProduct().getStatus() == ProductStatus.ACTIVE)
                .collect(Collectors.toList());

        // Filter theo keyword
        if (keyword != null && !keyword.isBlank()) {
            String kw = keyword.trim().toLowerCase();
            allProducts = allProducts.stream()
                    .filter(pu ->
                            pu.getProduct().getName().toLowerCase().contains(kw)
                                    || pu.getSku().toLowerCase().contains(kw))
                    .collect(Collectors.toList());
        }
        // Filter theo category
        else if (categoryId != null) {
            allProducts = allProducts.stream()
                    .filter(pu -> pu.getProduct().getCategory() != null
                            && pu.getProduct().getCategory().getId().equals(categoryId))
                    .collect(Collectors.toList());
        }

        model.addAttribute("products", allProducts);
        model.addAttribute("cart", cart);

        // Flash message
        model.addAttribute("cart", cart);

        return "order/pos";
    }

    // =============================================
    // 2. Thêm sản phẩm bằng SKU/barcode (quét mã)
    // =============================================
    @GetMapping("/add")
    public String addToCart(
            @RequestParam String keyword,
            @ModelAttribute("sessionCart") PosCart cart,
            RedirectAttributes ra) {

        String kw = keyword.trim();
        ProductUnit pu = productUnitRepo.findBySku(kw)
                .orElseGet(() -> productUnitRepo.findByBarcodeUnit(kw).orElse(null));

        if (pu != null) {
            addProductToCart(cart, pu);
        } else {
            ra.addFlashAttribute("error", "Không tìm thấy sản phẩm: " + kw);
        }

        return "redirect:/pos";
    }

    // =============================================
    // 3. Thêm sản phẩm từ grid (click card)
    // =============================================
    @GetMapping("/add-by-id")
    public String addToCartById(
            @RequestParam Long productUnitId,
            @ModelAttribute("sessionCart") PosCart cart,
            RedirectAttributes ra) {

        ProductUnit pu = productUnitRepo.findById(productUnitId).orElse(null);
        if (pu != null) {
            addProductToCart(cart, pu);
        } else {
            ra.addFlashAttribute("error", "Không tìm thấy sản phẩm");
        }

        return "redirect:/pos";
    }

    // =============================================
    // 4. Cập nhật số lượng
    // =============================================
    @GetMapping("/update-qty")
    public String updateQuantity(
            @RequestParam int index,
            @RequestParam int quantity,
            @ModelAttribute("sessionCart") PosCart cart) {

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
    // 5. Xóa 1 sản phẩm khỏi giỏ
    // =============================================
    @GetMapping("/remove")
    public String removeCartItem(
            @RequestParam int index,
            @ModelAttribute("sessionCart") PosCart cart) {

        if (index >= 0 && index < cart.getItems().size()) {
            cart.getItems().remove(index);
        }
        return "redirect:/pos";
    }

    // =============================================
    // 6. Xóa toàn bộ giỏ hàng
    // =============================================
    @GetMapping("/clear")
    public String clearCart(@ModelAttribute("sessionCart") PosCart cart) {
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
    // 7. API tìm khách hàng theo SĐT (AJAX)
    // =============================================
    @GetMapping("/api/customer")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> findCustomer(
            @RequestParam String phone) {

        Map<String, Object> result = new HashMap<>();
        Customer customer = customerRepository.findByPhone(phone.trim()).orElse(null);

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
            result.put("message", "Không tìm thấy khách hàng");
        }

        return ResponseEntity.ok(result);
    }

    // =============================================
    // 8. Gán khách hàng vào giỏ
    // =============================================
    @PostMapping("/set-customer")
    public String setCustomer(
            @RequestParam Long customerId,
            @RequestParam String customerName,
            @RequestParam String customerPhone,
            @ModelAttribute("sessionCart") PosCart cart) {

        cart.setCustomerId(customerId);
        cart.setCustomerName(customerName);
        cart.setCustomerPhone(customerPhone);
        return "redirect:/pos";
    }

    // =============================================
    // 9. Xóa khách hàng khỏi giỏ
    // =============================================
    @GetMapping("/remove-customer")
    public String removeCustomer(@ModelAttribute("sessionCart") PosCart cart) {
        cart.setCustomerId(null);
        cart.setCustomerName(null);
        cart.setCustomerPhone(null);
        return "redirect:/pos";
    }

    // =============================================
    // 10. API kiểm tra voucher (AJAX)
    // =============================================
    @GetMapping("/api/voucher")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkVoucher(
            @RequestParam String code,
            @ModelAttribute("sessionCart") PosCart cart) {

        Map<String, Object> result = new HashMap<>();
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
    // 11. Xóa voucher
    // =============================================
    @GetMapping("/remove-voucher")
    public String removeVoucher(@ModelAttribute("sessionCart") PosCart cart) {
        cart.setVoucherCode(null);
        cart.setVoucherDiscount(BigDecimal.ZERO);
        return "redirect:/pos";
    }

    // =============================================
    // 12. Thanh toán
    // =============================================
    @PostMapping("/checkout")
    public String checkoutOrder(
            @ModelAttribute("sessionCart") PosCart cart,
            @RequestParam(required = false) String note,
            @RequestParam Long paymentMethodId,
            @RequestParam BigDecimal givenAmount,
            RedirectAttributes ra,
            Model model) {

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

            // Reset giỏ sau thanh toán
            cart.getItems().clear();
            cart.setCustomerId(null);
            cart.setCustomerName(null);
            cart.setCustomerPhone(null);
            cart.setVoucherCode(null);
            cart.setVoucherDiscount(BigDecimal.ZERO);
            cart.setGivenAmount(BigDecimal.ZERO);

            ra.addFlashAttribute("success",
                    "Thanh toán thành công! Mã đơn: " + savedOrder.getCode());
            return "redirect:/pos";

        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/pos";
        }
    }

    // =============================================
    // Helper: thêm sản phẩm vào giỏ
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
            newItem.setUnitName(pu.getUnit().getName());
            newItem.setImageUrl(pu.getProduct().getImageUrl());
            cart.getItems().add(newItem);
        }
    }
}