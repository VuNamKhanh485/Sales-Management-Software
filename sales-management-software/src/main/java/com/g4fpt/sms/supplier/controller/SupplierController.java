package com.g4fpt.sms.supplier.controller;

import com.g4fpt.sms.common.exception.DuplicateException;
import com.g4fpt.sms.common.exception.NotFoundException;
import com.g4fpt.sms.common.exception.ResourceInUseException;
import com.g4fpt.sms.supplier.dto.request.SupplierRequest;
import com.g4fpt.sms.supplier.dto.response.SupplierResponse;
import com.g4fpt.sms.supplier.mapper.SupplierMapper;
import com.g4fpt.sms.supplier.service.SupplierService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/supplier")
@AllArgsConstructor
public class SupplierController {
    private SupplierService supplierService;
    private SupplierMapper supplierMapper;

    @GetMapping
    public String list(Model model,
                       @RequestParam(defaultValue = "") String keyword,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(defaultValue = "name") String sortField,
                       @RequestParam(defaultValue = "asc") String sortDir) {

        Page<SupplierResponse> supplierPage = supplierService.findAll(keyword, page, size, sortField, sortDir);

        model.addAttribute("supplierPage", supplierPage);
        model.addAttribute("page", page);
        model.addAttribute("keyword", keyword);
        model.addAttribute("size", size);
        model.addAttribute("currentPage", page);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        // Dùng để render nút toggle asc/desc trên header bảng
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        return "supplier/list";
    }

    @GetMapping("/form/{id}")
    public String form(@PathVariable Long id, Model model,
                       RedirectAttributes redirectAttributes) {
        SupplierRequest supplierRequest = new SupplierRequest();
        if(id!=0){
            try {
                SupplierResponse supplierResponse = supplierService.findById(id);
                supplierRequest = supplierMapper.toRequest(supplierResponse);
            }catch(NotFoundException e){
                redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
                return "redirect:/supplier";
            }
        }
        model.addAttribute("supplierRequest",supplierRequest);
        return "supplier/form";
    }

    @PostMapping("/form/{id}")
    public String form(@PathVariable Long id, Model model,
                       @Valid @ModelAttribute SupplierRequest supplierRequest,
                       BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "supplier/form";
        }
        String action;
        try{
            if(id==0){
                action = "Tạo";
                supplierService.create(supplierRequest);
            }else{
                action = "Sửa";
                supplierService.update(supplierRequest, id);
            }
        }catch (DuplicateException e){
            result.rejectValue("supplierCode", "error.supplierCode", e.getMessage());
            return "supplier/form";
        }
        redirectAttributes.addFlashAttribute(
                "successMessage",
                action + " nhà cung cấp thành công!");
        return "redirect:/supplier";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam("id") Long id,
                         RedirectAttributes redirectAttributes) {
        try {
            supplierService.deleteById(id);
        }catch (NotFoundException | ResourceInUseException e){
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/supplier";
        }
        redirectAttributes.addFlashAttribute("successMessage", "Xóa thành công");
        return "redirect:/supplier";
    }
}
