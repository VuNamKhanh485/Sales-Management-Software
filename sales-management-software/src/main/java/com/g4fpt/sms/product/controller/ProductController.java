package com.g4fpt.sms.product.controller;

import com.g4fpt.sms.product.dto.request.ProductRequest;
import com.g4fpt.sms.product.dto.request.ProductUnitRequest;
import com.g4fpt.sms.product.dto.response.ProductResponse;
import com.g4fpt.sms.product.dto.response.ProductUnitResponse;
import com.g4fpt.sms.product.entity.Product;
import com.g4fpt.sms.product.exception.ValidationException;
import com.g4fpt.sms.product.mapper.ProductMapper;
import com.g4fpt.sms.product.service.*;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/product")
@AllArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final BrandService brandService;
    private final UnitService unitService;
    private final ProductUnitService productUnitService;
    private final ProductMapper productMapper;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("productList", productService.findAll());
        return "product/list";
    }

    @GetMapping({"/form", "/form/{id}"})
    public String form(Model model,
                       @PathVariable(required = false) Long id) {
        ProductRequest productRequest = new ProductRequest();
        if(id != null) {
            ProductResponse productResponse = productService.findById(id);
            productRequest = productMapper.toRequest(productResponse);
            model.addAttribute("id", id);
        }

        model.addAttribute("categoryList", categoryService.findAll());
        model.addAttribute("brandList", brandService.findAll());
        model.addAttribute("unitList", unitService.findAll());
        model.addAttribute("productRequest",  productRequest);
        return "product/form";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute ProductRequest productRequest,
                         BindingResult result) {
        if (result.hasErrors()) {
            return "product/form";
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
/**
    @GetMapping
    public String list(Model model) {
        model.addAttribute("productUnitList",productUnitService.findAll());
        return "productunit/list";
    }

    @GetMapping("/create")
    public String productUnitForm(Model model) {
        model.addAttribute("productUnitRequest", new ProductUnitRequest());
        return "productunit/create";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute  ProductUnitRequest productUnitRequest) {
        productUnitService.create(productUnitRequest);
        return "redirect:/productunit";
    }

    @GetMapping("/update/{id}")
    public String updatePage(@PathVariable Long id, Model model) {
        ProductUnitResponse productUnitResponse =  productUnitService.findById(id);

        model.addAttribute("productUnitResponse", productUnitResponse);
        return "productunit/update";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id, @ModelAttribute  ProductUnitRequest productUnitRequest) {
        productUnitService.update(id,productUnitRequest);
        return "redirect:/productunit";
    }
}
*/
}
