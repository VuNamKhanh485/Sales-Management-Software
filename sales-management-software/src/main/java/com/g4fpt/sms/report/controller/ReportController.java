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

    @GetMapping("/profit")
    public String showProfitReport(
            @RequestParam(value = "branchId", required = false) Long branchId,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model,
            HttpSession session) {

        SessionUser user = (SessionUser) session.getAttribute(SessionConstants.LOGGED_IN_USER);
        if (user != null && ("MANAGER".equals(user.getRoleName()) || "CASHIER".equals(user.getRoleName()))) {
            if (branchId == null) {
                branchId = user.getBranchId();
            }
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

        model.addAttribute("reportData", reportData);
        model.addAttribute("branches", branches);
        model.addAttribute("selectedBranchId", branchId);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

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
        if (user != null && ("MANAGER".equals(user.getRoleName()) || "CASHIER".equals(user.getRoleName()))) {
            if (branchId == null) {
                branchId = user.getBranchId();
            }
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

        model.addAttribute("reportData", reportData);
        model.addAttribute("branches", branches);
        model.addAttribute("selectedBranchId", branchId);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

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
        if (user != null && ("MANAGER".equals(user.getRoleName()) || "CASHIER".equals(user.getRoleName()))) {
            if (branchId == null) {
                branchId = user.getBranchId();
            }
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

        model.addAttribute("profitData", profitData);
        model.addAttribute("employeeSalesData", employeeSalesData);
        model.addAttribute("branches", branches);
        model.addAttribute("selectedBranchId", branchId);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

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
        if (user != null && ("MANAGER".equals(user.getRoleName()) || "CASHIER".equals(user.getRoleName()))) {
            if (branchId == null) {
                branchId = user.getBranchId();
            }
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
}
