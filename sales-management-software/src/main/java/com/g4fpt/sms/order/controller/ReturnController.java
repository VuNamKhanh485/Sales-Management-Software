package com.g4fpt.sms.order.controller;

import com.g4fpt.sms.order.entity.OrderTransaction;
import com.g4fpt.sms.order.entity.ReturnRequest;
import com.g4fpt.sms.order.repository.OrderTransactionRepository;
import com.g4fpt.sms.order.service.ReturnRequestService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import com.g4fpt.sms.auth.security.CustomUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;

@Controller
@RequestMapping("/return")
@RequiredArgsConstructor
public class ReturnController {

    private final ReturnRequestService returnRequestService;
    private final OrderTransactionRepository orderTransactionRepository;

    private CustomUserDetails getCurrentUser() {
        return (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    // ---------- Trang tạo yêu cầu trả hàng (cho nhân viên POS) ----------
    @GetMapping
    public String returnPage(@RequestParam(required = false) String orderCode, Model model) {
        model.addAttribute("autoOrderCode", orderCode != null && !orderCode.isBlank() ? orderCode : "");
        return "order/return-request";
    }

    // ---------- API: tìm đơn hàng theo mã ----------
    @GetMapping("/api/search-order")
    @ResponseBody
    public ResponseEntity<?> searchOrder(@RequestParam("code") String code) {
        try {
            OrderTransaction order = returnRequestService.searchOrderByCode(code);
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("id", order.getId());
            data.put("code", order.getCode());
            data.put("createdAt", order.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            data.put("customerName", order.getCustomer() != null ? order.getCustomer().getFullName() : "Khách lẻ");
            data.put("status", order.getStatus());

            List<Map<String, Object>> details = new ArrayList<>();
            for (var d : order.getDetails()) {
                Map<String, Object> det = new LinkedHashMap<>();
                det.put("id", d.getId());
                det.put("productName", d.getProductUnit().getProduct().getName());
                det.put("unitName", d.getProductUnit().getUnit() != null ? d.getProductUnit().getUnit().getName() : "");
                det.put("quantity", d.getQuantity());
                det.put("salePrice", d.getSalePrice());
                det.put("totalAmount", d.getTotalAmount());
                details.add(det);
            }
            data.put("details", details);

            return ResponseEntity.ok(data);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ---------- API: tạo yêu cầu trả hàng ----------
    @PostMapping("/api/create")
    @ResponseBody
    public ResponseEntity<?> createReturnRequest(
            @RequestParam("orderId") Long orderId,
            @RequestParam("reason") String reason,
            @RequestParam(value = "detailIds", required = false) String detailIdsStr,
            @RequestParam(value = "quantities", required = false) String quantitiesStr,
            @RequestParam(value = "images", required = false) List<MultipartFile> images,
            HttpSession session) {

        try {
            CustomUserDetails user = getCurrentUser();
            Long employeeId = user.getEmployee().getId();

            // Parse detailIds và quantities
            List<ReturnRequestService.ReturnItemInput> items = new ArrayList<>();
            if (detailIdsStr != null && quantitiesStr != null) {
                String[] ids = detailIdsStr.split(",");
                String[] qtys = quantitiesStr.split(",");
                for (int i = 0; i < ids.length; i++) {
                    items.add(new ReturnRequestService.ReturnItemInput(
                            Long.parseLong(ids[i]), Integer.parseInt(qtys[i])));
                }
            }

            if (items.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Chọn ít nhất 1 sản phẩm để trả"));
            }

            // Get branchId from order
            OrderTransaction order = orderTransactionRepository.findByIdWithDetails(orderId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
            Long branchId = order.getBranchId();

            // Upload ảnh
            List<String> imageUrls = new ArrayList<>();
            if (images != null) {
                String uploadDir = "src/main/resources/static/uploads/returns/";
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                for (MultipartFile file : images) {
                    if (!file.isEmpty()) {
                        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                        Path filePath = uploadPath.resolve(fileName);
                        Files.copy(file.getInputStream(), filePath);
                        imageUrls.add("/uploads/returns/" + fileName);
                    }
                }
            }

            ReturnRequest request = returnRequestService.createReturnRequest(
                    orderId, branchId, employeeId, reason, items, imageUrls);

            return ResponseEntity.ok(Map.of("success", true, "id", request.getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ---------- Trang quản lý yêu cầu (cho Shop Manager) ----------
    @GetMapping("/manage")
    public String managePage(Model model) {
        List<ReturnRequest> requests = returnRequestService.getAllRequests();
        model.addAttribute("requests", requests);
        model.addAttribute("pendingCount", returnRequestService.countPendingRequests());
        return "order/return-manage";
    }

    // ---------- API: lấy chi tiết yêu cầu ----------
    @GetMapping("/api/requests/{id}")
    @ResponseBody
    public ResponseEntity<?> getRequestDetail(@PathVariable Long id) {
        try {
            ReturnRequest req = returnRequestService.getById(id);
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("id", req.getId());
            data.put("reason", req.getReason());
            data.put("status", req.getStatus());
            data.put("createdAt", req.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            data.put("orderCode", req.getOrder().getCode());
            data.put("customerName", req.getOrder().getCustomer() != null ? req.getOrder().getCustomer().getFullName() : "Khách lẻ");

            List<Map<String, Object>> items = new ArrayList<>();
            for (var item : req.getItems()) {
                Map<String, Object> it = new LinkedHashMap<>();
                it.put("productName", item.getProductUnit().getProduct().getName());
                it.put("quantity", item.getQuantity());
                it.put("salePrice", item.getSalePrice());
                items.add(it);
            }
            data.put("items", items);

            List<String> images = req.getImages().stream()
                    .map(img -> img.getImageUrl())
                    .collect(Collectors.toList());
            data.put("images", images);

            if (req.getReviewedBy() != null) {
                data.put("reviewedBy", req.getReviewedBy());
                data.put("reviewedAt", req.getReviewedAt() != null ?
                        req.getReviewedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : null);
                data.put("rejectReason", req.getRejectReason());
            }

            return ResponseEntity.ok(data);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ---------- API: duyệt yêu cầu ----------
    @PostMapping("/api/requests/{id}/approve")
    @ResponseBody
    public ResponseEntity<?> approveRequest(@PathVariable Long id, HttpSession session) {
        try {
            Long employeeId = getCurrentUser().getEmployee().getId();
            returnRequestService.approveRequest(id, employeeId);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ---------- API: từ chối yêu cầu ----------
    @PostMapping("/api/requests/{id}/reject")
    @ResponseBody
    public ResponseEntity<?> rejectRequest(@PathVariable Long id,
                                            @RequestParam("reason") String reason,
                                            HttpSession session) {
        try {
            Long employeeId = getCurrentUser().getEmployee().getId();
            returnRequestService.rejectRequest(id, employeeId, reason);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
