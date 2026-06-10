package com.g4fpt.sms.product.controller;

import com.g4fpt.sms.product.dto.request.CategoryRequest;
import com.g4fpt.sms.product.dto.response.CategoryResponse;
import com.g4fpt.sms.product.exception.DuplicateException;
import com.g4fpt.sms.product.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.naming.Binding;

@Controller
@RequestMapping("category")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("categoryList", categoryService.findAll());
        return "category/list";
    }

    @GetMapping("/create")
    public String createPage(Model model) {
        model.addAttribute("categoryRequest", new CategoryRequest());
        return "category/create";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute CategoryRequest categoryRequest,
                         BindingResult result) {
        if (result.hasErrors()) {
            return "category/create";
        }

        try {
            categoryService.create(categoryRequest);
        }catch(DuplicateException e) {
            result.rejectValue("CategoryName", "error.CategoryName",e.getMessage());
        }
        return "redirect:/category";
    }

    @GetMapping("/update/{id}")
    public String updatePage(@PathVariable Long id, Model model) {
        CategoryResponse categoryResponse = categoryService.findById(id);
        model.addAttribute("categoryResponse", categoryResponse);
        return "category/update";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute CategoryRequest categoryRequest,
                         BindingResult result) {
        if (result.hasErrors()) {
            return "category/update";
        }
        try {
            categoryService.update(id, categoryRequest);
        }catch(DuplicateException e) {
            result.rejectValue("CategoryName", "error.CategoryName",e.getMessage());
        }
        return "redirect:/category";
    }
}
