package com.g4fpt.sms.employee.controller;

import com.g4fpt.sms.auth.dto.SessionUser;
import com.g4fpt.sms.auth.util.SessionConstants;
import com.g4fpt.sms.branch.repository.BranchRepository;
import com.g4fpt.sms.employee.dto.EmployeeForm;
import com.g4fpt.sms.employee.repository.RoleRepository;
import com.g4fpt.sms.employee.service.EmployeeService;
import com.g4fpt.sms.employee.utils.Gender;
import com.g4fpt.sms.employee.utils.WorkStatus;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/employee")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final RoleRepository roleRepository;
    private final BranchRepository branchRepository;

    public EmployeeController(EmployeeService employeeService,
                              RoleRepository roleRepository,
                              BranchRepository branchRepository) {
        this.employeeService = employeeService;
        this.roleRepository = roleRepository;
        this.branchRepository = branchRepository;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String keyword,
                       @RequestParam(required = false) Long branchId,
                       @RequestParam(required = false) Long roleId,
                       @RequestParam(required = false) WorkStatus status,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "5") int size,
                       HttpSession session,
                       Model model) {

        SessionUser currentUser =
                (SessionUser) session.getAttribute(SessionConstants.LOGGED_IN_USER);

        if (!canAccess(currentUser)) {
            return "redirect:/error/403";
        }

        Pageable pageable = PageRequest.of(page, size);

        model.addAttribute("employeePage",
                employeeService.searchEmployees(
                        keyword,
                        branchId,
                        roleId,
                        status,
                        pageable,
                        currentUser
                ));

        model.addAttribute("keyword", keyword);
        model.addAttribute("branchId", branchId);
        model.addAttribute("roleId", roleId);
        model.addAttribute("status", status);

        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("branches", branchRepository.findAll());
        model.addAttribute("statuses", WorkStatus.values());

        return "employee/list";
    }

    @GetMapping("/new")
    public String createForm(HttpSession session, Model model) {
        SessionUser currentUser =
                (SessionUser) session.getAttribute(SessionConstants.LOGGED_IN_USER);

        if (!canAccess(currentUser)) {
            return "redirect:/error/403";
        }

        EmployeeForm form = new EmployeeForm();

        if ("BRANCH_MANAGER".equals(currentUser.getRoleCode())) {
            form.setBranchId(currentUser.getBranchId());
        }

        model.addAttribute("employeeForm", form);
        addFormData(model);

        return "employee/form";
    }

    @PostMapping("/new")
    public String create(@Valid @ModelAttribute("employeeForm") EmployeeForm form,
                         BindingResult bindingResult,
                         HttpSession session,
                         Model model,
                         RedirectAttributes redirectAttributes) {

        SessionUser currentUser =
                (SessionUser) session.getAttribute(SessionConstants.LOGGED_IN_USER);

        if (!canAccess(currentUser)) {
            return "redirect:/error/403";
        }

        if (bindingResult.hasErrors()) {
            addFormData(model);
            return "employee/form";
        }

        try {
            employeeService.create(form, currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm nhân viên thành công");
            return "redirect:/employee";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            addFormData(model);
            return "employee/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id,
                           HttpSession session,
                           Model model) {

        SessionUser currentUser =
                (SessionUser) session.getAttribute(SessionConstants.LOGGED_IN_USER);

        if (!canAccess(currentUser)) {
            return "redirect:/error/403";
        }

        try {
            model.addAttribute("employeeForm", employeeService.getFormById(id, currentUser));
            addFormData(model);
            return "employee/form";
        } catch (RuntimeException e) {
            return "redirect:/error/403";
        }
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("employeeForm") EmployeeForm form,
                         BindingResult bindingResult,
                         HttpSession session,
                         Model model,
                         RedirectAttributes redirectAttributes) {

        SessionUser currentUser =
                (SessionUser) session.getAttribute(SessionConstants.LOGGED_IN_USER);

        if (!canAccess(currentUser)) {
            return "redirect:/error/403";
        }

        if (bindingResult.hasErrors()) {
            addFormData(model);
            return "employee/form";
        }

        try {
            employeeService.update(id, form, currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật nhân viên thành công");
            return "redirect:/employee";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            addFormData(model);
            return "employee/form";
        }
    }

    @PostMapping("/{id}/toggle-status")
    public String toggleStatus(@PathVariable Long id,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {

        SessionUser currentUser =
                (SessionUser) session.getAttribute(SessionConstants.LOGGED_IN_USER);

        if (!canAccess(currentUser)) {
            return "redirect:/error/403";
        }

        try {
            employeeService.toggleStatus(id, currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật trạng thái thành công");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/employee";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {

        SessionUser currentUser =
                (SessionUser) session.getAttribute(SessionConstants.LOGGED_IN_USER);

        if (!canAccess(currentUser)) {
            return "redirect:/error/403";
        }

        try {
            employeeService.delete(id, currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa nhân viên thành công");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/employee";
    }

    private void addFormData(Model model) {
        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("branches", branchRepository.findAll());
        model.addAttribute("genders", Gender.values());
        model.addAttribute("statuses", WorkStatus.values());
    }

    private boolean canAccess(SessionUser user) {
        if (user == null) {
            return false;
        }

        return "OWNER".equals(user.getRoleCode())
                || "BRANCH_MANAGER".equals(user.getRoleCode());
    }
}