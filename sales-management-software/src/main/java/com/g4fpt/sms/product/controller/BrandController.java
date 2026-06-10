package com.g4fpt.sms.product.controller;

import com.g4fpt.sms.product.dto.request.BrandRequest;
import com.g4fpt.sms.product.dto.response.BrandResponse;
import com.g4fpt.sms.product.exception.DuplicateException;
import com.g4fpt.sms.product.service.BrandService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
    public String create(@ModelAttribute BrandRequest request) {
        try {
            brandService.create(request);
        }catch(DuplicateException e){
            
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
    public String update(@PathVariable Long id, @ModelAttribute BrandRequest brandRequest) {
        brandService.update(id, brandRequest);
        return "redirect:/brand";
    }







}
