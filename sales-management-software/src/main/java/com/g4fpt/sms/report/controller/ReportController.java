package com.g4fpt.sms.report.controller;

import com.g4fpt.sms.auth.dto.SessionUser;
import com.g4fpt.sms.auth.util.SessionConstants;
import com.g4fpt.sms.branch.entity.Branch;
import com.g4fpt.sms.branch.service.BranchService;
import com.g4fpt.sms.report.dto.EmployeeSalesDTO;
import com.g4fpt.sms.report.service.ReportService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.g4fpt.sms.order.entity.OrderTransaction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final BranchService branchService;
    private final com.g4fpt.sms.employee.repository.EmployeeRepository employeeRepository;

    @GetMapping("/profit")
    public String showProfitReport(
            @RequestParam(value = "branchId", required = false) Long branchId,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model,
            HttpSession session) {

        SessionUser user = (SessionUser) session.getAttribute(SessionConstants.LOGGED_IN_USER);
        if (user != null && !user.hasRole("OWNER")) {
            branchId = user.getBranchId();
        }

        if (startDate == null) {
            startDate = LocalDate.now().minusDays(30); // Default last 30 days
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        Map<String, Object> reportData = reportService.getProfitReport(branchId, startDateTime, endDateTime);

        List<Branch> branches = branchService.getAll();

        boolean isManager = user != null && user.hasAnyRole("OWNER", "BRANCH_MANAGER");
        boolean isOwner = user != null && user.hasRole("OWNER");

        model.addAttribute("reportData", reportData);
        model.addAttribute("branches", branches);
        model.addAttribute("selectedBranchId", branchId);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("isManager", isManager);
        model.addAttribute("isOwner", isOwner);

        return "report/profit";
    }

    @GetMapping("/employee-sales")
    public String showEmployeeSalesReport(
            @RequestParam(value = "branchId", required = false) Long branchId,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model,
            HttpSession session) {

        SessionUser user = (SessionUser) session.getAttribute(SessionConstants.LOGGED_IN_USER);
        if (user != null && !user.hasRole("OWNER")) {
            branchId = user.getBranchId();
        }

        if (startDate == null) {
            startDate = LocalDate.now().minusDays(30); // Default last 30 days
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        List<EmployeeSalesDTO> reportData = reportService.getEmployeeSalesReport(branchId, startDateTime, endDateTime);

        List<Branch> branches = branchService.getAll();

        boolean isManager = user != null && user.hasAnyRole("OWNER", "BRANCH_MANAGER");
        boolean isOwner = user != null && user.hasRole("OWNER");

        model.addAttribute("reportData", reportData);
        model.addAttribute("branches", branches);
        model.addAttribute("selectedBranchId", branchId);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("isManager", isManager);
        model.addAttribute("isOwner", isOwner);

        return "report/employee-sales";
    }

    @GetMapping({"", "/", "/overview"})
    public String showOverviewReport(
            @RequestParam(value = "branchId", required = false) Long branchId,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model,
            HttpSession session) {

        SessionUser user = (SessionUser) session.getAttribute(SessionConstants.LOGGED_IN_USER);
        if (user != null && !user.hasRole("OWNER")) {
            branchId = user.getBranchId();
        }

        if (startDate == null) {
            startDate = LocalDate.now().minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        Map<String, Object> profitData = reportService.getProfitReport(branchId, startDateTime, endDateTime);
        List<EmployeeSalesDTO> employeeSalesData = reportService.getEmployeeSalesReport(branchId, startDateTime, endDateTime);
        List<Branch> branches = branchService.getAll();

        boolean isManager = user != null && user.hasAnyRole("OWNER", "BRANCH_MANAGER");
        boolean isOwner = user != null && user.hasRole("OWNER");
        if (!isManager && user != null) {
            employeeSalesData = employeeSalesData.stream()
                    .filter(dto -> dto.getEmployeeId().equals(user.getId()))
                    .toList();
        }

        model.addAttribute("profitData", profitData);
        model.addAttribute("employeeSalesData", employeeSalesData);
        model.addAttribute("branches", branches);
        model.addAttribute("selectedBranchId", branchId);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("isManager", isManager);
        model.addAttribute("isOwner", isOwner);

        return "report/overview";
    }

    @GetMapping("/employee-sales/{employeeId}")
    public String showEmployeeSalesDetails(
            @PathVariable Long employeeId,
            @RequestParam(value = "branchId", required = false) Long branchId,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model,
            HttpSession session) {

        SessionUser user = (SessionUser) session.getAttribute(SessionConstants.LOGGED_IN_USER);
        if (user != null && !user.hasRole("OWNER")) {
            branchId = user.getBranchId();
        }

        if (startDate == null) {
            startDate = LocalDate.now().minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        List<OrderTransaction> transactions = reportService.getEmployeeSalesDetails(employeeId, branchId, startDateTime, endDateTime);

        model.addAttribute("transactions", transactions);
        model.addAttribute("employeeId", employeeId);
        model.addAttribute("branchId", branchId);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "report/employee-sales-detail";
    }

    @GetMapping("/detailed-sales")
    public String showDetailedSales(
            @RequestParam(value = "branchId", required = false) Long branchId,
            @RequestParam(value = "employeeId", required = false) Long employeeId,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            Model model,
            HttpSession session) {

        SessionUser user = (SessionUser) session.getAttribute(SessionConstants.LOGGED_IN_USER);
        if (user != null && !user.hasRole("OWNER")) {
            branchId = user.getBranchId();
        }

        boolean isManager = user != null && user.hasAnyRole("OWNER", "BRANCH_MANAGER");
        boolean isOwner = user != null && user.hasRole("OWNER");

        if (user != null && !isManager) {
            employeeId = user.getId();
        }

        if (startDate == null) {
            startDate = LocalDate.now().minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page - 1, size);
        org.springframework.data.domain.Page<com.g4fpt.sms.report.dto.EmployeeOrderSalesDTO> reportPage = reportService.getDetailedOrderSalesPage(branchId, employeeId, startDateTime, endDateTime, pageable);

        List<Branch> branches = branchService.getAll();
        List<com.g4fpt.sms.employee.entity.Employee> employees = employeeRepository.findAll();

        model.addAttribute("reportData", reportPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", reportPage.getTotalPages());
        model.addAttribute("totalItems", reportPage.getTotalElements());
        model.addAttribute("size", size);
        
        model.addAttribute("branches", branches);
        model.addAttribute("employees", employees);
        model.addAttribute("selectedBranchId", branchId);
        model.addAttribute("selectedEmployeeId", employeeId);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("isManager", isManager);
        model.addAttribute("isOwner", isOwner);

        // Calculate totals
        List<Object[]> totalsResult = reportService.getDetailedOrderSalesTotals(branchId, employeeId, startDateTime, endDateTime);
        long totalOrders = 0;
        java.math.BigDecimal totalRevenue = java.math.BigDecimal.ZERO;
        
        if (totalsResult != null && !totalsResult.isEmpty() && totalsResult.get(0) != null) {
            Object[] totals = totalsResult.get(0);
            if (totals[0] != null) totalOrders = ((Number) totals[0]).longValue();
            if (totals[1] != null) totalRevenue = (java.math.BigDecimal) totals[1];
        }

        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalRevenue", totalRevenue);

        return "report/detailed-sales";
    }

    @GetMapping("/detailed-sales/export")
    public org.springframework.http.ResponseEntity<byte[]> exportDetailedSales(
            @RequestParam(value = "branchId", required = false) Long branchId,
            @RequestParam(value = "employeeId", required = false) Long employeeId,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            HttpSession session) {
            
        SessionUser user = (SessionUser) session.getAttribute(SessionConstants.LOGGED_IN_USER);
        if (user != null && !user.hasRole("OWNER")) {
            branchId = user.getBranchId();
        }

        boolean isManager = user != null && user.hasAnyRole("OWNER", "BRANCH_MANAGER");
        if (user != null && !isManager) {
            employeeId = user.getId();
        }

        if (startDate == null) {
            startDate = LocalDate.now().minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        try {
            List<com.g4fpt.sms.report.dto.EmployeeOrderSalesDTO> data = reportService.getDetailedOrderSalesList(branchId, employeeId, startDateTime, endDateTime);
            byte[] excelData = reportService.exportDetailedOrderSalesToExcel(data);

            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDispositionFormData("attachment", "Bao_cao_don_hang.xlsx");

            return new org.springframework.http.ResponseEntity<>(excelData, headers, org.springframework.http.HttpStatus.OK);
        } catch (java.io.IOException e) {
            e.printStackTrace();
            return org.springframework.http.ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
