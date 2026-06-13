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

    @GetMapping("/form/{id}")
    public String updatePage(@PathVariable Long id, Model model) {
        if(id == 0){
            model.addAttribute("brandRequest", new BrandRequest());
        } else {
            BrandResponse brandResponse = brandService.findById(id);
            BrandRequest brandRequest = new BrandRequest();

            brandRequest.setBrandName(brandResponse.getName());
            brandRequest.setBrandStatus(brandResponse.getStatus());

            model.addAttribute("brandRequest", brandRequest);
        }
        return "form";
    }

    @PostMapping("/form/{id}")
    public String update(@PathVariable Long id,@Valid @ModelAttribute BrandRequest brandRequest,
                         BindingResult result,
                         Model model) {
        if (result.hasErrors()) {
            return "form";
        }
        try {
            if (id == 0){
                brandService.create(brandRequest);
          }else{
                brandService.update(id, brandRequest);
            }
        } catch (DuplicateException e) {
            result.rejectValue("BrandName", "error.BrandName", e.getMessage());
            return "form";
        }
        return "redirect:/brand";
    }
}
