package com.g4fpt.sms.branch.controller;

import com.g4fpt.sms.branch.dto.BranchRequest;
import com.g4fpt.sms.branch.entity.Branch;
import com.g4fpt.sms.branch.service.BranchService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

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
            BranchRequest request,
            Model model) {

        try {
            branchService.create(request);

            return "redirect:/branch";
        } catch (Exception e) {
            model.addAttribute("branch", request);
            model.addAttribute(
                    "errors",
                    Arrays.asList(e.getMessage().split("\\|"))
            );
            return "branch/create";
        }

    }

    //UPDATE
    @GetMapping("/edit/{id}")
    public String editForm(
            @PathVariable Long id,
            Model model) {

        Branch branch = branchService.getById(id);
        model.addAttribute("branch", branch);

        return "branch/edit";
    }

    @PostMapping("/edit/{id}")
    public String update(
            @PathVariable Long id,
            @ModelAttribute("branch") BranchRequest request,
            Model model) {

        try {
            branchService.update(id, request);
            return "redirect:/branch";
        } catch (Exception e) {
            model.addAttribute("branch", request);
            model.addAttribute("id", id);
            model.addAttribute(
                    "errors",
                    Arrays.asList(
                            e.getMessage().split("\\|"))
            );
            return "branch/edit";
        }
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