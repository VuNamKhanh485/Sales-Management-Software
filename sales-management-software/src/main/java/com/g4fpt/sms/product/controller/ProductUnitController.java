package com.g4fpt.sms.product.controller;

import com.g4fpt.sms.product.dto.request.ProductUnitRequest;
import com.g4fpt.sms.product.dto.response.ProductUnitResponse;
import com.g4fpt.sms.product.entity.ProductUnit;
import com.g4fpt.sms.product.service.ProductUnitService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/productunit")
public class ProductUnitController {

    private final ProductUnitService productUnitService;

    public ProductUnitController(ProductUnitService productUnitService) {
        this.productUnitService = productUnitService;
    }

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
