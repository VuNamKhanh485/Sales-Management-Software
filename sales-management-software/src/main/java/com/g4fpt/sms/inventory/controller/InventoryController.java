package com.g4fpt.sms.inventory.controller;

import com.g4fpt.sms.auth.security.CustomUserDetails;
import com.g4fpt.sms.branch.entity.Branch;
import com.g4fpt.sms.branch.repository.BranchRepository;
import com.g4fpt.sms.inventory.dto.InventoryBranchSummaryResponse;
import com.g4fpt.sms.inventory.dto.InventoryDetailResponse;
import com.g4fpt.sms.inventory.dto.InventoryRequest;
import com.g4fpt.sms.inventory.service.InventoryService;
import com.g4fpt.sms.product.repository.ProductRepository;
import com.g4fpt.sms.product.repository.ProductUnitRepository;
import com.g4fpt.sms.product.repository.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;
    private final BranchRepository branchRepository;
    private final ProductRepository productRepository;
    private final UnitRepository unitRepository;
    private final ProductUnitRepository productUnitRepository;

    // --- Danh sách kho: GET /inventory ---
    @GetMapping
    public String listInventoryBranches(@AuthenticationPrincipal CustomUserDetails userDetails,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "5") int size,
                                        Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        // Nếu là OWNER -> xem danh sách tất cả các kho
        if (userDetails.hasRole("OWNER")) {
            Pageable pageable = PageRequest.of(page, size);
            Page<InventoryBranchSummaryResponse> branchesPage = inventoryService.getInventorySummaryByBranch(pageable);
            model.addAttribute("branchesPage", branchesPage);
            model.addAttribute("branches", branchesPage.getContent());
            model.addAttribute("size", size);
            return "inventory/list";
        }

        // Nếu là BRANCH_MANAGER hoặc WAREHOUSE_STAFF -> tự động chuyển về kho của chi nhánh mình
        if (userDetails.hasRole("BRANCH_MANAGER") || userDetails.hasRole("WAREHOUSE_STAFF")) {
            Long userBranchId = userDetails.getBranchId();
            if (userBranchId != null) {
                return "redirect:/inventory/" + userBranchId;
            }
        }

        throw new AccessDeniedException("Bạn không có quyền truy cập trang này!");
    }

    //Chi tiết kho
    @GetMapping("/{branchId}")
    public String detailInventoryBranch(@AuthenticationPrincipal CustomUserDetails userDetails,
                                        @PathVariable Long branchId,
                                        @RequestParam(required = false) String keyword,
                                        @RequestParam(required = false) String filter,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "5") int size,
                                        Model model) {
        // Kiểm tra quyền: nếu không phải chi nhánh của mình thì redirect về chi nhánh của mình (hoặc ném 403)
        try {
            checkBranchAccess(userDetails, branchId);
        } catch (AccessDeniedException e) {
            if (userDetails != null && userDetails.getBranchId() != null) {
                return "redirect:/inventory/" + userDetails.getBranchId();
            }
            throw e;
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

        return "inventory/detail";
    }

    // --- Hiển thị form Thêm mới: GET /inventory/create ---
    @GetMapping("/create")
    public String showCreateForm(@AuthenticationPrincipal CustomUserDetails userDetails,
                                 @RequestParam(required = false) Long branchId, Model model) {
        // Nếu không phải OWNER thì ép target branchId về đúng chi nhánh của nhân viên đó
        if (userDetails != null && !userDetails.hasRole("OWNER")) {
            branchId = userDetails.getBranchId();
        }

        checkBranchAccess(userDetails, branchId);

        InventoryRequest request = new InventoryRequest();
        if (branchId != null) {
            request.setBranchId(branchId);
        }
        model.addAttribute("inventoryRequest", request);

        // Phân quyền truyền chi nhánh ra giao diện Form
        if (userDetails.hasRole("OWNER")) {
            model.addAttribute("branches", branchRepository.findAll());
        } else if (userDetails.getBranchId() != null) {
            Branch userBranch = branchRepository.findById(userDetails.getBranchId())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy chi nhánh của nhân viên."));
            model.addAttribute("userBranch", userBranch);
        }


        model.addAttribute("products", productRepository.findAll());
        model.addAttribute("units", unitRepository.findAll());
        return "inventory/form";
    }

    // --- Lưu thêm mới: POST /inventory/save ---
    @PostMapping("/save")
    public String saveInventory(@AuthenticationPrincipal CustomUserDetails userDetails,
                                @ModelAttribute InventoryRequest request,
                                RedirectAttributes redirectAttributes) {
        try {
            // Nếu người dùng không phải OWNER, bỏ qua branchId gửi từ client, lấy trực tiếp từ tài khoản đăng nhập
            if (userDetails != null && !userDetails.hasRole("OWNER")) {
                request.setBranchId(userDetails.getBranchId());
            }

            checkBranchAccess(userDetails, request.getBranchId());

            inventoryService.createInventory(request);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm tồn kho thành công!");
            return "redirect:/inventory/" + request.getBranchId();
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/inventory/create";
        } catch (AccessDeniedException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/inventory";
        }
    }


    // --- Hiển thị form Sửa: GET /inventory/edit/{id} ---
    @GetMapping("/edit/{id}")
    public String showEditForm(@AuthenticationPrincipal CustomUserDetails userDetails,
                               @PathVariable Long id, Model model) {
        InventoryRequest request = inventoryService.getInventoryRequestById(id);

        checkBranchAccess(userDetails, request.getBranchId());

        model.addAttribute("inventoryRequest", request);

        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy chi nhánh"));
        model.addAttribute("branch", branch);
        model.addAttribute("productUnit", productUnitRepository.findById(request.getProductUnitId()).orElse(null));
        return "inventory/form";
    }

    // --- Cập nhật: POST /inventory/update/{id} ---
    @PostMapping("/update/{id}")
    public String updateInventory(@AuthenticationPrincipal CustomUserDetails userDetails,
                                  @PathVariable Long id,
                                  @ModelAttribute InventoryRequest request,
                                  RedirectAttributes redirectAttributes) {
        try {
            // Lấy lại branchId thực tế của bản ghi để tránh bị bypass chỉnh sửa chi nhánh khác qua request param
            InventoryRequest existing = inventoryService.getInventoryRequestById(id);
            checkBranchAccess(userDetails, existing.getBranchId());

            inventoryService.updateInventory(id, request);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật tồn kho thành công!");
            return "redirect:/inventory/" + request.getBranchId();
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/inventory/edit/" + id;
        } catch (AccessDeniedException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/inventory";
        }
    }

    // --- Xóa: GET /inventory/delete/{id} ---
    @GetMapping("/delete/{id}")
    public String deleteInventory(@AuthenticationPrincipal CustomUserDetails userDetails,
                                  @PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            InventoryRequest request = inventoryService.getInventoryRequestById(id);
            checkBranchAccess(userDetails, request.getBranchId());

            Long branchId = request.getBranchId();
            inventoryService.deleteInventory(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa tồn kho thành công!");
            return "redirect:/inventory/" + branchId;
        } catch (AccessDeniedException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/inventory";
        }
    }

    // Helper check quyền truy cập chi nhánh
    private void checkBranchAccess(CustomUserDetails userDetails, Long targetBranchId) {
        if (userDetails == null) {
            throw new AccessDeniedException("Bạn cần đăng nhập để thực hiện chức năng này!");
        }
        // OWNER có quyền xem tất cả
        if (userDetails.hasRole("OWNER")) {
            return;
        }
        // BRANCH_MANAGER / WAREHOUSE_STAFF chỉ được xem kho của chi nhánh mình
        Long userBranchId = userDetails.getBranchId();
        if (userBranchId == null || !userBranchId.equals(targetBranchId)) {
            throw new AccessDeniedException("403 - Access Denied: Bạn không có quyền truy cập thông tin của chi nhánh khác!");
        }
    }

}
