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

    @GetMapping("/cashflow")
    public String showCashflowDetails(
            @RequestParam(value = "branchId", required = false) Long branchId,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
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

        List<com.g4fpt.sms.report.dto.CashflowDetailDTO> allCashflowData = reportService.getDetailedCashflow(branchId, startDateTime, endDateTime);

        if (type != null && !type.isEmpty()) {
            allCashflowData = allCashflowData.stream()
                    .filter(d -> type.equals(d.getType()))
                    .collect(java.util.stream.Collectors.toList());
        }

        List<Branch> branches = branchService.getAll();

        boolean isManager = user != null && user.hasAnyRole("OWNER", "BRANCH_MANAGER");
        boolean isOwner = user != null && user.hasRole("OWNER");

        java.math.BigDecimal totalIn = java.math.BigDecimal.ZERO;
        java.math.BigDecimal totalOut = java.math.BigDecimal.ZERO;
        java.math.BigDecimal totalSalesIn = java.math.BigDecimal.ZERO;
        java.math.BigDecimal totalCashbookIn = java.math.BigDecimal.ZERO;
        java.math.BigDecimal totalTransferIn = java.math.BigDecimal.ZERO;
        java.math.BigDecimal totalImportOut = java.math.BigDecimal.ZERO;
        java.math.BigDecimal totalReturnOut = java.math.BigDecimal.ZERO;
        java.math.BigDecimal totalCashbookOut = java.math.BigDecimal.ZERO;
        java.math.BigDecimal totalTransferOut = java.math.BigDecimal.ZERO;

        for (com.g4fpt.sms.report.dto.CashflowDetailDTO dto : allCashflowData) {
            java.math.BigDecimal amountIn = dto.getAmountIn() != null ? dto.getAmountIn() : java.math.BigDecimal.ZERO;
            java.math.BigDecimal amountOut = dto.getAmountOut() != null ? dto.getAmountOut() : java.math.BigDecimal.ZERO;
            
            totalIn = totalIn.add(amountIn);
            totalOut = totalOut.add(amountOut);

            if ("Bán hàng".equals(dto.getType())) {
                totalSalesIn = totalSalesIn.add(amountIn);
            } else if ("Sổ quỹ - Thu".equals(dto.getType())) {
                totalCashbookIn = totalCashbookIn.add(amountIn);
            } else if ("Chuyển kho (Xuất)".equals(dto.getType())) {
                totalTransferIn = totalTransferIn.add(amountIn);
            } else if ("Nhập hàng".equals(dto.getType())) {
                totalImportOut = totalImportOut.add(amountOut);
            } else if ("Trả hàng".equals(dto.getType())) {
                totalReturnOut = totalReturnOut.add(amountOut);
            } else if ("Sổ quỹ - Chi".equals(dto.getType())) {
                totalCashbookOut = totalCashbookOut.add(amountOut);
            } else if ("Chuyển kho (Nhập)".equals(dto.getType())) {
                totalTransferOut = totalTransferOut.add(amountOut);
            }
        }

        int totalItems = allCashflowData.size();
        int totalPages = (int) Math.ceil((double) totalItems / size);
        if (page < 1) page = 1;
        if (page > totalPages && totalPages > 0) page = totalPages;

        int fromIndex = (page - 1) * size;
        int toIndex = Math.min(fromIndex + size, totalItems);
        List<com.g4fpt.sms.report.dto.CashflowDetailDTO> pagedData = totalItems > 0 ? allCashflowData.subList(fromIndex, toIndex) : new java.util.ArrayList<>();

        model.addAttribute("cashflowData", pagedData);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("size", size);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("totalIn", totalIn);
        model.addAttribute("totalOut", totalOut);
        model.addAttribute("netProfit", totalIn.subtract(totalOut));
        
        model.addAttribute("totalSalesIn", totalSalesIn);
        model.addAttribute("totalCashbookIn", totalCashbookIn);
        model.addAttribute("totalTransferIn", totalTransferIn);
        model.addAttribute("totalImportOut", totalImportOut);
        model.addAttribute("totalReturnOut", totalReturnOut);
        model.addAttribute("totalCashbookOut", totalCashbookOut);
        model.addAttribute("totalTransferOut", totalTransferOut);
        
        model.addAttribute("branches", branches);
        model.addAttribute("selectedBranchId", branchId);
        model.addAttribute("selectedType", type);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("isManager", isManager);
        model.addAttribute("isOwner", isOwner);

        return "report/cashflow-details";
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
            @RequestParam(value = "period", required = false, defaultValue = "custom") String period,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
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
        List<com.g4fpt.sms.employee.entity.Employee> allEmployees = employeeRepository.findAll();
        List<com.g4fpt.sms.employee.entity.Employee> employees = new java.util.ArrayList<>();
        for (com.g4fpt.sms.employee.entity.Employee e : allEmployees) {
            if (branchId == null) {
                employees.add(e);
            } else {
                if (e.getBranch() != null && e.getBranch().getId().equals(branchId)) {
                    employees.add(e);
                } else if (e.getRole() != null && "OWNER".equals(e.getRole().getCode())) {
                    employees.add(e);
                }
            }
        }

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
        model.addAttribute("period", period);
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

        // Get Best Salesperson
        List<EmployeeSalesDTO> employeeSales = reportService.getEmployeeSalesReport(branchId, startDateTime, endDateTime);
        EmployeeSalesDTO bestSalesperson = null;
        if (!isManager && user != null) {
            if (employeeSales != null) {
                bestSalesperson = employeeSales.stream()
                        .filter(e -> e.getEmployeeId().equals(user.getId()))
                        .findFirst()
                        .orElse(null);
            }
            if (bestSalesperson == null) {
                bestSalesperson = new EmployeeSalesDTO(user.getId(), "", user.getFullName(), 0L, java.math.BigDecimal.ZERO, "Chi nhánh");
            }
        } else if (employeeSales != null && !employeeSales.isEmpty()) {
            bestSalesperson = employeeSales.get(0);
        }

        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("bestSalesperson", bestSalesperson);

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
