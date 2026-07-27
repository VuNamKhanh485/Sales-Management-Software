package com.g4fpt.sms.dashboard.controller;

import com.g4fpt.sms.auth.dto.SessionUser;
import com.g4fpt.sms.auth.util.SessionConstants;
import com.g4fpt.sms.employee.entity.Employee;
import com.g4fpt.sms.employee.service.EmployeeService;
import com.g4fpt.sms.order.service.ImportService;
import com.g4fpt.sms.order.dto.ImportResponse;
import com.g4fpt.sms.order.entity.ReturnRequest;
import com.g4fpt.sms.order.service.ReturnRequestService;
import com.g4fpt.sms.order.service.TransferService;
import com.g4fpt.sms.order.dto.TransferResponse;
import jakarta.persistence.EntityNotFoundException;
import com.g4fpt.sms.payment.service.CashbookService;
import com.g4fpt.sms.payment.entity.CashbookTransaction;
import org.springframework.data.domain.PageRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final ReturnRequestService returnRequestService;
    private final ImportService importService;
    private final TransferService transferService;
    private final CashbookService cashbookService;
    private final EmployeeService employeeService;

    public record ApprovalRequest(String code, String type, String info, String creator, java.time.LocalDateTime createdAt, String url) {}

    private SessionUser getSessionUser(HttpSession session) {
        return (SessionUser) session.getAttribute(SessionConstants.LOGGED_IN_USER);
    }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(HttpSession session, Model model,
                            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page) {
        if (page == null) page = 0;
        SessionUser sessionUser = getSessionUser(session);
        
        if (sessionUser == null) {
            return "redirect:/login";
        }
        
        if (!sessionUser.hasAnyRole("OWNER", "BRANCH_MANAGER")) {
            return "redirect:/sales";
        }
        
        List<ApprovalRequest> pendingApprovals = new ArrayList<>();
        
        // Cả OWNER và BRANCH_MANAGER đều thấy yêu cầu trả hàng
        List<ReturnRequest> pendingReturns = returnRequestService.getPendingRequests();
        for (ReturnRequest req : pendingReturns) {
            try {
                String creatorName = String.valueOf(req.getRequestedBy());
                try {
                    Employee emp = employeeService.findById(req.getRequestedBy());
                    if (emp != null && emp.getFullName() != null) {
                        creatorName = emp.getFullName();
                    }
                } catch (Exception e) {
                    // Ignore
                }
                
                pendingApprovals.add(new ApprovalRequest(
                    "#" + req.getId(),
                    "Trả hàng",
                    req.getOrder().getCode(),
                    creatorName,
                    req.getCreatedAt(),
                    "/return/" + req.getId()
                ));
            } catch (EntityNotFoundException e) {
                // Skip return requests with missing order
            }
        }
        
        // Chỉ OWNER mới thấy yêu cầu nhập hàng
        if (sessionUser.hasRole("OWNER")) {
            List<ImportResponse> pendingImports = importService.getAllImports("PENDING", null, null);
            for (ImportResponse req : pendingImports) {
                pendingApprovals.add(new ApprovalRequest(
                    req.getCode(),
                    "Nhập hàng",
                    req.getBranchName(),
                    req.getCreatorName(),
                    req.getCreatedAt(),
                    "/imports/" + req.getId()
                ));
            }
        }
        
        // Yêu cầu chuyển kho: Phân quyền theo userBranchId
        Long userBranchId = sessionUser.hasRole("OWNER") ? null : sessionUser.getBranchId();
        List<TransferResponse> pendingTransfers = transferService.getTransfers(null, null, "PENDING", userBranchId, PageRequest.of(0, 50)).getContent();
        for (TransferResponse req : pendingTransfers) {
            pendingApprovals.add(new ApprovalRequest(
                req.getCode(),
                "Chuyển kho",
                req.getFromBranchName() + " ➔ " + req.getToBranchName(),
                req.getCreatorName(),
                req.getCreatedAt(),
                "/transfer/" + req.getId()
            ));
        }
        pendingApprovals.sort((a, b) -> b.createdAt().compareTo(a.createdAt())); // mới nhất lên đầu
        
        // 4. Fetch Pending Cashbook Transactions
        List<CashbookTransaction> pendingCashbookList = cashbookService.getPendingTransactions(userBranchId);
        for (CashbookTransaction tx : pendingCashbookList) {
            pendingApprovals.add(new ApprovalRequest(
                tx.getReferenceCode() != null ? tx.getReferenceCode() : "CB-" + tx.getId(),
                "Sổ quỹ",
                tx.getBranch().getName(),
                tx.getCreator() != null ? tx.getCreator().getFullName() : "N/A",
                tx.getCreatedAt(),
                "/cashbook"
            ));
        }
        
        // Sort again after adding cashbook
        pendingApprovals.sort((a, b) -> b.createdAt().compareTo(a.createdAt()));
        
        int size = 10;
        int totalItems = pendingApprovals.size();
        int totalPages = (int) Math.ceil((double) totalItems / size);
        if (totalPages == 0) totalPages = 1;
        if (page < 0) page = 0;
        if (page >= totalPages) page = totalPages - 1;
        
        int start = Math.min(page * size, totalItems);
        int end = Math.min((page + 1) * size, totalItems);
        
        List<ApprovalRequest> pagedApprovals = pendingApprovals.subList(start, end);
        
        model.addAttribute("pendingApprovals", pagedApprovals);
        model.addAttribute("totalPending", totalItems);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        
        return "dashboard/index";
    }
}
