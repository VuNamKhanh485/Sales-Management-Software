package com.g4fpt.sms.controller.branch;

import com.g4fpt.sms.dto.request.BranchRequest;
import com.g4fpt.sms.entity.Branch;
import com.g4fpt.sms.service.BranchService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/branch")
public class BranchController {

    private final BranchService branchService;

    public BranchController(BranchService branchService) {
        this.branchService = branchService;
    }

    //LIST
    @GetMapping
    public String list(Model model) {
        model.addAttribute("branches", branchService.getAll());
        return "branch/list";
    }

    // SHOW CREATE
    @GetMapping("/create")
    public String createPage(Model model) {
        model.addAttribute("branchRequest", new BranchRequest());
        return "branch/create";
    }

    // CREATE
    @PostMapping("/create")
    public String create(@ModelAttribute BranchRequest request) {
        branchService.create(request);
        return "redirect:/branch";
    }

    // SHOW UPDATE
    @GetMapping("/update/{id}")
    public String updatePage(@PathVariable Long id, Model model) {
        Branch branch = branchService.getById(id);

        BranchRequest request = new BranchRequest();

        request.setName(branch.getName());
        request.setAddress(branch.getAddress());
        request.setStatus(branch.getStatus());
        request.setManagerId(branch.getManagerId());

        model.addAttribute("branchRequest", request);

        model.addAttribute("id", id);

        return "branch/update";
    }

    // UPDATE
    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id, @ModelAttribute BranchRequest request) {
        branchService.update(id, request);
        return "redirect:/branch";
    }

    // DELETE
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        branchService.delete(id);
        return "redirect:/branch";
    }


}
