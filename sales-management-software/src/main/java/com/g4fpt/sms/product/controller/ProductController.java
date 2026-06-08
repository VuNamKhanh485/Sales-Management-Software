package com.g4fpt.sms.product.controller;

import com.g4fpt.sms.product.dto.request.ProductRequest;
import com.g4fpt.sms.product.dto.response.ProductResponse;
import com.g4fpt.sms.product.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("productList", productService.findAll());
        return "product/list";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("productRequest", new ProductRequest());
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
        return "product/update";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id, @ModelAttribute ProductRequest productRequest) {
        productService.update(id, productRequest);
        return "redirect:/product";
    }


}
