package com.g4fpt.sms.order.controller;

import com.g4fpt.sms.auth.dto.SessionUser;
import com.g4fpt.sms.auth.util.SessionConstants;
import com.g4fpt.sms.branch.repository.BranchRepository;
import com.g4fpt.sms.inventory.entity.Inventory;
import com.g4fpt.sms.inventory.repository.InventoryRepository;
import com.g4fpt.sms.order.dto.TransferItemRequest;
import com.g4fpt.sms.order.dto.TransferRequest;
import com.g4fpt.sms.order.dto.TransferResponse;
import com.g4fpt.sms.order.service.TransferService;
import com.g4fpt.sms.product.dto.request.ProductFilterRequest;
import com.g4fpt.sms.product.dto.response.ProductResponse;
import com.g4fpt.sms.product.dto.response.ProductUnitResponse;
import com.g4fpt.sms.product.entity.ProductUnit;
import com.g4fpt.sms.product.enums.ProductStatus;
import com.g4fpt.sms.product.repository.ProductUnitRepository;
import com.g4fpt.sms.product.service.ProductService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Controller
@RequiredArgsConstructor
@RequestMapping("/transfer")
public class TransferController {

    private final TransferService transferService;
    private final BranchRepository branchRepository;
    private final ProductUnitRepository productUnitRepository;
    private final ProductService productService;
    private final InventoryRepository inventoryRepository;

    private SessionUser getSessionUser(HttpSession session) {
        return (SessionUser) session.getAttribute(SessionConstants.LOGGED_IN_USER);
    }

    @GetMapping
    public String listTransfers(HttpSession session,
            @RequestParam(required = false) Long fromBranchId,
            @RequestParam(required = false) Long toBranchId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        SessionUser sessionUser = getSessionUser(session);
        Long userBranchId = sessionUser.hasRole("OWNER") ? null : sessionUser.getBranchId();

        model.addAttribute("page", "transfer");
        model.addAttribute("branches", branchRepository.findAll());
        model.addAttribute("fromBranchId", fromBranchId);
        model.addAttribute("toBranchId", toBranchId);
        model.addAttribute("filterStatus", status);

        var pageRequest = PageRequest.of(page, size);
        model.addAttribute("transferPage",
                transferService.getTransfers(fromBranchId, toBranchId, status, userBranchId, pageRequest));

        return "transfer/list";
    }

    @GetMapping("/create")
    public String showCreateForm(HttpSession session, Model model, @RequestParam(required = false) Boolean reset) {
        SessionUser sessionUser = getSessionUser(session);

        if (!sessionUser.hasAnyRole("OWNER", "WAREHOUSE_STAFF")) {
            return "redirect:/error/403";
        }

        model.addAttribute("page", "transfer");

        TransferRequest draft = (TransferRequest) session.getAttribute("draftTransferRequest");
        if (draft == null || Boolean.TRUE.equals(reset)) {
            draft = new TransferRequest();
            if (sessionUser.getBranchId() != null) {
                draft.setToBranchId(sessionUser.getBranchId());
            }
            session.setAttribute("draftTransferRequest", draft);
        }

        model.addAttribute("transferRequest", draft);
        model.addAttribute("branches", branchRepository.findAll());

        // 1. Lấy toàn bộ danh sách sản phẩm đang hoạt động trên hệ thống
        ProductFilterRequest filter = new ProductFilterRequest();
        filter.setStatus(ProductStatus.ACTIVE);
        List<ProductResponse> allProducts = productService.findAll(filter, 0, 10000, "name", "asc").getContent();

        List<ProductResponse> productsInStock = new ArrayList<>();

        // 2. Kiểm tra xem người dùng đã chọn "Kho xuất" chưa
        if (draft.getFromBranchId() != null) {

            // 3. Lấy tất cả thông tin tồn kho của "Kho xuất"
            List<Inventory> branchInventories = inventoryRepository.findAllByBranchId(draft.getFromBranchId());

            // 4. Lọc ra danh sách ID của các Đơn vị Sản phẩm (Product Unit) có số lượng tồn
            // kho > 0
            List<Long> inStockProductUnitIds = new ArrayList<>();
            for (Inventory inventory : branchInventories) {
                if (inventory.getStock() > 0) {
                    inStockProductUnitIds.add(inventory.getProductUnit().getId());
                }
            }

            // 5. Duyệt qua từng sản phẩm để kiểm tra
            for (ProductResponse product : allProducts) {

                // Lọc ra những đơn vị quy đổi của sản phẩm này ĐANG CÓ HÀNG trong kho
                List<ProductUnitResponse> availableUnits = new ArrayList<>();

                if (product.getProductUnitsResponses() != null) {
                    for (ProductUnitResponse unit : product.getProductUnitsResponses()) {
                        // Nếu ID đơn vị nằm trong danh sách có hàng
                        if (inStockProductUnitIds.contains(unit.getId())) {
                            availableUnits.add(unit);
                        }
                    }
                }

                // Nếu sản phẩm này có ít nhất 1 đơn vị có hàng
                if (availableUnits.size() > 0) {
                    // Cập nhật lại danh sách đơn vị quy đổi (chỉ giữ lại những cái có hàng)
                    product.setProductUnitsResponses(availableUnits);

                    // Thêm sản phẩm này vào danh sách hợp lệ để hiển thị lên màn hình
                    productsInStock.add(product);
                }
            }
        }

        // 6. Truyền danh sách sản phẩm cuối cùng ra View
        model.addAttribute("activeProducts", productsInStock);

        int totalQty = draft.getItems().stream().mapToInt(TransferItemRequest::getQuantity).sum();
        long totalSKUs = draft.getItems().size();
        BigDecimal totalAmount = draft.getItems().stream()
                .map(item -> item.getImportPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("totalQty", totalQty);
        model.addAttribute("totalSKUs", totalSKUs);
        model.addAttribute("totalAmount", totalAmount);

        return "transfer/form";
    }

    @PostMapping("/create/filter")
    public String filterBranch(HttpSession session, @RequestParam(required = false) Long fromBranchId,
            @RequestParam(required = false) Long toBranchId, @RequestParam(required = false) String note) {
        TransferRequest draft = (TransferRequest) session.getAttribute("draftTransferRequest");
        if (draft != null) {
            draft.setFromBranchId(fromBranchId);
            draft.setToBranchId(toBranchId);
            draft.setNote(note);
        }
        return "redirect:/transfer/create";
    }

    @PostMapping("/create/add-item")
    public String addItem(HttpSession session,
            @RequestParam(required = false) Long productUnitId,
            @RequestParam(required = false) Integer quantity,
            @RequestParam(required = false) BigDecimal importPrice,
            @RequestParam(required = false) Long fromBranchId,
            @RequestParam(required = false) Long toBranchId,
            @RequestParam(required = false) String note,
            RedirectAttributes redirectAttributes) {

        TransferRequest draft = (TransferRequest) session.getAttribute("draftTransferRequest");
        if (draft != null) {
            draft.setFromBranchId(fromBranchId);
            draft.setToBranchId(toBranchId);
            draft.setNote(note);
        }

        if (productUnitId == null || quantity == null || importPrice == null || quantity <= 0) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Vui lòng chọn sản phẩm, đơn vị, số lượng > 0 và giá chuyển hợp lệ.");
            return "redirect:/transfer/create";
        }

        if (draft != null) {
            ProductUnit unit = productUnitRepository.findById(productUnitId).orElse(null);
            if (unit != null) {
                TransferItemRequest item = new TransferItemRequest();
                item.setProductUnitId(productUnitId);
                item.setQuantity(quantity);
                item.setImportPrice(importPrice);
                item.setProductName(unit.getProduct().getName());
                item.setSku(unit.getSku());
                item.setUnitName(unit.getUnit().getName());
                item.setLineTotal(importPrice.multiply(BigDecimal.valueOf(quantity)));
                draft.getItems().add(item);
            }
        }
        return "redirect:/transfer/create";
    }

    @PostMapping("/create/remove-item/{index}")
    public String removeItem(HttpSession session, @PathVariable int index,
            @RequestParam(required = false) Long fromBranchId,
            @RequestParam(required = false) Long toBranchId,
            @RequestParam(required = false) String note) {
        TransferRequest draft = (TransferRequest) session.getAttribute("draftTransferRequest");
        if (draft != null) {
            draft.setFromBranchId(fromBranchId);
            draft.setToBranchId(toBranchId);
            draft.setNote(note);

            if (index >= 0 && index < draft.getItems().size()) {
                draft.getItems().remove(index);
            }
        }
        return "redirect:/transfer/create";
    }

    @PostMapping("/save")
    public String saveTransferRequest(HttpSession session, RedirectAttributes redirectAttributes,
            @RequestParam(required = false) Long fromBranchId,
            @RequestParam(required = false) Long toBranchId,
            @RequestParam(required = false) String note) {
        SessionUser sessionUser = getSessionUser(session);
        TransferRequest draft = (TransferRequest) session.getAttribute("draftTransferRequest");

        if (draft == null || draft.getItems().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Phiếu chuyển kho trống!");
            return "redirect:/transfer/create";
        }

        if (fromBranchId != null)
            draft.setFromBranchId(fromBranchId);
        if (toBranchId != null)
            draft.setToBranchId(toBranchId);
        if (note != null)
            draft.setNote(note);

        if (!sessionUser.hasAnyRole("OWNER", "WAREHOUSE_STAFF")) {
            redirectAttributes.addFlashAttribute("errorMessage", "Chỉ SYSTEM OWNER hoặc NHÂN VIÊN KHO mới được phép tạo phiếu chuyển kho!");
            return "redirect:/transfer";
        }

        // Security Check: Only OWNER or staff of the involved branches can create the transfer
        if (!sessionUser.hasRole("OWNER")) {
            Long userBranchId = sessionUser.getBranchId();
            if (!userBranchId.equals(draft.getFromBranchId()) && !userBranchId.equals(draft.getToBranchId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Bạn không có quyền tạo phiếu cho chi nhánh khác!");
                return "redirect:/transfer/create";
            }
        }

        try {
            Long employeeId = sessionUser.getId();
            transferService.createTransferRequest(draft, employeeId);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Đã tạo phiếu xin chuyển hàng, đang chờ xác nhận từ chi nhánh xuất.");
            session.removeAttribute("draftTransferRequest");
            return "redirect:/transfer";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/transfer/create";
        }
    }

    @GetMapping("/{id}")
    public String detailTransfer(@PathVariable Long id, Model model) {
        model.addAttribute("page", "transfer");
        model.addAttribute("transferTx", transferService.getTransferById(id));
        model.addAttribute("details", transferService.getTransferDetails(id));
        return "transfer/detail";
    }

    @PostMapping("/{id}/approve")
    public String approveTransfer(HttpSession session, @PathVariable Long id, RedirectAttributes redirectAttributes) {
        SessionUser sessionUser = getSessionUser(session);
        try {
            if (!sessionUser.hasAnyRole("OWNER", "BRANCH_MANAGER")) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Chỉ OWNER hoặc Quản lý chi nhánh mới có quyền duyệt phiếu!");
                return "redirect:/transfer/" + id;
            }

            TransferResponse tx = transferService.getTransferById(id);
            if (sessionUser.hasRole("BRANCH_MANAGER") && !sessionUser.getBranchId().equals(tx.getFromBranchId())) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Chỉ Quản lý của chi nhánh xuất mới được duyệt phiếu!");
                return "redirect:/transfer/" + id;
            }

            transferService.approveTransferRequest(id, sessionUser.getId());
            redirectAttributes.addFlashAttribute("successMessage",
                    "Chuyển kho thành công. Hàng tồn kho ở cả 2 chi nhánh đã được cập nhật!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/transfer/" + id;
    }

    @PostMapping("/{id}/reject")
    public String rejectTransfer(HttpSession session,
                                 @PathVariable Long id,
                                 @RequestParam(required = false) String reason,
                                 RedirectAttributes redirectAttributes) {
        SessionUser sessionUser = getSessionUser(session);

        try {
            // Chỉ Owner hoặc Quản lý chi nhánh xuất mới được từ chối
            if (!sessionUser.hasAnyRole("OWNER", "BRANCH_MANAGER")) {
                redirectAttributes.addFlashAttribute("errorMessage", "Bạn không có quyền từ chối phiếu chuyển kho!");
                return "redirect:/transfer/" + id;
            }

            TransferResponse tx = transferService.getTransferById(id);
            if (sessionUser.hasRole("BRANCH_MANAGER") && !sessionUser.getBranchId().equals(tx.getFromBranchId())) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Chỉ Quản lý của chi nhánh xuất mới được từ chối phiếu!");
                return "redirect:/transfer/" + id;
            }

            transferService.rejectTransferRequest(id, sessionUser.getId(), reason);
            redirectAttributes.addFlashAttribute("successMessage", "Đã từ chối yêu cầu chuyển kho!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/transfer/" + id;
    }
}
