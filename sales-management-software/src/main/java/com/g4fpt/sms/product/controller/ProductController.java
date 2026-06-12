package com.g4fpt.sms.product.controller;

import com.g4fpt.sms.product.dto.request.ProductRequest;
import com.g4fpt.sms.product.exception.ValidationException;
import com.g4fpt.sms.product.service.BrandService;
import com.g4fpt.sms.product.service.CategoryService;
import com.g4fpt.sms.product.service.ProductService;
import com.g4fpt.sms.product.service.UnitService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final BrandService brandService;
    private final UnitService unitService;
    public ProductController(ProductService productService, CategoryService categoryService, BrandService brandService, UnitService unitService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.brandService = brandService;
        this.unitService = unitService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("productList", productService.findAll());
        return "product/list";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("productRequest", new ProductRequest());
        model.addAttribute("categoryList", categoryService.findAll());
        model.addAttribute("brandList", brandService.findAll());
        model.addAttribute("unitList", unitService.findAll());
        return "product/create";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute ProductRequest productRequest,
                         BindingResult result) {
        if (result.hasErrors()) {
            return "product/create";
        }

        try{
            productService.create(productRequest);
        }catch(ValidationException e){
            e.getErrors().forEach(err ->
                    result.rejectValue(err.getField(), "error", err.getMessage())
            );
        }

        return "redirect:/product";
    }

    /**
     * Update all attribute
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/update/{id}")
    public String update(@PathVariable Long id, Model model) {
        model.addAttribute("productResponse", productService.findById(id));
        model.addAttribute("categoryList", categoryService.findAll());
        model.addAttribute("brandList", brandService.findAll());
        return "save";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute ProductRequest productRequest,
                         BindingResult result,
                         Model model) {
        if (result.hasErrors()) {
            model.addAttribute("productResponse", productService.findById(id));
            model.addAttribute("categoryList", categoryService.findAll());
            model.addAttribute("brandList", brandService.findAll());
            return "save";
        }
        try {
            productService.update(id, productRequest);
        }catch(ValidationException e){
            e.getErrors().forEach(err ->
                    result.rejectValue(err.getField(), "error", err.getMessage())
            );
            return "save";
        }

        return "redirect:/product";
    }


}
