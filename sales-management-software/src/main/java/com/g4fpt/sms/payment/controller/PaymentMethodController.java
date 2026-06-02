package com.g4fpt.sms.payment.controller;

import com.g4fpt.sms.common.exception.AppException;
import com.g4fpt.sms.payment.dto.request.PaymentMethodCreateRequest;
import com.g4fpt.sms.payment.dto.request.PaymentMethodUpdateRequest;
import com.g4fpt.sms.payment.dto.response.PaymentMethodResponse;
import com.g4fpt.sms.payment.service.PaymentMethodService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/payment-methods")
@RequiredArgsConstructor
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;

    // ── LIST + SEARCH ──────────────────────────────────────────
    @GetMapping
    public String list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            Model model) {
        model.addAttribute("paymentMethods", paymentMethodService.search(keyword, status));
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedStatus", status);
        return "payment-method/list";
    }

    // ── CREATE ─────────────────────────────────────────────────
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("request", new PaymentMethodCreateRequest());
        model.addAttribute("isEdit", false);
        return "payment-method/form";
    }

    @PostMapping("/create")
    public String create(
            @Valid @ModelAttribute("request") PaymentMethodCreateRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", false);
            return "payment-method/form";
        }
        try {
            paymentMethodService.create(request);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Thêm phương thức thanh toán thành công!");
        } catch (AppException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("isEdit", false);
            return "payment-method/form";
        }
        return "redirect:/payment-methods";
    }

    // ── EDIT ───────────────────────────────────────────────────
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        PaymentMethodResponse response = paymentMethodService.getById(id);
        PaymentMethodUpdateRequest request = PaymentMethodUpdateRequest.builder()
                .code(response.getCode())
                .name(response.getName())
                .status(response.getStatus() != null ? response.getStatus().name() : null)
                .build();
        model.addAttribute("request", request);
        model.addAttribute("paymentMethodId", id);
        model.addAttribute("isEdit", true);
        return "payment-method/form";
    }

    @PostMapping("/edit/{id}")
    public String edit(
            @PathVariable Long id,
            @Valid @ModelAttribute("request") PaymentMethodUpdateRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", true);
            model.addAttribute("paymentMethodId", id);
            return "payment-method/form";
        }
        try {
            paymentMethodService.update(id, request);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Cập nhật phương thức thanh toán thành công!");
        } catch (AppException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("isEdit", true);
            model.addAttribute("paymentMethodId", id);
            return "payment-method/form";
        }
        return "redirect:/payment-methods";
    }

    // ── DELETE ─────────────────────────────────────────────────
    @GetMapping("/delete/{id}")
    public String deleteConfirm(@PathVariable Long id, Model model) {
        model.addAttribute("paymentMethod", paymentMethodService.getEntityById(id));
        return "payment-method/confirm-delete";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        paymentMethodService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage",
                "Xóa phương thức thanh toán thành công!");
        return "redirect:/payment-methods";
    }
}
