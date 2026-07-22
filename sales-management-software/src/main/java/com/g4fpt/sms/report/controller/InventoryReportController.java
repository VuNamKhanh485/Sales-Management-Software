package com.g4fpt.sms.report.controller;

import com.g4fpt.sms.branch.repository.BranchRepository;
import com.g4fpt.sms.common.exception.ValidationException;
import com.g4fpt.sms.product.repository.BrandRepository;
import com.g4fpt.sms.product.repository.CategoryRepository;
import com.g4fpt.sms.report.dto.InventoryReportDTO;
import com.g4fpt.sms.report.dto.InventoryReportFilterRequest;
import com.g4fpt.sms.report.service.InventoryReportService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import com.g4fpt.sms.report.emuns.SnapshotType;

@Controller
@RequestMapping("/report/inventory")
@RequiredArgsConstructor
public class InventoryReportController {

    private final InventoryReportService inventoryReportService;
    private final BranchRepository branchRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;

    @GetMapping
    public String viewReport(@ModelAttribute InventoryReportFilterRequest filter, Model model) {
        if (filter.getFromDate() == null) filter.setFromDate(LocalDate.now(ZoneId.systemDefault()).withDayOfMonth(1));
        if (filter.getToDate() == null) filter.setToDate(LocalDate.now(ZoneId.systemDefault()));
        if (filter.getSnapshotType() == null) filter.setSnapshotType(SnapshotType.DAY);
        try {
            List<InventoryReportDTO> data = inventoryReportService.generateReport(filter);
            model.addAttribute("data", data);
        }catch(ValidationException e){
            model.addAttribute("data", List.of());      // ← nên phải set lại ở đây
            model.addAttribute("errors", e.getErrors());
        }
        model.addAttribute("filter", filter);
        model.addAttribute("branches", branchRepository.findAll());
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("brands", brandRepository.findAll());

        return "report/inventory";
    }

    @GetMapping("/export")
    public void exportExcel(@ModelAttribute InventoryReportFilterRequest filter,
                            HttpServletResponse response) throws IOException{
        if (filter.getFromDate() == null) filter.setFromDate(LocalDate.now(ZoneId.systemDefault()).withDayOfMonth(1));
        if (filter.getToDate() == null) filter.setToDate(LocalDate.now(ZoneId.systemDefault()));
        if (filter.getSnapshotType() == null) filter.setSnapshotType(SnapshotType.DAY);

        byte[] data = inventoryReportService.exportExcel(filter);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition",
                "attachment; filename=bao_cao_xuat_nhap_ton_" + LocalDate.now(ZoneId.systemDefault()) + ".xlsx");
        response.setContentLength(data.length);

        response.getOutputStream().write(data);
        response.getOutputStream().flush();
    }
}