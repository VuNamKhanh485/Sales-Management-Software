package com.g4fpt.sms.order.controller;

import com.g4fpt.sms.order.entity.OrderTransaction;
import com.g4fpt.sms.order.entity.ReturnRequest;
import com.g4fpt.sms.order.repository.OrderTransactionRepository;
import com.g4fpt.sms.order.service.ReturnRequestService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import com.g4fpt.sms.auth.dto.SessionUser;
import com.g4fpt.sms.auth.util.SessionConstants;

@Controller
@RequestMapping("/return")
@RequiredArgsConstructor
public class ReturnController {

    private final ReturnRequestService returnRequestService;
    private final OrderTransactionRepository orderTransactionRepository;
    private final com.g4fpt.sms.employee.repository.EmployeeRepository employeeRepository;

    @Value("${upload.path}")
    private String uploadDir;

    // Lấy thông tin nhân viên đang đăng nhập từ session
    private SessionUser getCurrentUser(HttpSession session) {
        return (SessionUser) session.getAttribute(SessionConstants.LOGGED_IN_USER);
    }

    // Hiển thị trang tạo yêu cầu trả hàng và tìm kiếm đơn hàng
    @GetMapping
    public String returnPage(@RequestParam(required = false) String orderCode, Model model) {
        if (orderCode != null && !orderCode.isBlank()) {
            model.addAttribute("autoOrderCode", orderCode);
        } else {
            model.addAttribute("autoOrderCode", "");
        }

        if (orderCode != null && !orderCode.trim().isEmpty()) {
            try {
                OrderTransaction order = returnRequestService.searchOrderByCode(orderCode.trim());
                model.addAttribute("order", order);
            } catch (RuntimeException e) {
                model.addAttribute("error", e.getMessage());
            }
        }

        return "order/return-request";
    }

    // Xử lý gửi yêu cầu trả hàng từ giao diện
    @PostMapping("/create")
    public String createReturnRequest(
            @RequestParam("orderId") Long orderId,
            @RequestParam("reason") String reason,
            @RequestParam(value = "detailIds", required = false) List<Long> detailIds,
            @RequestParam(value = "quantities", required = false) List<Integer> quantities,
            @RequestParam(value = "images", required = false) List<MultipartFile> images,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            SessionUser user = getCurrentUser(session);
            Long employeeId = user.getId();

            List<ReturnRequestService.ReturnItemInput> items = new ArrayList<>();
            if (detailIds != null && quantities != null && detailIds.size() == quantities.size()) {
                for (int i = 0; i < detailIds.size(); i++) {
                    ReturnRequestService.ReturnItemInput input = new ReturnRequestService.ReturnItemInput(detailIds.get(i), quantities.get(i));
                    items.add(input);
                }
            }

            if (items.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Chọn ít nhất 1 sản phẩm để trả");
                return "redirect:/return";
            }

            OrderTransaction order = orderTransactionRepository.findByIdWithDetails(orderId).orElse(null);
            if (order == null) {
                throw new RuntimeException("Không tìm thấy đơn hàng");
            }
            
            Long branchId = order.getBranchId();

            ReturnRequest request = returnRequestService.createReturnRequest(
                    orderId, branchId, employeeId, reason, items, images);

            redirectAttributes.addFlashAttribute("success", "Đã tạo yêu cầu trả hàng mã #" + request.getId());
            return "redirect:/return";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/return";
        }
    }

    // Hiển thị trang quản lý các yêu cầu trả hàng
    @GetMapping("/manage")
    public String managePage(HttpSession session, Model model) {
        SessionUser user = getCurrentUser(session);
        if (user == null || (!user.hasRole("OWNER") && !user.hasRole("BRANCH_MANAGER"))) {
            return "redirect:/";
        }
        List<ReturnRequest> requests = returnRequestService.getAllRequests();
        model.addAttribute("requests", requests);
        
        long pendingCount = returnRequestService.countPendingRequests();
        model.addAttribute("pendingCount", pendingCount);
        
        Map<Long, String> employeeNameMap = new HashMap<>();
        for (ReturnRequest req : requests) {
            if (req.getRequestedBy() != null && !employeeNameMap.containsKey(req.getRequestedBy())) {
                employeeRepository.findById(req.getRequestedBy()).ifPresent(e -> employeeNameMap.put(req.getRequestedBy(), e.getFullName()));
            }
            if (req.getReviewedBy() != null && !employeeNameMap.containsKey(req.getReviewedBy())) {
                employeeRepository.findById(req.getReviewedBy()).ifPresent(e -> employeeNameMap.put(req.getReviewedBy(), e.getFullName()));
            }
        }
        model.addAttribute("employeeNameMap", employeeNameMap);
        
        return "order/return-manage";
    }

    // Hiển thị trang chi tiết của một yêu cầu trả hàng
    @GetMapping("/{id}")
    public String detailPage(@PathVariable Long id, Model model) {
        try {
            ReturnRequest detailRequest = returnRequestService.getById(id);
            model.addAttribute("detailRequest", detailRequest);
            
            Map<Long, String> employeeNameMap = new HashMap<>();
            if (detailRequest.getRequestedBy() != null) {
                employeeRepository.findById(detailRequest.getRequestedBy()).ifPresent(e -> employeeNameMap.put(detailRequest.getRequestedBy(), e.getFullName()));
            }
            if (detailRequest.getReviewedBy() != null) {
                employeeRepository.findById(detailRequest.getReviewedBy()).ifPresent(e -> employeeNameMap.put(detailRequest.getReviewedBy(), e.getFullName()));
            }
            model.addAttribute("employeeNameMap", employeeNameMap);
            
            return "order/return-detail";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/return/manage";
        }
    }

    // Duyệt (chấp nhận) yêu cầu trả hàng
    @PostMapping("/{id}/approve")
    public String approveRequest(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            SessionUser user = getCurrentUser(session);
            Long employeeId = user.getId();
            returnRequestService.approveRequest(id, employeeId);
            redirectAttributes.addFlashAttribute("success", "Đã duyệt yêu cầu #" + id + " thành công!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/return/" + id;
    }

    // Từ chối yêu cầu trả hàng
    @PostMapping("/{id}/reject")
    public String rejectRequest(@PathVariable Long id,
            @RequestParam("reason") String reason,
            HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            SessionUser user = getCurrentUser(session);
            Long employeeId = user.getId();
            returnRequestService.rejectRequest(id, employeeId, reason);
            redirectAttributes.addFlashAttribute("success", "Đã từ chối yêu cầu #" + id);
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/return/" + id;
    }
}
