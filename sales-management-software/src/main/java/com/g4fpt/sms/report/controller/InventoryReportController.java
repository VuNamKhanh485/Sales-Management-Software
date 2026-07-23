package com.g4fpt.sms.report.controller;

import com.g4fpt.sms.branch.repository.BranchRepository;
import com.g4fpt.sms.common.exception.ValidationException;
import com.g4fpt.sms.product.repository.BrandRepository;
import com.g4fpt.sms.product.repository.CategoryRepository;
import com.g4fpt.sms.report.dto.InventoryReportDTO;
import com.g4fpt.sms.report.dto.InventoryReportFilterRequest;
import com.g4fpt.sms.report.emuns.SnapshotType;
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
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import com.g4fpt.sms.auth.dto.SessionUser;
import com.g4fpt.sms.auth.util.SessionConstants;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/report/inventory")
@RequiredArgsConstructor
public class InventoryReportController {

    private final InventoryReportService inventoryReportService;
    private final BranchRepository branchRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;

    @GetMapping
    public String viewReport(@ModelAttribute InventoryReportFilterRequest filter, HttpSession httpSession, Model model) {
        SessionUser user = (SessionUser) httpSession.getAttribute(SessionConstants.LOGGED_IN_USER);
        if (user == null || (!user.hasRole("OWNER") && !user.hasRole("BRANCH_MANAGER"))) {
            return "redirect:/";
        }

        boolean isOwner = user.hasRole("OWNER");
        model.addAttribute("isOwner", isOwner);
        if (!isOwner) {
            filter.setBranchId(user.getBranchId());
        }

        // Defaults
        if (filter.getPage() == null || filter.getPage() < 1)
            filter.setPage(1);
        if (filter.getPageSize() == null || filter.getPageSize() < 1)
            filter.setPageSize(10);

        // Convert reportPeriod + year → fromDate/toDate/snapshotType
        resolveDates(filter);

        try {
            List<InventoryReportDTO> allData = inventoryReportService.generateReport(filter);
            int totalItems = allData.size();

            // Pagination
            int pageSize = filter.getPageSize();
            int totalPages = (totalItems + pageSize - 1) / pageSize;
            if (totalPages < 1)
                totalPages = 1;
            if (filter.getPage() > totalPages)
                filter.setPage(totalPages);

            int fromIndex = (filter.getPage() - 1) * pageSize;
            int toIndex = Math.min(fromIndex + pageSize, totalItems);

            List<InventoryReportDTO> pageData = (fromIndex < totalItems)
                    ? allData.subList(fromIndex, toIndex)
                    : List.of();

            model.addAttribute("data", pageData);
            model.addAttribute("allData", allData);
            model.addAttribute("totalItems", totalItems);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("currentPage", filter.getPage());
            model.addAttribute("pageSize", pageSize);
        } catch (ValidationException e) {
            model.addAttribute("data", List.of());
            model.addAttribute("errors", e.getErrors());
            model.addAttribute("totalItems", 0);
            model.addAttribute("totalPages", 1);
            model.addAttribute("currentPage", 1);
            model.addAttribute("pageSize", filter.getPageSize());
        }

        model.addAttribute("filter", filter);
        model.addAttribute("branches", branchRepository.findAll());
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("brands", brandRepository.findAll());
        model.addAttribute("currentYear", LocalDate.now().getYear());

        return "report/inventory";
    }

    /**
     * Convert reportPeriod + year → fromDate, toDate, snapshotType
     * Only overrides when reportPeriod or year is explicitly set.
     * If both empty → preserves manual fromDate/toDate as-is.
     */
    private void resolveDates(InventoryReportFilterRequest filter) {
        String period = filter.getReportPeriod();
        Integer yearVal = filter.getYear();

        // If both period and year are empty → preserve manual fromDate/toDate, set
        // defaults if null
        if ((period == null || period.isEmpty()) && (yearVal == null || yearVal == 0)) {
            if (filter.getSnapshotType() == null)
                filter.setSnapshotType(SnapshotType.DAY);
            if (filter.getFromDate() == null)
                filter.setFromDate(LocalDate.now(ZoneId.systemDefault()).withDayOfMonth(1));
            if (filter.getToDate() == null)
                filter.setToDate(LocalDate.now(ZoneId.systemDefault()));
            return;
        }
        if (filter.getSnapshotType() == null)
            filter.setSnapshotType(SnapshotType.DAY);
        if (filter.getFromDate() == null)
            filter.setFromDate(LocalDate.now(ZoneId.systemDefault()).withDayOfMonth(1));
        if (filter.getToDate() == null)
            filter.setToDate(LocalDate.now(ZoneId.systemDefault()));

        int year = (yearVal != null) ? yearVal : LocalDate.now().getYear();

        if (period == null || period.isEmpty()) {
            // Year only → YEAR report
            filter.setSnapshotType(SnapshotType.YEAR);
            filter.setFromDate(LocalDate.of(year, 1, 1));
            filter.setToDate(LocalDate.of(year, 12, 31));
            return;
        }

        YearMonth ym;
        switch (period) {
            case "Q1" -> {
                filter.setSnapshotType(SnapshotType.QUARTER);
                filter.setFromDate(LocalDate.of(year, 1, 1));
                filter.setToDate(LocalDate.of(year, 3, 31));
                return;
            }
            case "Q2" -> {
                filter.setSnapshotType(SnapshotType.QUARTER);
                filter.setFromDate(LocalDate.of(year, 4, 1));
                filter.setToDate(LocalDate.of(year, 6, 30));
                return;
            }
            case "Q3" -> {
                filter.setSnapshotType(SnapshotType.QUARTER);
                filter.setFromDate(LocalDate.of(year, 7, 1));
                filter.setToDate(LocalDate.of(year, 9, 30));
                return;
            }
            case "Q4" -> {
                filter.setSnapshotType(SnapshotType.QUARTER);
                filter.setFromDate(LocalDate.of(year, 10, 1));
                filter.setToDate(LocalDate.of(year, 12, 31));
                return;
            }
            case "H1" -> {
                filter.setSnapshotType(SnapshotType.MONTH);
                filter.setFromDate(LocalDate.of(year, 1, 1));
                filter.setToDate(LocalDate.of(year, 6, 30));
                return;
            }
            case "H2" -> {
                filter.setSnapshotType(SnapshotType.MONTH);
                filter.setFromDate(LocalDate.of(year, 7, 1));
                filter.setToDate(LocalDate.of(year, 12, 31));
                return;
            }
            default -> {
                try {
                    int month = Integer.parseInt(period);
                    if (month < 1 || month > 12)
                        month = 1;
                    ym = YearMonth.of(year, month);
                    filter.setSnapshotType(SnapshotType.MONTH);
                    filter.setFromDate(ym.atDay(1));
                    filter.setToDate(ym.atEndOfMonth());
                } catch (NumberFormatException e) {
                    ym = YearMonth.now();
                    filter.setSnapshotType(SnapshotType.MONTH);
                    filter.setFromDate(ym.atDay(1));
                    filter.setToDate(ym.atEndOfMonth());
                }
            }
        }
    }

    @GetMapping("/export")
    public void exportExcel(@ModelAttribute InventoryReportFilterRequest filter,
            HttpServletResponse response) throws IOException {
        resolveDates(filter);

        byte[] data = inventoryReportService.exportExcel(filter);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition",
                "attachment; filename=bao_cao_xuat_nhap_ton_" + LocalDate.now(ZoneId.systemDefault()) + ".xlsx");
        response.setContentLength(data.length);

        response.getOutputStream().write(data);
        response.getOutputStream().flush();
    }
}