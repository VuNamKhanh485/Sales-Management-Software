package com.g4fpt.sms.voucher.controller;

import com.g4fpt.sms.common.exception.AppException;
import com.g4fpt.sms.voucher.dto.request.VoucherCreateRequest;
import com.g4fpt.sms.voucher.dto.request.VoucherUpdateRequest;
import com.g4fpt.sms.voucher.dto.response.VoucherResponse;
import com.g4fpt.sms.voucher.enums.VoucherStatus;
import com.g4fpt.sms.voucher.service.VoucherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class VoucherController {

    private final VoucherService voucherService;

    @GetMapping("/vouchers")
    public String list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            Model model) {
        
        VoucherStatus voucherStatus = null;
        if (status != null && !status.trim().isEmpty()) {
            try {
                voucherStatus = VoucherStatus.valueOf(status.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
            }
        }
        
        var pageResponse = voucherService.search(
                keyword != null && !keyword.trim().isEmpty() ? keyword.trim() : null,
                voucherStatus,
                0,
                1000
        );
        
        model.addAttribute("vouchers", pageResponse.getContent());
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        return "voucher/list";
    }

    @GetMapping("/vouchers/create")
    public String createForm(Model model) {
        model.addAttribute("request", new VoucherCreateRequest());
        model.addAttribute("isEdit", false);
        return "voucher/form";
    }

    @PostMapping("/vouchers/create")
    public String createMvc(
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

    @GetMapping("/vouchers/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        var response = voucherService.getById(id);
        VoucherUpdateRequest request = VoucherUpdateRequest.builder()
                .code(response.getCode())
                .name(response.getName())
                .discountType(response.getDiscountType())
                .discountValue(response.getDiscountValue())
                .minOrderAmount(response.getMinOrderAmount())
                .maxDiscountAmount(response.getMaxDiscountAmount())
                .startAt(response.getStartAt())
                .endAt(response.getEndAt())
                .status(response.getStatus())
                .build();
        
        model.addAttribute("request", request);
        model.addAttribute("voucherId", id);
        model.addAttribute("isEdit", true);
        return "voucher/form";
    }

    @PostMapping("/vouchers/edit/{id}")
    public String editMvc(
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

    @GetMapping("/vouchers/delete/{id}")
    public String deleteConfirm(@PathVariable Long id, Model model) {
        model.addAttribute("voucher", voucherService.getById(id));
        return "voucher/confirm-delete";
    }

    @PostMapping("/vouchers/delete/{id}")
    public String deleteMvc(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        voucherService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Xóa voucher thành công!");
        return "redirect:/vouchers";
    }


}