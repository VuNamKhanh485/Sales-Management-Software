package com.g4fpt.sms.customer.controller;

import com.g4fpt.sms.customer.dto.CustomerRequestDTO;
import com.g4fpt.sms.customer.entity.Customer;
import com.g4fpt.sms.customer.repository.CustomerRepository;
import com.g4fpt.sms.customer.service.CustomerRankService;
import com.g4fpt.sms.customer.service.CustomerService;
import com.g4fpt.sms.employee.utils.Gender;
import com.g4fpt.sms.order.entity.OrderTransaction;
import com.g4fpt.sms.order.repository.OrderTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final CustomerRankService customerRankService;
    private final CustomerRepository customerRepository;
    private final OrderTransactionRepository orderTransactionRepository;


    @GetMapping
    public String listCustomers(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "6") int size, Model model) {

        Page<Customer> customerPage = customerService.getCustomers(keyword, page, size);
        model.addAttribute("customerPage", customerPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", customerPage.getTotalPages());

        return "customer/list";
    }


    @GetMapping("/detail/{id}")
    public String showDetail(
            @PathVariable Long id,
            @RequestParam(value = "page", defaultValue = "1") int page, Model model) {

        Customer customer = customerService.getCustomerById(id);
        Pageable pageable = PageRequest.of(page - 1, 6);
        Page<OrderTransaction> orderPage = orderTransactionRepository
                .findByCustomerIdOrderByCreatedAtDesc(id, pageable);

        model.addAttribute("customer", customer);
        model.addAttribute("orderPage", orderPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", orderPage.getTotalPages());

        return "customer/detail";
    }

    @GetMapping({"/create", "/edit/{id}"})
    public String showForm(
            @PathVariable(value = "id", required = false) Long id,
            Model model) {

        CustomerRequestDTO dto = new CustomerRequestDTO();

        if (id != null) {
            Customer customer = customerService.getCustomerById(id);
            dto.setId(customer.getId());
            dto.setFullName(customer.getFullName());
            dto.setPhone(customer.getPhone());
            dto.setEmail(customer.getEmail());
            dto.setAddress(customer.getAddress());
            dto.setGender(customer.getGender());
            dto.setDob(customer.getDob());
            dto.setNote(customer.getNote());
            if (customer.getCustomerRank() != null) {
                dto.setCustomerRankId(customer.getCustomerRank().getId());
            }
        }

        model.addAttribute("customerDTO", dto);
        model.addAttribute("ranks", customerRankService.getAllRanks());
        model.addAttribute("genders", Gender.values());

        return "customer/form";
    }

    @PostMapping("/save")
    public String saveCustomer(
            @ModelAttribute("customerDTO") CustomerRequestDTO dto,
            Model model,
            RedirectAttributes redirectAttributes) {

        Long currentUserId = 1L;
        try {
            if (dto.getId() == null) {
                customerService.createCustomer(dto, currentUserId);
                redirectAttributes.addFlashAttribute("successMessage", "Thêm mới thành công!");
            } else {
                customerService.updateCustomer(dto.getId(), dto, currentUserId);
                redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thành công!");
            }
        } catch (Exception e) {

            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("customerDTO", dto);
            model.addAttribute("ranks", customerRankService.getAllRanks());
            model.addAttribute("genders", Gender.values());
            return "customer/form";
        }

        return "redirect:/customers";
    }












//    @PostMapping("/save")
//    public String saveCustomer(
//            @ModelAttribute("customerDTO") CustomerRequestDTO dto,
//            Model model,
//            RedirectAttributes redirectAttributes) {
//
//        Long currentUserId = 1L;
//        try {
//            customerService.save(dto, currentUserId);
//            redirectAttributes.addFlashAttribute("successMessage",
//                    dto.getId() == null ? "Thêm mới thành công!" : "Cập nhật thành công!");
//        } catch (Exception e) {
//            model.addAttribute("errorMessage", e.getMessage());
//            model.addAttribute("customerDTO", dto);
//            model.addAttribute("ranks", customerRankService.getAllRanks());
//            model.addAttribute("genders", Gender.values());
//            return "customer/form";
//        }
//
//        return "redirect:/customers";
//    }

}