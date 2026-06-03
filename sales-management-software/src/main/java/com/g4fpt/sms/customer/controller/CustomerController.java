package com.g4fpt.sms.customer.controller;

import com.g4fpt.sms.customer.dto.CustomerRequest;
import com.g4fpt.sms.customer.entity.Customer;
import com.g4fpt.sms.customer.repository.CustomerRankRepository;
import com.g4fpt.sms.customer.repository.CustomerRepository;
import com.g4fpt.sms.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final CustomerRankRepository customerRankRepository;
    private final CustomerRepository customerRepository;

    @GetMapping
    public String listCustomers(@RequestParam(value = "keyword", required = false) String keyword, Model model) {
        List<Customer> customers;


        if (keyword != null && !keyword.trim().isEmpty()) {
            String cleanKeyword = keyword.trim();
            customers = customerRepository.findByCustomerCodeOrPhone(cleanKeyword, cleanKeyword);
            model.addAttribute("keyword", cleanKeyword);
        } else {

            customers = customerService.getAllCustomers();
        }

        model.addAttribute("customers", customers);
        return "customer/customer-list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("customerReq", new CustomerRequest());
        model.addAttribute("ranks", customerRankRepository.findAll());
        return "customer/customer-create";
    }

    @PostMapping("/create")
    public String createCustomer(@ModelAttribute("customerReq") CustomerRequest request, Model model) {
        try {
            if (request.getEmail() != null && request.getEmail().trim().isEmpty()) {
                request.setEmail(null);
            }
            customerService.createCustomer(request);
            return "redirect:/customers";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("ranks", customerRankRepository.findAll());
            return "customer/customer-create";
        }
    }

    @GetMapping("/update/{id}")
    public String showUpdateForm(@PathVariable Long id, Model model) {
        Customer customer = customerService.getCustomerById(id);

        CustomerRequest request = new CustomerRequest();
        request.setFullName(customer.getFullName());
        request.setPhone(customer.getPhone());
        request.setEmail(customer.getEmail());
        request.setAddress(customer.getAddress());
        request.setNote(customer.getNote());
        if (customer.getCustomerRank() != null) {
            request.setCustomerRankId(customer.getCustomerRank().getId());
        }

        model.addAttribute("customerReq", request);
        model.addAttribute("customerId", id);
        model.addAttribute("ranks", customerRankRepository.findAll());
        return "customer/customer-update";
    }

    @PostMapping("/update/{id}")
    public String updateCustomer(@PathVariable Long id, @ModelAttribute("customerReq") CustomerRequest request, Model model) {
        try {
            if (request.getEmail() != null && request.getEmail().trim().isEmpty()) {
                request.setEmail(null);
            }
            customerService.updateCustomer(id, request);
            return "redirect:/customers";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("customerId", id);
            model.addAttribute("ranks", customerRankRepository.findAll());
            return "customer/customer-update";
        }
    }

    @GetMapping("/toggle-status/{id}")
    public String toggleStatus(@PathVariable Long id) {
        customerService.toggleStatus(id);
        return "redirect:/customers";
    }
}