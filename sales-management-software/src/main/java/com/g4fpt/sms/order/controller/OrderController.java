package com.g4fpt.sms.order.controller;

import com.g4fpt.sms.branch.repository.BranchRepository;
import com.g4fpt.sms.order.entity.OrderTransaction;
import com.g4fpt.sms.order.repository.OrderTransactionRepository;
import com.g4fpt.sms.employee.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderTransactionRepository orderTransactionRepository;
    private final BranchRepository branchRepository;
    private final EmployeeRepository employeeRepository;

    @GetMapping("/{id}")
    public String showOrderDetail(@PathVariable Long id, Model model) {
        OrderTransaction order = orderTransactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        model.addAttribute("order", order);

        if (order.getBranchId() != null) {
            branchRepository.findById(order.getBranchId()).ifPresent(branch -> {
                model.addAttribute("branch", branch);
            });
        }

        if (order.getCreatedBy() != null) {
            employeeRepository.findById(order.getCreatedBy()).ifPresent(employee -> {
                model.addAttribute("employee", employee);
            });
        }

        return "order/detail";
    }
}