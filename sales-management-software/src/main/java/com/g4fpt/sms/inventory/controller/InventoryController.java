package com.g4fpt.sms.inventory.controller;

import com.g4fpt.sms.inventory.dto.response.InventoryResponse;
import com.g4fpt.sms.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    // LIST
    @GetMapping
    public String list(Model model) {

        List<InventoryResponse> inventories =
                inventoryService.getAll();

        model.addAttribute("inventories", inventories);
        model.addAttribute("page", "inventory");

        return "inventory/list";
    }

    // DETAIL
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id,
                         Model model) {

        InventoryResponse inventory =
                inventoryService.getById(id);

        model.addAttribute("inventory", inventory);
        model.addAttribute("page", "inventory");

        return "inventory/detail";
    }
}