package com.g4fpt.sms.dashboard.controller;

import com.g4fpt.sms.auth.dto.SessionUser;
import com.g4fpt.sms.auth.util.SessionConstants;
import com.g4fpt.sms.order.service.ImportService;
import com.g4fpt.sms.order.dto.ImportResponse;
import com.g4fpt.sms.order.entity.ReturnRequest;
import com.g4fpt.sms.order.service.ReturnRequestService;
import com.g4fpt.sms.order.service.TransferService;
import com.g4fpt.sms.order.dto.TransferResponse;
import org.springframework.data.domain.PageRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final ReturnRequestService returnRequestService;
    private final ImportService importService;
    private final TransferService transferService;

    public record ApprovalRequest(String code, String type, String info, String creator, java.time.LocalDateTime createdAt, String url) {}

    private SessionUser getSessionUser(HttpSession session) {
        return (SessionUser) session.getAttribute(SessionConstants.LOGGED_IN_USER);
    }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(HttpSession session, Model model) {
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
            pendingApprovals.add(new ApprovalRequest(
                "#" + req.getId(),
                "Trả hàng",
                req.getOrder().getCode(),
                String.valueOf(req.getRequestedBy()),
                req.getCreatedAt(),
                "/return/" + req.getId()
            ));
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
        
        model.addAttribute("pendingApprovals", pendingApprovals);
        return "dashboard/index";
    }
}
