package com.g4fpt.sms.product.controller;

import com.g4fpt.sms.product.dto.request.BrandRequest;
import com.g4fpt.sms.product.dto.response.BrandResponse;
import com.g4fpt.sms.product.exception.DuplicateException;
import com.g4fpt.sms.product.service.BrandService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/brand")
public class BrandController {
    private final BrandService brandService;

    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    /**
     * List
     */
    @GetMapping
    public String list(Model model) {
        model.addAttribute("brand", brandService.findAll());
        return "brand/list";
    }

    @GetMapping("/create")
    public String createPage(Model model) {
        model.addAttribute("brandRequest", new BrandRequest());
        return "brand/create";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute BrandRequest request,
                         BindingResult result) {
        if (result.hasErrors()) {
            return "brand/create";
        }

        try {
            brandService.create(request);
        }catch(DuplicateException e){
            result.rejectValue("BrandName", "error.BrandName",e.getMessage());
            return "brand/create";
        }
        return "redirect:/brand";
    }

    @GetMapping("/update/{id}")
    public String updatePage(@PathVariable Long id, Model model) {
        BrandResponse brandResponse = brandService.findById(id);

        model.addAttribute("brandResponse", brandResponse);
        model.addAttribute("id", id);
        return "brand/update";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id,@Valid @ModelAttribute BrandRequest brandRequest,
                         BindingResult result) {
        if (result.hasErrors()) {
            return "brand/update";
        }

        try {
            brandService.update(id, brandRequest);
        }catch(DuplicateException e){
            result.rejectValue("BrandName", "error.BrandName",e.getMessage());
        }

        return "redirect:/brand";
    }







}
