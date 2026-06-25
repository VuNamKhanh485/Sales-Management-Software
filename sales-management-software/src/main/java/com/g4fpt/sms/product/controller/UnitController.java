package com.g4fpt.sms.product.controller;

import com.g4fpt.sms.common.exception.NotFoundException;
import com.g4fpt.sms.common.exception.ResourceInUseException;
import com.g4fpt.sms.product.dto.request.UnitRequest;
import com.g4fpt.sms.product.dto.response.UnitResponse;
import com.g4fpt.sms.common.exception.DuplicateException;
import com.g4fpt.sms.product.service.UnitService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("unit")
public class UnitController {

    private final UnitService unitService;

    public UnitController(UnitService unitService) {
        this.unitService = unitService;
    }

    @GetMapping
    public String list(Model model,
                       @RequestParam(defaultValue = "") String keyword,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(defaultValue = "name") String sortField,
                       @RequestParam(defaultValue = "asc") String sortDir){
        Page<UnitResponse> unitPage = unitService.findAll(keyword, size, page, sortField, sortDir);

        model.addAttribute("unitPage", unitPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("size", size);
        model.addAttribute("currentPage", page);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        // Dùng để render nút toggle asc/desc trên header bảng
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        return "unit/list";
    }

    @GetMapping("/form/{id}")
    public String updatePage(@PathVariable Long id, Model model,
                             RedirectAttributes redirectAttributes) {
        UnitRequest unitRequest = new UnitRequest();
        if(id != 0){
            try {
                UnitResponse unitResponse = unitService.findById(id);
                unitRequest.setName(unitResponse.getName());
            }catch (NotFoundException e){
                redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
                return "redirect:/unit";
            }
        }
        model.addAttribute("unitRequest", unitRequest);
        return "unit/form";
    }

    @PostMapping("/form/{id}")
    public String update(@PathVariable Long id,@Valid @ModelAttribute UnitRequest unitRequest,
                         BindingResult result,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "unit/form";
        }
        String action;
        try {
            if (id == 0){
                action = "Tạo";
                unitService.create(unitRequest);
            }else{
                action = "Sửa";
                unitService.update(id, unitRequest);
            }
        } catch (DuplicateException | NotFoundException e) {
            result.rejectValue("unitName", "error.unitName", e.getMessage());
            return "unit/form";
        }
        redirectAttributes.addFlashAttribute(
                "successMessage",
                action + " đơn vị thành công!");
        return "redirect:/unit";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam("id") Long id,
                         RedirectAttributes redirectAttributes) {
        try {
            unitService.deleteById(id);
        }catch (NotFoundException | ResourceInUseException e){
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/unit";
        }
        redirectAttributes.addFlashAttribute("successMessage", "Xóa thành công");
        return "redirect:/unit";
    }

    @PostMapping("/api/create")
    @ResponseBody
    public org.springframework.http.ResponseEntity<?> createApi(@Valid @RequestBody UnitRequest unitRequest, BindingResult result) {
        if (result.hasErrors()) {
            return org.springframework.http.ResponseEntity.badRequest().body(result.getAllErrors());
        }
        try {
            UnitResponse response = unitService.create(unitRequest);
            return org.springframework.http.ResponseEntity.ok(response);
        } catch (DuplicateException e) {
            return org.springframework.http.ResponseEntity.badRequest().body(java.util.Collections.singletonMap("error", e.getMessage()));
        }
    }
}
