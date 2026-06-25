package com.g4fpt.sms.product.controller;

import com.g4fpt.sms.common.exception.NotFoundException;
import com.g4fpt.sms.common.exception.ResourceInUseException;
import com.g4fpt.sms.product.dto.request.BrandRequest;
import com.g4fpt.sms.product.dto.response.BrandResponse;
import com.g4fpt.sms.common.exception.DuplicateException;
import com.g4fpt.sms.product.service.BrandService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/brand")
public class BrandController {
    private final BrandService brandService;

    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    /**
     * List
     */
    @GetMapping
    public String list(Model model,
                       @RequestParam(defaultValue = "") String keyword,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(defaultValue = "name") String sortField,
                       @RequestParam(defaultValue = "asc") String sortDir) {
        Page<BrandResponse> brandPage = brandService.findAll(keyword, size, page, sortField, sortDir);

        model.addAttribute("brandPage", brandPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("size", size);
        model.addAttribute("currentPage", page);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        // Dùng để render nút toggle asc/desc trên header bảng
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        return "brand/list";
    }

    @GetMapping("/form/{id}")
    public String updatePage(@PathVariable Long id, Model model,
                             RedirectAttributes redirectAttributes) {
        BrandRequest brandRequest = new BrandRequest();
        if(id != 0) {
            try {
                BrandResponse brandResponse = brandService.findById(id);
                brandRequest.setBrandName(brandResponse.getName());
                brandRequest.setBrandStatus(brandResponse.getStatus());
            }catch (NotFoundException e){
                redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
                return "redirect:/brand";
            }
        }
        model.addAttribute("brandRequest", brandRequest);
        return "brand/form";
    }

    @PostMapping("/form/{id}")
    public String update(@PathVariable Long id,@Valid @ModelAttribute BrandRequest brandRequest,
                         BindingResult result,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "brand/form";
        }
        String action;
        try {
            if (id == 0) {
                action = "Tạo";
                brandService.create(brandRequest);
            } else {
                action = "Sửa";
                brandService.update(id, brandRequest);
            }
        } catch (DuplicateException | NotFoundException e) {
            result.rejectValue("brandName", "error.brandName", e.getMessage());
            return "brand/form";
        }

        redirectAttributes.addFlashAttribute(
                "successMessage",
                action + " thương hiệu thành công!");

        return "redirect:/brand";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam("id") Long id,
                         RedirectAttributes redirectAttributes) {
        try {
            brandService.deleteById(id);
        }catch (NotFoundException | ResourceInUseException e){
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/brand";
        }
        redirectAttributes.addFlashAttribute("successMessage", "Xóa thành công");
        return "redirect:/brand";
    }

    @PostMapping("/create")
    @ResponseBody
    public org.springframework.http.ResponseEntity<?> createAjax(@Valid @RequestBody BrandRequest brandRequest, BindingResult result) {
        if (result.hasErrors()) {
            return org.springframework.http.ResponseEntity.badRequest().body(result.getAllErrors());
        }
        try {
            BrandResponse response = brandService.create(brandRequest);
            return org.springframework.http.ResponseEntity.ok(response);
        } catch (DuplicateException e) {
            return org.springframework.http.ResponseEntity.badRequest().body(java.util.Collections.singletonMap("error", e.getMessage()));
        }
    }
}
