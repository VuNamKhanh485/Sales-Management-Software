package com.g4fpt.sms.branch.controller;

import com.g4fpt.sms.branch.dto.BranchRequest;
import com.g4fpt.sms.branch.entity.Branch;
import com.g4fpt.sms.branch.service.BranchService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@Controller
@RequestMapping("/branch")
public class BranchController {

    private final BranchService branchService;

    public BranchController(BranchService branchService) {
        this.branchService = branchService;
    }

    //LIST
    @GetMapping
    public String list(@RequestParam(required = false) String keyword,
                       @RequestParam(required = false, defaultValue = "name") String searchType,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "5") int size,
                       Model model) {

        PageRequest pageRequest = PageRequest.of(page, size);

        Page<Branch> branchPage;

        if (keyword == null || keyword.trim().isEmpty()) {
            branchPage = branchService.getAll(pageRequest);
        } else {
            branchPage = branchService.search(searchType, keyword, pageRequest);
        }

        model.addAttribute("page", "branch");
        model.addAttribute("branchPage", branchPage);
        model.addAttribute("branches", branchPage.getContent());
        model.addAttribute("keyword", keyword);
        model.addAttribute("searchType", searchType);

        return "branch/list";
    }

    //DETAIL
    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, Model model) {

        Branch branch = branchService.getById(id);
        model.addAttribute("branch", branch);

        return "branch/detail";
    }

    //CREATE
    @GetMapping("/create")
    public String createForm(Model model) {

        model.addAttribute("branch", new BranchRequest());
        model.addAttribute("isEdit", false);

        return "branch/form";
    }

    // EDIT
    @GetMapping("/edit/{id}")
    public String editForm(
            @PathVariable Long id,
            Model model) {

        Branch branch = branchService.getById(id);

        model.addAttribute("branch", branch);
        model.addAttribute("isEdit", true);

        return "branch/form";
    }

    // SAVE
    @PostMapping("/save")
    public String save(@RequestParam(required = false) Long id,
                       @ModelAttribute("branch") BranchRequest request,
                       Model model) {
        if (id == null) {
            try {
                branchService.create(request);

                return "redirect:/branch";
            } catch (Exception e) {
                model.addAttribute("branch", request);
                model.addAttribute(
                        "errors",
                        Arrays.asList(e.getMessage().split("\\|"))
                );
                return "branch/form";
            }
        } else {
            try {

                branchService.update(id, request);
                return "redirect:/branch";

            } catch (Exception e) {

                model.addAttribute("branch", request);
                model.addAttribute("id", id);
                model.addAttribute("viewOnly", false);

                model.addAttribute(
                        "errors",
                        Arrays.asList(
                                e.getMessage().split("\\|"))
                );

                return "branch/form";
            }
        }
    }


    //DELETE
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {

        branchService.delete(id);

        return "redirect:/branch";
    }


}
