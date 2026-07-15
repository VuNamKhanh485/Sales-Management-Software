package com.g4fpt.sms.dashboard.controller;

import com.g4fpt.sms.order.service.ReturnRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final ReturnRequestService returnRequestService;

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        long pendingCount = returnRequestService.countPendingRequests();
        model.addAttribute("pendingReturnsCount", pendingCount);
        return "dashboard/index";
    }
}
