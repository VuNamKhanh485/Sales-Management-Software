package com.g4fpt.sms.product.controller;

import com.g4fpt.sms.product.dto.request.CategoryRequest;
import com.g4fpt.sms.product.dto.response.CategoryResponse;
import com.g4fpt.sms.product.service.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    public String create(@ModelAttribute CategoryRequest categoryRequest) {
        categoryService.create(categoryRequest);
        return "redirect:/category";
    }

    @GetMapping("/update/{id}")
    public String updatePage(@PathVariable Long id, Model model) {
        CategoryResponse categoryResponse = categoryService.findById(id);

        model.addAttribute("categoryResponse", categoryResponse);
        return "category/update";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id, @ModelAttribute CategoryRequest categoryRequest) {
        categoryService.update(id, categoryRequest);
        return "redirect:/category";
    }
}
