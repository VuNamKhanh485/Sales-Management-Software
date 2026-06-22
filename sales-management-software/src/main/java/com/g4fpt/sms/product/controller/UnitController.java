package com.g4fpt.sms.product.controller;

import com.g4fpt.sms.product.dto.request.UnitRequest;
import com.g4fpt.sms.product.dto.response.UnitResponse;
import com.g4fpt.sms.common.exception.DuplicateException;
import com.g4fpt.sms.product.service.UnitService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("unit")
public class UnitController {

    private final UnitService unitService;

    public UnitController(UnitService unitService) {
        this.unitService = unitService;
    }

    @GetMapping
    public String list(Model model,
                       @RequestParam(defaultValue = "") String keyword,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(defaultValue = "name") String sortField,
                       @RequestParam(defaultValue = "asc") String sortDir){
        Page<UnitResponse> unitPage = unitService.findAll(keyword, size, page, sortField, sortDir);

        model.addAttribute("unitPage", unitPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("size", size);
        model.addAttribute("currentPage", page);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        // Dùng để render nút toggle asc/desc trên header bảng
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        return "unit/list";
    }

    @GetMapping("/create")
    public String createPage(Model model){
        model.addAttribute("unit", new UnitRequest());
        return "unit/create";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute UnitRequest unitRequest,
                         BindingResult result){
        if (result.hasErrors()){
            return "unit/create";
        }
        try{
            unitService.create(unitRequest);
        }catch(DuplicateException e){
            result.rejectValue("UnitName", "error.UnitName",e.getMessage());
        }

        return "redirect:/unit";
    }

    @GetMapping("/update/{id}")
    public String updatePage(@PathVariable Long id, Model model){
        UnitResponse unitResponse = unitService.findById(id);

        model.addAttribute("unitResponse", unitResponse);

        return "unit/update";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute UnitRequest unitRequest,
                         BindingResult result){
        if (result.hasErrors()){
            return "unit/update";
        }

        try{
            unitService.update(id,unitRequest);
        }catch(DuplicateException e){
            result.rejectValue("UnitName", "error.UnitName",e.getMessage());
        }

        return "redirect:/unit";
    }
}
