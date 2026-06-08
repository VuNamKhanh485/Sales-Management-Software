package com.g4fpt.sms.branch.controller;

import com.g4fpt.sms.branch.dto.BranchRequest;
import com.g4fpt.sms.branch.service.BranchService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/branch")
public class BranchController {

    private final BranchService branchService;

    public BranchController(
            BranchService branchService) {

        this.branchService = branchService;
    }

    //LIST
    @GetMapping
    public String list(Model model) {
        model.addAttribute("page", "branch");
        model.addAttribute(
                "branches",
                branchService.getAll());
        return "branch/list";
    }

    //CREATE
    @GetMapping("/create")
    public String createForm(Model model) {

        model.addAttribute(
                "branch",
                new BranchRequest());

        return "branch/create";
    }

    @PostMapping("/create")
    public String create(
            @ModelAttribute("branch")
            BranchRequest request) {

        branchService.create(request);

        return "redirect:/branch";
    }

    //UPDATE
    @GetMapping("/edit/{id}")
    public String editForm(
            @PathVariable Long id,
            Model model) {

        model.addAttribute(
                "branch",
                branchService.getById(id));

        return "branch/edit";
    }

    //DELETE
    @GetMapping("/delete/{id}")
    public String delete(
            @PathVariable Long id) {

        branchService.delete(id);

        return "redirect:/branch";
    }

    //DETAIL
    @GetMapping("/detail/{id}")
    public String detail(
            @PathVariable Long id,
            Model model) {

        model.addAttribute(
                "branch",
                branchService.getById(id));

        return "branch/detail";
    }
}
