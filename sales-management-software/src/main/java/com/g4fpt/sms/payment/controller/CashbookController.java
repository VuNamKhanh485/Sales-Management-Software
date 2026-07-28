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
import org.springframework.dao.DataIntegrityViolationException;
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

        boolean isOwner = user != null && user.hasRole("OWNER");

        model.addAttribute("transactionPage", transactionPage);
        model.addAttribute("branches", branches);
        model.addAttribute("selectedBranchId", branchId);
        model.addAttribute("selectedType", type);
        model.addAttribute("selectedMethod", method);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", transactionPage.getTotalPages());
        model.addAttribute("cashBalance", cashBalance);
        model.addAttribute("bankBalance", bankBalance);
        model.addAttribute("isOwner", isOwner);

        return "payment/cashbook/list";
    }

    @GetMapping("/{id}")
    public String viewDetail(@PathVariable Long id, Model model) {
        CashbookTransaction transaction = cashbookService.getTransactionById(id);
        model.addAttribute("transaction", transaction);
        return "payment/cashbook/detail";
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
            CashbookTransaction transaction = cashbookService.createTransaction(dto, currentUserId);
            if (user.hasRole("OWNER")) {
                cashbookService.approveTransaction(transaction.getId());
                redirectAttributes.addFlashAttribute("successMessage", "Tạo và tự động duyệt phiếu thu/chi thành công!");
            } else {
                redirectAttributes.addFlashAttribute("successMessage", "Tạo phiếu thu/chi thành công! Chờ duyệt.");
            }
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Số tiền nhập vào quá lớn hoặc dữ liệu không hợp lệ, vui lòng kiểm tra lại.");
            return "redirect:/cashbook/create";
        } catch (Exception e) {
            String msg = e.getMessage();
            if (msg != null && (msg.contains("Data truncation") || msg.contains("Out of range value"))) {
                msg = "Số tiền nhập vào quá lớn, vui lòng kiểm tra lại.";
            }
            redirectAttributes.addFlashAttribute("errorMessage", msg);
            return "redirect:/cashbook/create";
        }

        return "redirect:/cashbook";
    }

    @PostMapping("/{id}/approve")
    public String approveTransaction(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        SessionUser user = (SessionUser) session.getAttribute(SessionConstants.LOGGED_IN_USER);
        if (user == null || !user.hasRole("OWNER")) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bạn không có quyền duyệt phiếu.");
            return "redirect:/cashbook";
        }
        try {
            cashbookService.approveTransaction(id);
            redirectAttributes.addFlashAttribute("successMessage", "Duyệt phiếu thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/cashbook";
    }

    @PostMapping("/{id}/reject")
    public String rejectTransaction(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        SessionUser user = (SessionUser) session.getAttribute(SessionConstants.LOGGED_IN_USER);
        if (user == null || !user.hasRole("OWNER")) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bạn không có quyền từ chối phiếu.");
            return "redirect:/cashbook";
        }
        try {
            cashbookService.rejectTransaction(id);
            redirectAttributes.addFlashAttribute("successMessage", "Đã từ chối phiếu thu/chi!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/cashbook";
    }
}
