package com.g4fpt.sms.employee.controller;

import com.g4fpt.sms.auth.security.CustomUserDetails;
import com.g4fpt.sms.branch.service.BranchService;
import com.g4fpt.sms.employee.entity.Employee;
import com.g4fpt.sms.employee.entity.Role;
import com.g4fpt.sms.employee.repository.RoleRepository;
import com.g4fpt.sms.employee.service.EmployeeService;
import com.g4fpt.sms.employee.utils.Gender;
import com.g4fpt.sms.employee.utils.WorkStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@Controller
@RequestMapping("/employee")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final BranchService branchService;
    private final RoleRepository roleRepository;

    public EmployeeController(EmployeeService employeeService,
                              BranchService branchService,
                              RoleRepository roleRepository) {
        this.employeeService = employeeService;
        this.branchService = branchService;
        this.roleRepository = roleRepository;
    }

    @GetMapping
    public String list(Model model, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        model.addAttribute("employees", employeeService.getAll(customUserDetails));
        return "employee/list";
    }


    // create and update
    @GetMapping({"/create", "/edit/{id}"})
    public String form(@PathVariable(value = "id", required = false) Long id, Model model) {
        Employee employee;
        boolean isEdit = id != null;
        if (isEdit) {
            employee = employeeService.getById(id);
            if (employee == null) {
                return "redirect:/employee";
            }
        } else {
            employee = new Employee();
            int year = LocalDate.now().getYear();
            int randomNum = 100 + new Random().nextInt(900);
            employee.setEmployeeCode("EMP-" + year + "-" + randomNum);
            employee.setHiredDate(LocalDate.now());
            employee.setWorkStatus(WorkStatus.ACTIVE);
        }

        model.addAttribute("employee", employee);
        model.addAttribute("branches", branchService.getAll());
        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("genders", Gender.values());
        model.addAttribute("isEdit", isEdit);
        return "employee/create";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute("employee") Employee employee,
                       @RequestParam("roleCode") String roleCode,
                       @RequestParam(value = "branchId", required = false) Long branchId) {
        
        if (employee.getId() != null) {
            Employee existing = employeeService.getById(employee.getId());
            if (existing != null) {
                existing.setFullName(employee.getFullName());
                existing.setEmail(employee.getEmail());
                existing.setPhone(employee.getPhone());
                existing.setAddress(employee.getAddress());
                existing.setGender(employee.getGender());
                existing.setDob(employee.getDob());
                existing.setHiredDate(employee.getHiredDate());
                existing.setWorkStatus(employee.getWorkStatus());
                existing.setNote(employee.getNote());
                
                if (employee.getPasswordHash() != null && !employee.getPasswordHash().isEmpty()) {
                    existing.setPasswordHash(employee.getPasswordHash());
                }

                Role role = roleRepository.findByCode(roleCode).orElse(null);
                if (role != null) {
                    existing.setRole(role);
                }
                
                if (branchId != null) {
                    existing.setBranch(branchService.getById(branchId));
                } else {
                    existing.setBranch(null);
                }

                employeeService.save(existing);
            }
        } else {
            Role role = roleRepository.findByCode(roleCode).orElse(null);
            if (role != null) {
                employee.setRole(role);
            }
            if (branchId != null) {
                employee.setBranch(branchService.getById(branchId));
            }
            employeeService.save(employee);
        }
        
        return "redirect:/employee";
    }

    // delete
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id) {
        employeeService.delete(id);
        return "redirect:/employee";
    }

    // search
    @GetMapping("/search")
    public String searchEmployee(@RequestParam(value ="keyword",required = false) String keyword,Model model,@AuthenticationPrincipal CustomUserDetails customUserDetails ){
        model.addAttribute("employees",employeeService.search(keyword,customUserDetails));

        return "employee/list";
    }

}
