package com.g4fpt.sms.employee.controller;

import com.g4fpt.sms.branch.service.BranchService;
import com.g4fpt.sms.employee.entity.Employee;
import com.g4fpt.sms.employee.entity.Role;
import com.g4fpt.sms.employee.repository.RoleRepository;
import com.g4fpt.sms.employee.service.EmployeeService;
import com.g4fpt.sms.employee.utils.Gender;
import com.g4fpt.sms.employee.utils.WorkStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
    public String list(Model model) {
        model.addAttribute("page", "employee");
        model.addAttribute("employees", employeeService.getAll());
        return "employee/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        Employee employee = new Employee();
        // Pre-generate employee code like EMP-2026-XYZ
        int year = LocalDate.now().getYear();
        int randomNum = 100 + new Random().nextInt(900);
        employee.setEmployeeCode("EMP-" + year + "-" + randomNum);
        employee.setHiredDate(LocalDate.now());
        employee.setWorkStatus(WorkStatus.ACTIVE);

        model.addAttribute("employee", employee);
        model.addAttribute("branches", branchService.getAll());
        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("genders", Gender.values());
        model.addAttribute("isEdit", false);
        return "employee/create";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute("employee") Employee employee,
                         @RequestParam("roleCode") String roleCode,
                         @RequestParam(value = "branchId", required = false) Long branchId) {
        
        Role role = roleRepository.findByCode(roleCode).orElse(null);
        if (role != null) {
            employee.setRole(role);
        }
        
        if (branchId != null) {
            employee.setBranch(branchService.getById(branchId));
        }

        employeeService.save(employee);
        return "redirect:/employee";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable("id") Long id, Model model) {
        Employee employee = employeeService.getById(id);
        if (employee == null) {
            return "redirect:/employee";
        }
        model.addAttribute("employee", employee);
        model.addAttribute("branches", branchService.getAll());
        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("genders", Gender.values());
        model.addAttribute("isEdit", true);
        return "employee/create";
    }

    // EDIT (SUBMIT)
    @PostMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id,
                       @ModelAttribute("employee") Employee employee,
                       @RequestParam("roleCode") String roleCode,
                       @RequestParam(value = "branchId", required = false) Long branchId) {
        
        Employee existing = employeeService.getById(id);
        if (existing == null) {
            return "redirect:/employee";
        }
        
        // Update fields
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
        return "redirect:/employee";
    }

    // DELETE
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id) {
        employeeService.delete(id);
        return "redirect:/employee";
    }
}
