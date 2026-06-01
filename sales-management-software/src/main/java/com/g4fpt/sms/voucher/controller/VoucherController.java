package com.g4fpt.sms.voucher.controller;

import com.g4fpt.sms.common.exception.AppException;
import com.g4fpt.sms.voucher.dto.request.VoucherCreateRequest;
import com.g4fpt.sms.voucher.dto.request.VoucherUpdateRequest;
import com.g4fpt.sms.voucher.service.VoucherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/vouchers")
@RequiredArgsConstructor
public class VoucherController {

    private final VoucherService voucherService;

    @GetMapping
    public String list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            Model model) {
        model.addAttribute("vouchers", voucherService.search(keyword, status));
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedStatus", status);
        return "voucher/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("request", new VoucherCreateRequest());
        model.addAttribute("isEdit", false);
        return "voucher/form";
    }

    @PostMapping("/create")
    public String create(
            @Valid @ModelAttribute("request") VoucherCreateRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", false);
            return "voucher/form";
        }
        try {
            voucherService.create(request);
            redirectAttributes.addFlashAttribute("successMessage", "Tạo voucher thành công!");
        } catch (AppException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("isEdit", false);
            return "voucher/form";
        }
        return "redirect:/vouchers";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        var response = voucherService.getById(id);
        // Map response → UpdateRequest để bind vào form
        VoucherUpdateRequest request = VoucherUpdateRequest.builder()
                .code(response.getCode())
                .nameVoucher(response.getNameVoucher())
                .discountType(response.getDiscountType())
                .discountValue(response.getDiscountValue())
                .minOrderValue(response.getMinOrderValue())
                .maxDiscountAmount(response.getMaxDiscountAmount())
                .usageLimit(response.getUsageLimit())
                .startDate(response.getStartDate())
                .endDate(response.getEndDate())
                .status(response.getStatus() != null ? response.getStatus().name() : null)
                .build();
        model.addAttribute("request", request);
        model.addAttribute("voucherId", id);
        model.addAttribute("isEdit", true);
        return "voucher/form";
    }

    @PostMapping("/edit/{id}")
    public String edit(
            @PathVariable Long id,
            @Valid @ModelAttribute("request") VoucherUpdateRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", true);
            model.addAttribute("voucherId", id);
            return "voucher/form";
        }
        try {
            voucherService.update(id, request);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật voucher thành công!");
        } catch (AppException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("isEdit", true);
            model.addAttribute("voucherId", id);
            return "voucher/form";
        }
        return "redirect:/vouchers";
    }

    @GetMapping("/delete/{id}")
    public String deleteConfirm(@PathVariable Long id, Model model) {
        model.addAttribute("voucher", voucherService.getEntityById(id));
        return "voucher/confirm-delete";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        voucherService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Xóa voucher thành công!");
        return "redirect:/vouchers";
    }
}