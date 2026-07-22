package com.g4fpt.sms.order.controller;

import com.g4fpt.sms.auth.dto.SessionUser;
import com.g4fpt.sms.auth.util.SessionConstants;
import com.g4fpt.sms.branch.repository.BranchRepository;
import com.g4fpt.sms.order.dto.ImportItemRequest;
import com.g4fpt.sms.order.dto.ImportRequest;
import com.g4fpt.sms.order.dto.ImportResponse;
import com.g4fpt.sms.order.service.ImportService;
import com.g4fpt.sms.product.dto.request.ProductFilterRequest;
import com.g4fpt.sms.product.dto.response.ProductResponse;
import com.g4fpt.sms.product.entity.ProductUnit;
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

import java.math.BigDecimal;
import java.util.ArrayList;
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
        SessionUser sessionUser = getSessionUser(session);
        Long userBranchId = sessionUser.hasRole("OWNER") ? null : sessionUser.getBranchId();

        model.addAttribute("page", "imports");
        model.addAttribute("imports", importService.getAllImports(status, keyword, userBranchId));
        model.addAttribute("keyword", keyword != null ? keyword : "");
        model.addAttribute("filter", status != null ? status : "");
        return "imports/list";
    }

    // Hiển thị giao diện tạo yêu cầu nhập hàng cho Nhân viên kho
    @GetMapping("/create")
    public String showCreateForm(HttpSession session,
            Model model,
            @RequestParam(required = false) Boolean reset) {
        SessionUser sessionUser = getSessionUser(session);

        if (!sessionUser.hasAnyRole("OWNER", "WAREHOUSE_STAFF")) {
            return "redirect:/error/403";
        }

        model.addAttribute("page", "imports");

        ImportRequest draft = (ImportRequest) session.getAttribute("draftImportRequest");
        if (draft == null || Boolean.TRUE.equals(reset)) {
            draft = new ImportRequest();
            if (sessionUser.getBranchId() != null) {
                draft.setBranchId(sessionUser.getBranchId());
            }
            session.setAttribute("draftImportRequest", draft);
        }

        model.addAttribute("importRequest", draft);
        model.addAttribute("branches", branchRepository.findAll());
        model.addAttribute("suppliers", supplierRepository.findAll());

        // Lấy danh sách sản phẩm theo nhà cung cấp đã chọn
        List<ProductResponse> activeProducts = new ArrayList<>();
        if (draft.getSupplierId() != null) {
            ProductFilterRequest filter = new ProductFilterRequest();
            filter.setStatus(ProductStatus.ACTIVE);
            List<ProductResponse> allProducts = productService.findAll(filter, 0, 10000, "name", "asc").getContent();

            final Long selectedSupplierId = draft.getSupplierId();
            activeProducts = allProducts.stream()
                    .filter(p -> p.getSupplierIds() != null && p.getSupplierIds().contains(selectedSupplierId))
                    .toList();
        }

        model.addAttribute("activeProducts", activeProducts);

        // Tính tổng
        int totalQty = draft.getItems().stream().mapToInt(ImportItemRequest::getQuantity).sum();
        long totalSKUs = draft.getItems().size();
        BigDecimal totalAmount = draft.getItems().stream()
                .map(item -> item.getImportPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, java.math.BigDecimal::add);

        model.addAttribute("totalQty", totalQty);
        model.addAttribute("totalSKUs", totalSKUs);
        model.addAttribute("totalAmount", totalAmount);

        return "imports/form";
    }

    @PostMapping("/create/filter")
    public String filterSupplier(HttpSession session, @RequestParam(required = false) Long supplierId,
            @RequestParam(required = false) Long branchId, @RequestParam(required = false) String note) {
        ImportRequest draft = (ImportRequest) session.getAttribute("draftImportRequest");
        if (draft != null) {
            draft.setSupplierId(supplierId);
            draft.setBranchId(branchId);
            draft.setNote(note);
        }
        return "redirect:/imports/create";
    }

    @PostMapping("/create/add-item")
    public String addItem(HttpSession session,
            @RequestParam(required = false) Long productUnitId,
            @RequestParam(required = false) Integer quantity,
            @RequestParam(required = false) java.math.BigDecimal importPrice,
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false) Long supplierId,
            @RequestParam(required = false) String note,
            RedirectAttributes redirectAttributes) {
        ImportRequest draft = (ImportRequest) session.getAttribute("draftImportRequest");
        if (draft != null) {
            draft.setBranchId(branchId);
            draft.setSupplierId(supplierId);
            draft.setNote(note);
        }

        if (productUnitId == null || quantity == null || importPrice == null) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Vui lòng chọn sản phẩm, đơn vị, số lượng và giá nhập hợp lệ.");
            return "redirect:/imports/create";
        }

        if (draft != null) {
            ProductUnit unit = productUnitRepository.findById(productUnitId).orElse(null);
            if (unit != null) {
                ImportItemRequest item = new ImportItemRequest();
                item.setProductUnitId(productUnitId);
                item.setQuantity(quantity);
                item.setImportPrice(importPrice);
                item.setProductName(unit.getProduct().getName());
                item.setSku(unit.getSku());
                item.setUnitName(unit.getUnit().getName());
                item.setLineTotal(importPrice.multiply(java.math.BigDecimal.valueOf(quantity)));
                draft.getItems().add(item);
            }
        }
        return "redirect:/imports/create";
    }

    @PostMapping("/create/remove-item/{index}")
    public String removeItem(HttpSession session, @PathVariable int index,
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false) Long supplierId,
            @RequestParam(required = false) String note) {
        ImportRequest draft = (ImportRequest) session.getAttribute("draftImportRequest");
        if (draft != null) {
            draft.setBranchId(branchId);
            draft.setSupplierId(supplierId);
            draft.setNote(note);

            if (index >= 0 && index < draft.getItems().size()) {
                draft.getItems().remove(index);
            }
        }
        return "redirect:/imports/create";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        SessionUser sessionUser = getSessionUser(session);
        try {
            ImportResponse tx = importService.getImportById(id);
            if (!sessionUser.hasRole("OWNER") && !sessionUser.getBranchId().equals(tx.getBranchId())) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Bạn không có quyền sửa phiếu nhập của chi nhánh khác!");
                return "redirect:/imports";
            }
            ImportRequest draft = importService.loadImportRequestForEdit(id);
            session.setAttribute("draftImportRequest", draft);
            return "redirect:/imports/create";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/imports/" + id;
        }
    }

    // Lưu yêu cầu nhập hàng mới hoặc cập nhật
    @PostMapping("/save")
    public String saveImportRequest(HttpSession session, RedirectAttributes redirectAttributes,
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false) Long supplierId,
            @RequestParam(required = false) String note) {
        SessionUser sessionUser = getSessionUser(session);
        ImportRequest draft = (ImportRequest) session.getAttribute("draftImportRequest");

        if (draft == null || draft.getItems().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Phiếu nhập hàng trống!");
            return "redirect:/imports/create";
        }

        // Update final details before saving
        if (sessionUser.hasRole("OWNER")) {
            if (branchId != null)
                draft.setBranchId(branchId);
        } else {
            draft.setBranchId(sessionUser.getBranchId());
        }
        if (supplierId != null)
            draft.setSupplierId(supplierId);
        if (note != null)
            draft.setNote(note);

        try {
            if (!sessionUser.hasAnyRole("OWNER", "WAREHOUSE_STAFF")) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Chỉ OWNER hoặc Nhân viên kho mới được phép thao tác phiếu nhập hàng!");
                return "redirect:/imports";
            }

            Long employeeId = sessionUser.getId();
            if (draft.getId() != null) {
                importService.updateImportRequest(draft.getId(), draft, employeeId);
                redirectAttributes.addFlashAttribute("successMessage", "Cập nhật phiếu nhập hàng thành công!");
            } else {
                importService.createImportRequest(draft, employeeId);
                redirectAttributes.addFlashAttribute("successMessage",
                        "Gửi yêu cầu nhập hàng thành công! Đang chờ phê duyệt.");
            }
            session.removeAttribute("draftImportRequest");

            return "redirect:/imports";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/imports/create";
        }
    }

    // Xem chi tiết một phiếu nhập hàng
    @GetMapping("/{id}")
    public String detailImport(HttpSession session,
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes) {
        SessionUser sessionUser = getSessionUser(session);
        ImportResponse tx = importService.getImportById(id);

        if (!sessionUser.hasRole("OWNER") && !sessionUser.getBranchId().equals(tx.getBranchId())) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Bạn không có quyền xem phiếu nhập của chi nhánh khác!");
            return "redirect:/imports";
        }

        model.addAttribute("page", "imports");
        model.addAttribute("importTx", tx);
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
            // Chỉ Owner hoặc Quản lý chi nhánh mới được phép duyệt phiếu
            if (!sessionUser.hasAnyRole("OWNER", "BRANCH_MANAGER")) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Chỉ OWNER hoặc Quản lý chi nhánh mới có quyền duyệt phiếu nhập!");
                return "redirect:/imports/" + id;
            }

            ImportResponse tx = importService.getImportById(id);
            if (!sessionUser.hasRole("OWNER") && !sessionUser.getBranchId().equals(tx.getBranchId())) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Bạn không có quyền duyệt phiếu nhập của chi nhánh khác!");
                return "redirect:/imports/" + id;
            }

            importService.approveImportRequest(id);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Nhập hàng thành công. Hàng tồn kho đã được cập nhật!");
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
            // Chỉ Owner, Quản lý chi nhánh hoặc Nhân viên kho mới được phép từ chối/hủy
            // phiếu
            if (!sessionUser.hasAnyRole("OWNER", "BRANCH_MANAGER", "WAREHOUSE_STAFF")) {
                redirectAttributes.addFlashAttribute("errorMessage", "Bạn không có quyền hủy phiếu nhập!");
                return "redirect:/imports/" + id;
            }

            ImportResponse tx = importService.getImportById(id);
            if (!sessionUser.hasRole("OWNER") && !sessionUser.getBranchId().equals(tx.getBranchId())) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Bạn không có quyền hủy phiếu nhập của chi nhánh khác!");
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
