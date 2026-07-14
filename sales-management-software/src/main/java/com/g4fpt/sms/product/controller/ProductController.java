package com.g4fpt.sms.product.controller;

import com.g4fpt.sms.common.exception.NotFoundException;
import com.g4fpt.sms.common.exception.ResourceInUseException;
import com.g4fpt.sms.product.dto.request.ProductFilterRequest;
import com.g4fpt.sms.product.dto.request.ProductRequest;
import com.g4fpt.sms.product.dto.response.ProductResponse;
import com.g4fpt.sms.product.enums.ProductStatus;
import com.g4fpt.sms.common.exception.ValidationException;
import com.g4fpt.sms.product.mapper.ProductMapper;
import com.g4fpt.sms.product.service.*;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/product")
@AllArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final BrandService brandService;
    private final UnitService unitService;
    private final ProductMapper productMapper;

    @GetMapping
    public String list(Model model,
                       @RequestParam(defaultValue = "") String keyword,
                       @RequestParam(required = false) Long brandId,
                       @RequestParam(required = false) Long categoryId,
                       @RequestParam(required = false) ProductStatus status,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(defaultValue = "name") String sortField,
                       @RequestParam(defaultValue = "asc") String sortDir) {

        ProductFilterRequest filter = new ProductFilterRequest();
        filter.setKeyword(keyword);
        filter.setBrandId(brandId);
        filter.setCategoryId(categoryId);
        filter.setStatus(status);

        Page<ProductResponse> productPage = productService.findAll(filter, page, size, sortField, sortDir);

        model.addAttribute("productPage", productPage);
        model.addAttribute("filter", filter);
        model.addAttribute("size", size);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        // Cho dropdown filter
        model.addAttribute("brandList", brandService.findAll());
        model.addAttribute("categoryList", categoryService.findAll());
        model.addAttribute("statuses", ProductStatus.values());

        return "product/list";
    }

    @GetMapping({"/form", "/form/{id}"})
    public String form(Model model,
                       @PathVariable(required = false) Long id,
                       @RequestParam(required = false) String from,
                       RedirectAttributes redirectAttributes) {
        ProductRequest productRequest = new ProductRequest();
        if(id != null) {
            try {
                ProductResponse productResponse = productService.findById(id);
                productRequest = productMapper.toRequest(productResponse);
            }catch (NotFoundException e) {
                redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
                return "redirect:/product";
            }
        }

        addAttributeToForm(model, id);
        model.addAttribute("productRequest",  productRequest);
        model.addAttribute("from", from);
        return "product/form";
    }

    @PostMapping({"/form", "/form/{id}"})
    public String form(@Valid @ModelAttribute ProductRequest productRequest,
                       BindingResult result, Model model,
                       @PathVariable(required = false) Long id,
                       @RequestParam(required = false) String from,
                       RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            addAttributeToForm(model, id);
            model.addAttribute("from", from);
            return "product/form";
        }
        String action;
        try {
            if (id == null) {
                action = "Tạo";
                productService.create(productRequest);
            } else {
                action = "Sửa";
                productService.update(id, productRequest);
            }

        } catch (ValidationException e) {
            addAttributeToForm(model, id);
            model.addAttribute("from", from);
            e.getErrors().forEach(err ->
                    result.rejectValue(err.getField(), "error", err.getMessage())
            );
            return "product/form";

        }catch (NotFoundException | ResourceInUseException e){
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/product";
        }

        redirectAttributes.addFlashAttribute(
                "successMessage",
                action + " sản phẩm thành công!");

        if (from != null && !from.trim().isEmpty()) {
            return "redirect:" + from;
        }
        return "redirect:/product";
    }


    
    private void addAttributeToForm(Model model, Long id){
        if(id != null) {
            model.addAttribute("id", id);
        }
        
        java.util.List<com.g4fpt.sms.product.dto.response.CategoryResponse> activeCategories = categoryService.findAll().stream()
                .filter(c -> "ACTIVE".equals(c.getCategoryStatus().name()))
                .toList();
                
        java.util.List<com.g4fpt.sms.product.dto.response.BrandResponse> activeBrands = brandService.findAll().stream()
                .filter(b -> "ACTIVE".equals(b.getStatus().name()))
                .toList();
                
        model.addAttribute("categoryList", activeCategories);
        model.addAttribute("brandList", activeBrands);
        model.addAttribute("unitList", unitService.findAll());
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id,
                         RedirectAttributes redirectAttributes) {
        try {
            productService.deleteById(id);
        }catch(NotFoundException | ResourceInUseException e){
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/product";
        }
        redirectAttributes.addFlashAttribute("successMessage", "Xóa thành công");
        return "redirect:/product";
    }

}
