package com.g4fpt.sms.product.controller;

import com.g4fpt.sms.common.exception.NotFoundException;
import com.g4fpt.sms.common.exception.ResourceInUseException;
import com.g4fpt.sms.product.dto.request.CategoryRequest;
import com.g4fpt.sms.product.dto.response.CategoryResponse;
import com.g4fpt.sms.common.exception.DuplicateException;
import com.g4fpt.sms.product.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("category")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public String list(Model model,
                       @RequestParam(defaultValue = "") String keyword,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(defaultValue = "name") String sortField,
                       @RequestParam(defaultValue = "asc") String sortDir) {
        Page<CategoryResponse> categoryPage = categoryService.findAll(keyword, size, page, sortField, sortDir);

        model.addAttribute("categoryPage", categoryPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("size", size);
        model.addAttribute("currentPage", page);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        return "category/list";
    }

    @GetMapping("/form/{id}")
    public String updatePage(@PathVariable Long id, Model model,
                             RedirectAttributes redirectAttributes) {
        CategoryRequest categoryRequest = new CategoryRequest();
        if(id == 0){
            model.addAttribute("categoryRequest", categoryRequest);
        }else {
            try {
                CategoryResponse categoryResponse = categoryService.findById(id);

                categoryRequest.setCategoryName(categoryResponse.getName());
                categoryRequest.setCategoryStatus(categoryResponse.getCategoryStatus());
                categoryRequest.setDescription(categoryResponse.getDescription());

                model.addAttribute("categoryRequest", categoryRequest);
            }catch (NotFoundException e){
                redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
                return "redirect:/category";
            }
        }
        return "category/form";
    }

    @PostMapping("/form/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute CategoryRequest categoryRequest,
                         BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "category/form";
        }
        String action;
        try {
            if(id == 0){
                action = "Tạo";
                categoryService.create(categoryRequest);
            }else {
                action = "Sửa";
                categoryService.update(id, categoryRequest);
            }
        }catch(DuplicateException | NotFoundException e) {
            result.rejectValue("CategoryName", "error.CategoryName",e.getMessage());
            return "category/form";
        }

        redirectAttributes.addFlashAttribute(
                "successMessage",
                action + " danh mục thành công!");
        return "redirect:/category";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam("id") Long id,
                         RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteById(id);
        }catch (NotFoundException | ResourceInUseException e){
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/category";
        }
        redirectAttributes.addFlashAttribute("successMessage", "Xóa thành công");
        return "redirect:/category";
    }
}
