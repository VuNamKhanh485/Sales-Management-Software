package com.g4fpt.sms.inventory.controller;

import com.g4fpt.sms.auth.dto.SessionUser;
import com.g4fpt.sms.auth.util.SessionConstants;
import com.g4fpt.sms.branch.entity.Branch;
import com.g4fpt.sms.branch.repository.BranchRepository;
import com.g4fpt.sms.inventory.dto.InventoryBranchSummaryResponse;
import com.g4fpt.sms.inventory.dto.InventoryDetailResponse;
import com.g4fpt.sms.inventory.dto.InventoryRequest;
import com.g4fpt.sms.inventory.service.InventoryService;
import com.g4fpt.sms.product.repository.ProductUnitRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;
    private final BranchRepository branchRepository;
    private final ProductUnitRepository productUnitRepository;

    // --- Lấy SessionUser từ HttpSession ---
    private SessionUser getSessionUser(HttpSession session) {
        return (SessionUser) session.getAttribute(SessionConstants.LOGGED_IN_USER);
    }

    // --- Kiểm tra quyền truy cập chi nhánh ---
    // OWNER: xem tất cả. BRANCH_MANAGER / WAREHOUSE_STAFF: chỉ xem chi nhánh của mình.
    private boolean canAccessBranch(SessionUser sessionUser, Long targetBranchId) {
        if (sessionUser.hasRole("OWNER")) {
            return true;
        }
        Long userBranchId = sessionUser.getBranchId();
        return userBranchId != null && userBranchId.equals(targetBranchId);
    }

    // =========================================================
    // GET /inventory — Danh sách kho
    // =========================================================
    @GetMapping
    public String listInventoryBranches(HttpSession session,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "5") int size,
                                        Model model) {
        SessionUser sessionUser = getSessionUser(session);

        // OWNER: xem danh sách tất cả các kho
        if (sessionUser.hasRole("OWNER")) {
            Pageable pageable = PageRequest.of(page, size);
            Page<InventoryBranchSummaryResponse> branchesPage = inventoryService.getInventorySummaryByBranch(pageable);
            model.addAttribute("branchesPage", branchesPage);
            model.addAttribute("branches", branchesPage.getContent());
            model.addAttribute("size", size);
            model.addAttribute("page", "inventory");
            return "inventory/list";
        }

        // BRANCH_MANAGER hoặc WAREHOUSE_STAFF: tự động chuyển về kho của chi nhánh mình
        if (sessionUser.hasAnyRole("BRANCH_MANAGER", "WAREHOUSE_STAFF")) {
            Long userBranchId = sessionUser.getBranchId();
            if (userBranchId != null) {
                return "redirect:/inventory/" + userBranchId;
            }
        }

        // Các role khác (SALE_STAFF) không được vào — AuthInterceptor đã chặn trước
        return "redirect:/error/403";
    }

    // =========================================================
    // GET /inventory/{branchId} — Chi tiết kho một chi nhánh
    // =========================================================
    @GetMapping("/{branchId}")
    public String detailInventoryBranch(HttpSession session,
                                        @PathVariable Long branchId,
                                        @RequestParam(required = false) String keyword,
                                        @RequestParam(required = false) String filter,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "5") int size,
                                        Model model) {
        SessionUser sessionUser = getSessionUser(session);

        // Kiểm tra quyền: nếu không phải chi nhánh của mình thì redirect về chi nhánh của mình
        if (!canAccessBranch(sessionUser, branchId)) {
            Long userBranchId = sessionUser.getBranchId();
            if (userBranchId != null) {
                return "redirect:/inventory/" + userBranchId;
            }
            return "redirect:/error/403";
        }

        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy chi nhánh với ID: " + branchId));
        model.addAttribute("branch", branch);

        Pageable pageable = PageRequest.of(page, size);
        Page<InventoryDetailResponse> inventoryPage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            inventoryPage = inventoryService.searchInventory(branchId, keyword, pageable);
        } else if ("low_stock".equals(filter)) {
            inventoryPage = inventoryService.getLowStockInventory(branchId, pageable);
        } else {
            inventoryPage = inventoryService.getInventoryDetailByBranchId(branchId, pageable);
        }

        model.addAttribute("inventoryPage", inventoryPage);
        model.addAttribute("inventoryDetails", inventoryPage.getContent());
        model.addAttribute("keyword", keyword != null ? keyword : "");
        model.addAttribute("filter", filter != null ? filter : "");
        model.addAttribute("size", size);
        model.addAttribute("page", "inventory");

        return "inventory/detail";
    }

    // =========================================================
    // GET /inventory/edit/{id} — Hiển thị form sửa thông tin kho
    // (Chỉ cho phép sửa minStock, maxStock, positionInShop)
    // =========================================================
    @GetMapping("/edit/{id}")
    public String showEditForm(HttpSession session,
                               @PathVariable Long id,
                               Model model) {
        SessionUser sessionUser = getSessionUser(session);

        InventoryRequest request = inventoryService.getInventoryRequestById(id);

        // Kiểm tra quyền: chỉ được sửa kho của chi nhánh mình
        if (!canAccessBranch(sessionUser, request.getBranchId())) {
            return "redirect:/error/403";
        }

        model.addAttribute("inventoryRequest", request);
        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy chi nhánh"));
        model.addAttribute("branch", branch);
        model.addAttribute("productUnit", productUnitRepository.findById(request.getProductUnitId()).orElse(null));
        model.addAttribute("page", "inventory");
        return "inventory/form";
    }

    // =========================================================
    // POST /inventory/update/{id} — Cập nhật thông tin kho
    // =========================================================
    @PostMapping("/update/{id}")
    public String updateInventory(HttpSession session,
                                  @PathVariable Long id,
                                  @ModelAttribute InventoryRequest request,
                                  RedirectAttributes redirectAttributes) {
        SessionUser sessionUser = getSessionUser(session);

        try {
            // Lấy lại branchId thực tế của bản ghi để tránh bị bypass
            InventoryRequest existing = inventoryService.getInventoryRequestById(id);
            if (!canAccessBranch(sessionUser, existing.getBranchId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Bạn không có quyền chỉnh sửa kho của chi nhánh khác!");
                return "redirect:/inventory";
            }

            inventoryService.updateInventory(id, request);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật tồn kho thành công!");
            return "redirect:/inventory/" + existing.getBranchId();
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/inventory/edit/" + id;
        }
    }

    // =========================================================
    // GET /inventory/delete/{id} — Xóa bản ghi kho (chỉ OWNER)
    // =========================================================
    @GetMapping("/delete/{id}")
    public String deleteInventory(HttpSession session,
                                  @PathVariable Long id,
                                  RedirectAttributes redirectAttributes) {
        SessionUser sessionUser = getSessionUser(session);

        try {
            InventoryRequest request = inventoryService.getInventoryRequestById(id);

            // Chỉ OWNER mới được xóa bản ghi kho
            if (!sessionUser.hasRole("OWNER")) {
                redirectAttributes.addFlashAttribute("errorMessage", "Chỉ OWNER mới có quyền xóa bản ghi kho!");
                return "redirect:/inventory/" + request.getBranchId();
            }

            Long branchId = request.getBranchId();
            inventoryService.deleteInventory(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa tồn kho thành công!");
            return "redirect:/inventory/" + branchId;
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/inventory";
        }
    }
}
