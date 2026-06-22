package com.g4fpt.sms.product.controller;

import com.g4fpt.sms.branch.repository.BranchRepository;
import com.g4fpt.sms.product.entity.Inventory;
import com.g4fpt.sms.product.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryRepository inventoryRepository;
    private final BranchRepository branchRepository;

    @GetMapping
    public String list(Model model,
                       @RequestParam(required = false) Long branchId,
                       @RequestParam(defaultValue = "") String keyword,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Inventory> inventoryPage = inventoryRepository.findAllFiltered(branchId, keyword, pageable);

        model.addAttribute("inventoryPage", inventoryPage);
        model.addAttribute("branchList", branchRepository.findAll());
        model.addAttribute("selectedBranchId", branchId);
        model.addAttribute("keyword", keyword);
        model.addAttribute("size", size);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", inventoryPage.getTotalPages());

        return "inventory/list";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id,
                         @RequestParam Integer stock,
                         @RequestParam Integer minStock,
                         @RequestParam Integer maxStock,
                         @RequestParam String positionInShop) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid inventory Id:" + id));

        inventory.setStock(stock);
        inventory.setMinStock(minStock);
        inventory.setMaxStock(maxStock);
        inventory.setPositionInShop(positionInShop);

        inventoryRepository.save(inventory);
        return "redirect:/inventory";
    }
}
