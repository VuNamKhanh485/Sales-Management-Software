package com.g4fpt.sms.product.controller;

import com.g4fpt.sms.product.dto.request.ProductRequest;
import com.g4fpt.sms.product.dto.response.ProductResponse;
import com.g4fpt.sms.product.service.BrandService;
import com.g4fpt.sms.product.service.CategoryService;
import com.g4fpt.sms.product.service.ProductService;
import com.g4fpt.sms.product.service.UnitService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
    public String create(@ModelAttribute ProductRequest productRequest) {
        productService.create(productRequest);
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
        ProductResponse productResponse = productService.findById(id);
        model.addAttribute("productResponse", productResponse);
        model.addAttribute("categoryList", categoryService.findAll());  // thêm
        model.addAttribute("brandList", brandService.findAll());
        return "product/update";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id, @ModelAttribute ProductRequest productRequest) {
        productService.update(id, productRequest);
        return "redirect:/product";
    }


}
