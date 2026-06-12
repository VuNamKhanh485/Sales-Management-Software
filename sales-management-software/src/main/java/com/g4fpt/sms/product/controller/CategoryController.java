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

    @GetMapping("/save/{id}")
    public String updatePage(@PathVariable Long id, Model model) {
        if(id == 0){
            model.addAttribute("categoryRequest", new CategoryRequest());
        }else {
            CategoryResponse categoryResponse = categoryService.findById(id);

            CategoryRequest categoryRequest = new CategoryRequest();
            categoryRequest.setCategoryName(categoryResponse.getName());
            categoryRequest.setCategoryStatus(categoryResponse.getCategoryStatus());
            categoryRequest.setDescription(categoryResponse.getDescription());

            model.addAttribute("categoryRequest", categoryRequest);
        }
        return "category/save";
    }

    @PostMapping("/save/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute CategoryRequest categoryRequest,
                         BindingResult result) {
        if (result.hasErrors()) {
            return "category/save";
        }
        try {
            if(id == 0){
                categoryService.create(categoryRequest);
            }else {
                categoryService.update(id, categoryRequest);
            }
        }catch(DuplicateException e) {
            result.rejectValue("CategoryName", "error.CategoryName",e.getMessage());
            return "category/save";
        }
        return "redirect:/category";
    }
}
