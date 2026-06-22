package com.g4fpt.sms.order.controller;

import com.g4fpt.sms.order.dto.POSCartItemRequest;
import com.g4fpt.sms.order.dto.POSCheckoutRequest;
import com.g4fpt.sms.order.dto.PosCart;
import com.g4fpt.sms.order.dto.PosCartItem;
import com.g4fpt.sms.order.entity.OrderTransaction;
import com.g4fpt.sms.order.service.OrderTransactionService;
import com.g4fpt.sms.product.entity.ProductUnit;
import com.g4fpt.sms.product.repository.ProductUnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/pos")
@SessionAttributes("sessionCart")
@RequiredArgsConstructor
public class PosController {

    private final OrderTransactionService posService;
    private final ProductUnitRepository productUnitRepo;

    @ModelAttribute("sessionCart")
    public PosCart setupCart() {
        return new PosCart();
    }

    @GetMapping
    public String showPosScreen(@ModelAttribute("sessionCart") PosCart cart, Model model) {
        model.addAttribute("cart", cart);
        return "order/pos";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam String password,
                            @ModelAttribute("sessionCart") PosCart cart,
                            RedirectAttributes ra) {

        ProductUnit pu = null;
        try {
            Long id = Long.parseLong(password);
            pu = productUnitRepo.findById(id).orElse(null);
        } catch (NumberFormatException e) {
            pu = productUnitRepo.findBySku(password).orElse(null);
        }

        if (pu != null) {

            PosCartItem existingItem = null;
            for (PosCartItem item : cart.getItems()) {
                if (item.getProductUnitId().equals(pu.getId())) {
                    existingItem = item;
                    break;
                }
            }

            // --- XỬ LÝ LOGIC ---
            if (existingItem != null) {
                existingItem.setQuantity(existingItem.getQuantity() + 1);
            } else {
                PosCartItem newItem = new PosCartItem();
                newItem.setProductUnitId(pu.getId());
                newItem.setSku(pu.getSku());
                newItem.setName(pu.getProduct().getName());
                newItem.setPrice(pu.getPrice());
                newItem.setQuantity(1);
                newItem.setUnitName(pu.getUnit().getName());
                cart.getItems().add(newItem);
            }
        } else {
            ra.addFlashAttribute("error", "Không tìm thấy sản phẩm: " + password);
        }

        return "redirect:/pos";
    }
    @PostMapping("/remove")
    public String removeCartItem(@RequestParam int index, @ModelAttribute("sessionCart") PosCart cart) {
        cart.getItems().remove(index);
        return "redirect:/pos";
    }

    @PostMapping("/clear")
    public String clearCart(@ModelAttribute("sessionCart") PosCart cart) {
        cart.getItems().clear();
        return "redirect:/pos";
    }

    @PostMapping("/checkout")
    public String checkoutOrder(
            @ModelAttribute("sessionCart") PosCart cart,
            @RequestParam String note,
            Model model) {

        if (cart.getItems().isEmpty()) {
            return "redirect:/pos?error=empty_cart";
        }

        try {
            POSCheckoutRequest request = new POSCheckoutRequest();
            request.setBranchId(1L);
            request.setEmployeeId(1L);
            request.setPaymentMethodId(cart.getPaymentMethodId());
            request.setPaidAmount(cart.getGivenAmount());
            request.setNote(note);

            request.setItems(cart.getItems().stream().map(item -> {
                POSCartItemRequest req = new POSCartItemRequest();
                req.setProductUnitId(item.getProductUnitId());
                req.setQuantity(item.getQuantity());
                return req;
            }).collect(Collectors.toList()));

            OrderTransaction savedOrder = posService.processCheckout(request);

            cart.getItems().clear();
            return "redirect:/pos?success=" + savedOrder.getCode();

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "order/pos";
        }
    }
}