package com.g4fpt.sms.product.controller;

import com.g4fpt.sms.product.dto.request.UnitRequest;
import com.g4fpt.sms.product.entity.Unit;
import com.g4fpt.sms.product.service.UnitService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("unit")
public class UnitController {

    private final UnitService unitService;

    public UnitController(UnitService unitService) {
        this.unitService = unitService;
    }

    @GetMapping
    public String unitPage(Model model){
        model.addAttribute("unitList", unitService.findAll());
        return "unit/list";
    }

    @GetMapping("/create")
    public String createPage(Model model){
        model.addAttribute("unit", new UnitRequest());
        return "unit/create";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute UnitRequest unitRequest){
        unitService.create(unitRequest);
        return "redirect:/productunit";
    }

    @GetMapping("/update/{id}")
    public String updatePage(@PathVariable Long id, Model model){
        Unit unit = unitService.findById(id);

        UnitRequest unitRequest = new UnitRequest();
        unitRequest.setName(unit.getName());

        model.addAttribute("unitRequest", unitRequest);

        return "unit/update";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id, @ModelAttribute UnitRequest unitRequest){
        Unit unit = unitService.findById(id);
        unit.setName(unitRequest.getName());
        unitService.update(id,unitRequest);
        return "redirect:/productunit";
    }
}
