package com.g4fpt.sms.product.controller;

import com.g4fpt.sms.product.dto.request.BrandRequest;
import com.g4fpt.sms.product.dto.response.BrandResponse;
import com.g4fpt.sms.product.exception.DuplicateException;
import com.g4fpt.sms.product.service.BrandService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
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
    public String list(Model model,
                       @RequestParam(defaultValue = "") String keyword,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(defaultValue = "name") String sortField,
                       @RequestParam(defaultValue = "asc") String sortDir) {
        Page<BrandResponse> brandPage = brandService.findAll(keyword, page, size, sortField, sortDir);

        model.addAttribute("brandPage", brandPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("size", size);
        model.addAttribute("currentPage", page);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        // Dùng để render nút toggle asc/desc trên header bảng
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
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
        return "brand/form";
    }

    @PostMapping("/form/{id}")
    public String update(@PathVariable Long id,@Valid @ModelAttribute BrandRequest brandRequest,
                         BindingResult result,
                         Model model) {
        if (result.hasErrors()) {
            return "brand/form";
        }
        try {
            if (id == 0){
                brandService.create(brandRequest);
          }else{
                brandService.update(id, brandRequest);
            }
        } catch (DuplicateException e) {
            result.rejectValue("BrandName", "error.BrandName", e.getMessage());
            return "brand/form";
        }
        return "redirect:/brand";
    }
}
