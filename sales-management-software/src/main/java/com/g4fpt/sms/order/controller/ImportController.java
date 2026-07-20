package com.g4fpt.sms.order.controller;

import com.g4fpt.sms.auth.dto.SessionUser;
import com.g4fpt.sms.auth.util.SessionConstants;
import com.g4fpt.sms.branch.repository.BranchRepository;
import com.g4fpt.sms.order.dto.ImportRequest;
import com.g4fpt.sms.order.service.ImportService;
import com.g4fpt.sms.product.dto.request.ProductFilterRequest;
import com.g4fpt.sms.product.dto.response.ProductResponse;
import com.g4fpt.sms.product.enums.ProductStatus;
import com.g4fpt.sms.product.repository.ProductUnitRepository;
import com.g4fpt.sms.product.service.ProductService;
import com.g4fpt.sms.supplier.repository.SupplierRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
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
    private final ProductService productService;

    // Lấy thông tin người dùng đăng nhập từ session
    private SessionUser getSessionUser(HttpSession session) {
        return (SessionUser) session.getAttribute(SessionConstants.LOGGED_IN_USER);
    }

    // Hiển thị danh sách yêu cầu nhập hàng
    @GetMapping
    public String listImports(HttpSession session,
                              @RequestParam(required = false) String status,
                              @RequestParam(required = false) String keyword,
                              Model model) {
        model.addAttribute("page", "imports");
        model.addAttribute("imports", importService.getAllImports(status, keyword));
        model.addAttribute("keyword", keyword != null ? keyword : "");
        model.addAttribute("filter", status != null ? status : "");
        return "imports/list";
    }

    // Hiển thị giao diện tạo yêu cầu nhập hàng cho Quản lý chi nhánh hoặc Nhân viên kho
    @GetMapping("/create")
    public String showCreateForm(HttpSession session,
                                 Model model,
                                 @ModelAttribute("importRequest") ImportRequest importRequest) {
        SessionUser sessionUser = getSessionUser(session);

        if (!sessionUser.hasAnyRole("OWNER", "WAREHOUSE_STAFF")) {
            return "redirect:/error/403";
        }

        model.addAttribute("page", "imports");

        // Tự động chọn chi nhánh nếu nhân viên đã thuộc chi nhánh cụ thể
        if (sessionUser.getBranchId() != null) {
            importRequest.setBranchId(sessionUser.getBranchId());
        }

        model.addAttribute("branches", branchRepository.findAll());
        model.addAttribute("suppliers", supplierRepository.findAll());

        // Lấy danh sách sản phẩm đang hoạt động để đưa vào dropdown lựa chọn
        ProductFilterRequest filter = new ProductFilterRequest();
        filter.setStatus(ProductStatus.ACTIVE);
        List<ProductResponse> activeProducts = productService.findAll(filter, 0, 10000, "name", "asc").getContent();
        model.addAttribute("activeProducts", activeProducts);

        return "imports/form";
    }

    // Lưu yêu cầu nhập hàng mới
    @PostMapping("/save")
    public String saveImportRequest(HttpSession session,
                                    @ModelAttribute ImportRequest importRequest,
                                    RedirectAttributes redirectAttributes) {
        SessionUser sessionUser = getSessionUser(session);

        try {
            if (!sessionUser.hasAnyRole("OWNER", "WAREHOUSE_STAFF")) {
                redirectAttributes.addFlashAttribute("errorMessage", "Chỉ OWNER hoặc Nhân viên kho mới được phép tạo phiếu nhập hàng!");
                return "redirect:/imports";
            }

            // Lấy ID nhân viên trực tiếp từ session
            Long employeeId = sessionUser.getId();

            importService.createImportRequest(importRequest, employeeId);
            redirectAttributes.addFlashAttribute("successMessage", "Gửi yêu cầu nhập hàng thành công! Đang chờ phê duyệt.");
            return "redirect:/imports";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/imports/create";
        }
    }

    // Xem chi tiết một phiếu nhập hàng
    @GetMapping("/{id}")
    public String detailImport(@PathVariable Long id,
                               Model model) {
        model.addAttribute("page", "imports");
        model.addAttribute("importTx", importService.getImportById(id));
        model.addAttribute("details", importService.getImportDetails(id));
        return "imports/detail";
    }

    // Duyệt yêu cầu nhập hàng và cập nhật tồn kho
    @PostMapping("/{id}/approve")
    public String approveImport(HttpSession session,
                                @PathVariable Long id,
                                RedirectAttributes redirectAttributes) {
        SessionUser sessionUser = getSessionUser(session);

        try {
            // Chỉ Owner hoặc Nhân viên kho mới được phép duyệt phiếu
            if (!sessionUser.hasAnyRole("OWNER", "WAREHOUSE_STAFF")) {
                redirectAttributes.addFlashAttribute("errorMessage", "Chỉ OWNER hoặc Nhân viên kho mới có quyền duyệt phiếu nhập!");
                return "redirect:/imports/" + id;
            }
            importService.approveImportRequest(id);
            redirectAttributes.addFlashAttribute("successMessage", "Nhập hàng thành công. Hàng tồn kho đã được cập nhật!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/imports";
    }

    // Từ chối yêu cầu nhập hàng
    @PostMapping("/{id}/reject")
    public String rejectImport(HttpSession session,
                               @PathVariable Long id,
                               @RequestParam(required = false) String reason,
                               RedirectAttributes redirectAttributes) {
        SessionUser sessionUser = getSessionUser(session);

        try {
            // Chỉ Owner hoặc Nhân viên kho mới được phép từ chối phiếu
            if (!sessionUser.hasAnyRole("OWNER", "WAREHOUSE_STAFF")) {
                redirectAttributes.addFlashAttribute("errorMessage", "Chỉ OWNER hoặc Nhân viên kho mới có quyền từ chối phiếu nhập!");
                return "redirect:/imports/" + id;
            }
            importService.rejectImportRequest(id, reason);
            redirectAttributes.addFlashAttribute("successMessage", "Yêu cầu nhập hàng đã bị từ chối.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/imports";
    }
}
