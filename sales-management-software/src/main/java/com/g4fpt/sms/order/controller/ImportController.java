package com.g4fpt.sms.order.controller;

import com.g4fpt.sms.auth.security.CustomUserDetails;
import com.g4fpt.sms.branch.repository.BranchRepository;
import com.g4fpt.sms.order.dto.ImportRequest;
import com.g4fpt.sms.order.service.ImportService;
import com.g4fpt.sms.product.entity.ProductUnit;
import com.g4fpt.sms.product.enums.ProductStatus;
import com.g4fpt.sms.product.repository.ProductUnitRepository;
import com.g4fpt.sms.supplier.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/imports")
public class ImportController {

    private final ImportService importService;
    private final BranchRepository branchRepository;
    private final SupplierRepository supplierRepository;
    private final ProductUnitRepository productUnitRepository;

    // --- 1. Danh sách yêu cầu nhập hàng: GET /imports ---
    @GetMapping
    public String listImports(@RequestParam(required = false) String status,
                              @RequestParam(required = false) String keyword,
                              Model model) {
        model.addAttribute("page", "imports");
        model.addAttribute("imports", importService.getAllImports(status, keyword));
        model.addAttribute("keyword", keyword != null ? keyword : "");
        model.addAttribute("filter", status != null ? status : "");
        return "imports/list";
    }

    // --- 2. Màn hình Tạo phiếu nhập hàng: GET /imports/create ---
    @GetMapping("/create")
    public String showCreateForm(Model model,
                                 @AuthenticationPrincipal CustomUserDetails userDetails,
                                 @ModelAttribute("importRequest") ImportRequest importRequest) {
        if (userDetails == null) {
            throw new AccessDeniedException("Bạn cần đăng nhập để thực hiện chức năng này!");
        }
        if (!userDetails.hasRole("BRANCH_MANAGER") && !userDetails.hasRole("WAREHOUSE_STAFF")) {
            throw new AccessDeniedException("Chỉ Quản lý chi nhánh hoặc Nhân viên kho mới được phép tạo phiếu nhập hàng!");
        }

        model.addAttribute("page", "imports");

        // Nếu là chi nhánh (BRANCH_MANAGER/WAREHOUSE_STAFF) có liên kết chi nhánh, tự điền chi nhánh
        if (userDetails.getBranchId() != null) {
            importRequest.setBranchId(userDetails.getBranchId());
        }

        model.addAttribute("branches", branchRepository.findAll());
        model.addAttribute("suppliers", supplierRepository.findAll());

        // Lấy supplierId từ model attribute để lọc ProductUnit
        Long supplierId = importRequest.getSupplierId();
        if (supplierId != null) {
            List<ProductUnit> historyUnits = productUnitRepository.findProductUnitsBySupplierImportHistory(supplierId);
            if (historyUnits.isEmpty()) {
                historyUnits = productUnitRepository.findByProduct_Status(ProductStatus.ACTIVE);
            }
            model.addAttribute("productUnits", historyUnits);
        } else {
            model.addAttribute("productUnits", java.util.Collections.emptyList());
        }

        // Gửi supplierId xuống template để giữ trạng thái đã chọn
        model.addAttribute("selectedSupplierId", supplierId);

        return "imports/form";
    }




    // --- 3. Lưu yêu cầu nhập hàng: POST /imports/save ---
    @PostMapping("/save")
    public String saveImportRequest(@ModelAttribute ImportRequest importRequest,
                                    @AuthenticationPrincipal CustomUserDetails userDetails,
                                    RedirectAttributes redirectAttributes) {
        try {
            if (userDetails == null) {
                throw new AccessDeniedException("Bạn cần đăng nhập để thực hiện chức năng này!");
            }
            if (!userDetails.hasRole("BRANCH_MANAGER") && !userDetails.hasRole("WAREHOUSE_STAFF")) {
                throw new AccessDeniedException("Chỉ Quản lý chi nhánh hoặc Nhân viên kho mới được phép tạo phiếu nhập hàng!");
            }
            // Lấy ID nhân viên hiện tại đang đăng nhập
            Long employeeId = userDetails.getEmployee().getId();

            importService.createImportRequest(importRequest, employeeId);
            redirectAttributes.addFlashAttribute("successMessage", "Gửi yêu cầu nhập hàng thành công! Đang chờ OWNER phê duyệt.");
            return "redirect:/imports";
        } catch (IllegalArgumentException | AccessDeniedException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/imports/create";
        }
    }

    // --- 4. Chi tiết phiếu nhập: GET /imports/{id} ---
    @GetMapping("/{id}")
    public String detailImport(@PathVariable Long id, Model model) {
        model.addAttribute("page", "imports");
        model.addAttribute("importTx", importService.getImportById(id));
        model.addAttribute("details", importService.getImportDetails(id));
        return "imports/detail";
    }

    // --- 5. OWNER duyệt phiếu: POST /imports/{id}/approve ---
    @PostMapping("/{id}/approve")
    public String approveImport(@PathVariable Long id,
                                @AuthenticationPrincipal CustomUserDetails userDetails,
                                RedirectAttributes redirectAttributes) {
        try {
            if (userDetails == null) {
                throw new AccessDeniedException("Bạn cần đăng nhập để thực hiện chức năng này!");
            }
            if (!userDetails.hasRole("OWNER")) {
                throw new AccessDeniedException("Chỉ Chủ cửa hàng (OWNER) mới có quyền duyệt phiếu nhập!");
            }
            importService.approveImportRequest(id);
            redirectAttributes.addFlashAttribute("successMessage", "Nhập hàng thành công. Hàng tồn kho đã được cập nhật!");
        } catch (IllegalArgumentException | AccessDeniedException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/imports";
    }

    // --- 6. OWNER từ chối phiếu: POST /imports/{id}/reject ---
    @PostMapping("/{id}/reject")
    public String rejectImport(@PathVariable Long id,
                               @RequestParam(required = false) String reason,
                               @AuthenticationPrincipal CustomUserDetails userDetails,
                               RedirectAttributes redirectAttributes) {
        try {
            if (userDetails == null) {
                throw new AccessDeniedException("Bạn cần đăng nhập để thực hiện chức năng này!");
            }
            if (!userDetails.hasRole("OWNER")) {
                throw new AccessDeniedException("Chỉ Chủ cửa hàng (OWNER) mới có quyền từ chối phiếu nhập!");
            }
            importService.rejectImportRequest(id, reason);
            redirectAttributes.addFlashAttribute("successMessage", "Yêu cầu nhập hàng đã bị từ chối.");
        } catch (IllegalArgumentException | AccessDeniedException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/imports";
    }
}
