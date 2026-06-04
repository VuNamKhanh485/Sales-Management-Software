package com.g4fpt.sms.product.controller;

import com.g4fpt.sms.product.dto.request.ProductRequest;
import com.g4fpt.sms.product.dto.request.ProductUnitRequest;
import com.g4fpt.sms.product.entity.Product;
import com.g4fpt.sms.product.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public String productPage(Model model) {
        model.addAttribute("productList", productService.getAll());
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
        Product product = productService.findById(id);

        ProductRequest productRequest = new ProductRequest();

        productRequest.setCategory(product.getCategory());
        productRequest.setBrand(product.getBrand());
        productRequest.setName(product.getName());
        productRequest.setDescription(product.getDescription());
        productRequest.setStatus(product.getStatus());
        productRequest.setNote(product.getNote());

        List<ProductUnitRequest> productUnitRequest = product.getProductunits()
                        .stream()
                                .map(productUnit -> {
                                    ProductUnitRequest pRequest = new ProductUnitRequest();

                                    pRequest.setBarcodeUnit(productUnit.getBarcodeUnit());
                                    pRequest.setSku(productUnit.getSku());
                                    pRequest.setUnitPrice(productUnit.getPrice());
                                    pRequest.setIsBaseUnit(productUnit.getIsBaseUnit());
                                    pRequest.setConventionValue(productUnit.getConventionValue());

                                    return pRequest;
                                })
                                        .toList();

        productRequest.setProductUnitsRequest(productUnitRequest);

        model.addAttribute("productRequest", productRequest);
        return "product/update";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id, @ModelAttribute ProductRequest productRequest) {
        productService.update(id, productRequest);
        return "redirect:/product";
    }


}
