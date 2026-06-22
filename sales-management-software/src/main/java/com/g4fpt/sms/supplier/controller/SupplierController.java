package com.g4fpt.sms.supplier.controller;

import com.g4fpt.sms.common.exception.DuplicateException;
import com.g4fpt.sms.supplier.dto.request.SupplierRequest;
import com.g4fpt.sms.supplier.dto.response.SupplierResponse;
import com.g4fpt.sms.supplier.service.SupplierService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/supplier")
@AllArgsConstructor
public class SupplierController {
    private SupplierService supplierService;

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

    @GetMapping("form{id}")
    public String formPage(@PathVariable Long id, Model model) {
        SupplierRequest supplierRequest = new SupplierRequest();
        if(id!=0){
            SupplierResponse supplierResponse = supplierService.findById(id);

            supplierRequest.setCode(supplierResponse.getCode());
            supplierRequest.setName(supplierResponse.getName());
            supplierRequest.setPhone(supplierResponse.getPhone());
            supplierRequest.setEmail(supplierResponse.getEmail());
            supplierRequest.setAddress(supplierResponse.getAddress());
            supplierRequest.setStatus(supplierResponse.getStatus());
            supplierRequest.setNote(supplierResponse.getNote());

        }
        model.addAttribute("supplierRequest",supplierRequest);
        return "supplier/form";
    }

    @PostMapping("form{id}")
    public String form(@PathVariable Long id, Model model,
                       @Valid @ModelAttribute SupplierRequest supplierRequest,
                       BindingResult result) {
        if (result.hasErrors()) {
            return "supplier/form";
        }
        try{
            if(id==0){
                supplierService.create(supplierRequest);
            }else{
                supplierService.update(supplierRequest, id);
            }
        }catch (DuplicateException e){
            result.rejectValue("supplierCode", "error.supplierCode", e.getMessage());
            return "supplier/form";
        }
        return "redirect:/supplier";
    }
}
