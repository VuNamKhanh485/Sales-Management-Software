package com.g4fpt.sms.payment.controller;

import com.g4fpt.sms.auth.dto.SessionUser;
import com.g4fpt.sms.auth.util.SessionConstants;
import com.g4fpt.sms.branch.entity.Branch;
import com.g4fpt.sms.branch.entity.BranchStatus;
import com.g4fpt.sms.branch.service.BranchService;
import com.g4fpt.sms.payment.dto.CashbookDTO;
import com.g4fpt.sms.payment.entity.CashbookTransaction;
import com.g4fpt.sms.payment.service.CashbookService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/cashbook")
@RequiredArgsConstructor
public class CashbookController {

    private final CashbookService cashbookService;
    private final BranchService branchService;

    @GetMapping
    public String listCashbook(
            @RequestParam(value = "branchId", required = false) Long branchId,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "method", required = false) String method,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Model model,
            HttpSession session) {

        SessionUser user = (SessionUser) session.getAttribute(SessionConstants.LOGGED_IN_USER);
        if (user != null && !user.hasRole("OWNER")) {
            if (branchId == null) {
                branchId = user.getBranchId(); // Default to their branch
            }
        }

        Page<CashbookTransaction> transactionPage = cashbookService.getTransactions(
                branchId, type, method, null, null, page, size);

        List<Branch> branches = branchService.getAll();

        BigDecimal cashBalance = cashbookService.getBalance(branchId, "CASH");
        BigDecimal bankBalance = cashbookService.getBalance(branchId, "BANK");

        model.addAttribute("transactionPage", transactionPage);
        model.addAttribute("branches", branches);
        model.addAttribute("selectedBranchId", branchId);
        model.addAttribute("selectedType", type);
        model.addAttribute("selectedMethod", method);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", transactionPage.getTotalPages());
        model.addAttribute("cashBalance", cashBalance);
        model.addAttribute("bankBalance", bankBalance);

        return "payment/cashbook/list";
    }

    @GetMapping("/export")
    public org.springframework.http.ResponseEntity<byte[]> exportExcel(
            @RequestParam(value = "branchId", required = false) Long branchId,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "method", required = false) String method,
            HttpSession session) {

        SessionUser user = (SessionUser) session.getAttribute(SessionConstants.LOGGED_IN_USER);
        if (user != null && !user.hasRole("OWNER")) {
            if (branchId == null) {
                branchId = user.getBranchId();
            }
        }

        byte[] excelData = cashbookService.exportExcel(branchId, type, method, null, null);

        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "cashbook.xlsx");

        return org.springframework.http.ResponseEntity.ok()
                .headers(headers)
                .body(excelData);
    }

    @GetMapping("/create")
    public String showCreateForm(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        SessionUser user = (SessionUser) session.getAttribute(SessionConstants.LOGGED_IN_USER);

        if (user == null || !user.hasAnyRole("OWNER", "BRANCH_MANAGER")) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bạn không có quyền tạo phiếu thu/chi.");
            return "redirect:/cashbook";
        }

        CashbookDTO dto = new CashbookDTO();
        if (user.hasRole("BRANCH_MANAGER")) {
            dto.setBranchId(user.getBranchId());
        }

        model.addAttribute("cashbookDTO", dto);

        List<Branch> activeBranches = branchService.getAll().stream()
                .filter(b -> b.getStatus() == BranchStatus.ACTIVE)
                .collect(Collectors.toList());
        model.addAttribute("branches", activeBranches);

        model.addAttribute("sessionUser", user);
        return "payment/cashbook/form";
    }

    @PostMapping("/save")
    public String saveCashbook(
            @ModelAttribute("cashbookDTO") CashbookDTO dto,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        SessionUser user = (SessionUser) session.getAttribute(SessionConstants.LOGGED_IN_USER);

        if (user == null || !user.hasAnyRole("OWNER", "BRANCH_MANAGER")) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bạn không có quyền tạo phiếu thu/chi.");
            return "redirect:/cashbook";
        }

        Long currentUserId = user.getId();

        // Force branch id for Branch Manager to prevent tampering
        if (user.hasRole("BRANCH_MANAGER")) {
            dto.setBranchId(user.getBranchId());
        }

        try {
            cashbookService.createTransaction(dto, currentUserId);
            redirectAttributes.addFlashAttribute("successMessage", "Tạo phiếu thu/chi thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/cashbook/create";
        }

        return "redirect:/cashbook";
    }
}
