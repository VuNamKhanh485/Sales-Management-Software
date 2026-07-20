package com.g4fpt.sms.voucher.controller;

import com.g4fpt.sms.common.exception.AppException;
import com.g4fpt.sms.voucher.dto.VoucherDTO;
import com.g4fpt.sms.voucher.enums.VoucherStatus;
import com.g4fpt.sms.voucher.service.VoucherService;
import com.g4fpt.sms.customer.service.CustomerRankService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class VoucherController {

    private final VoucherService voucherService;
    private final CustomerRankService customerRankService;

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

    @GetMapping({"/vouchers/create", "/vouchers/edit/{id}"})
    public String form(@PathVariable(required = false) Long id, Model model) {
        model.addAttribute("ranks", customerRankService.getAllRanks());
        if (id != null) {
            VoucherDTO dto = voucherService.getById(id);
            model.addAttribute("request", dto);
            model.addAttribute("voucherId", id);
        } else {
            model.addAttribute("request", new VoucherDTO());
        }

        return "voucher/form";
    }

    @PostMapping({"/vouchers/create", "/vouchers/edit/{id}"})
    public String save(
            @PathVariable(required = false) Long id,
            @Valid @ModelAttribute("request") VoucherDTO request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            if (id != null) {
                model.addAttribute("voucherId", id);
            }
            model.addAttribute("ranks", customerRankService.getAllRanks());
            return "voucher/form";
        }

        try {
            if (id != null) {
                voucherService.update(id, request);
                redirectAttributes.addFlashAttribute("successMessage", "Cập nhật voucher thành công!");
            } else {
                voucherService.create(request);
                redirectAttributes.addFlashAttribute("successMessage", "Tạo voucher thành công!");
            }
        } catch (AppException e) {
            model.addAttribute("errorMessage", e.getMessage());
            if (id != null) {
                model.addAttribute("voucherId", id);
            }
            model.addAttribute("ranks", customerRankService.getAllRanks());
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
        try {
            voucherService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa voucher thành công!");
        } catch (AppException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/vouchers";
    }
}