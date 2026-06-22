package com.g4fpt.sms.inventory.service.impl;

import com.g4fpt.sms.branch.entity.Branch;
import com.g4fpt.sms.branch.repository.BranchRepository;
import com.g4fpt.sms.inventory.dto.request.InventoryRequest;
import com.g4fpt.sms.inventory.dto.response.InventoryResponse;
import com.g4fpt.sms.inventory.entity.Inventory;
import com.g4fpt.sms.inventory.mapper.InventoryMapper;
import com.g4fpt.sms.inventory.repository.InventoryRepository;
import com.g4fpt.sms.inventory.service.InventoryService;
import com.g4fpt.sms.product.entity.ProductUnit;
import com.g4fpt.sms.product.repository.ProductUnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final BranchRepository branchRepository;
    private final ProductUnitRepository productUnitRepository;
    private final InventoryMapper inventoryMapper;

    @Override
    public List<InventoryResponse> getAll() {
        return inventoryRepository.findAll()
                .stream()
                .map(inventoryMapper::toResponse)
                .toList();
    }

    @Override
    public InventoryResponse getById(Long id) {
        Inventory inventory = inventoryRepository
                .findById(id)
                .orElseThrow();

        return inventoryMapper.toResponse(inventory);
    }

    @Override
    public InventoryResponse create(InventoryRequest request) {

        if (inventoryRepository.existsByBranchIdAndProductUnitId(
                request.getBranchId(),
                request.getProductUnitId()
        )) {
            throw new RuntimeException(
                    "Sản phẩm này đã tồn tại trong kho của chi nhánh"
            );
        }

        if (request.getMaxStock() != null
                && request.getMaxStock() < request.getMinStock()) {
            throw new RuntimeException(
                    "Tồn kho tối đa phải lớn hơn hoặc bằng tồn kho tối thiểu"
            );
        }

        Branch branch = branchRepository
                .findById(request.getBranchId())
                .orElseThrow();

        ProductUnit productUnit = productUnitRepository
                .findById(request.getProductUnitId())
                .orElseThrow();

        Inventory inventory = inventoryMapper.toEntity(
                request,
                branch,
                productUnit
        );

        inventoryRepository.save(inventory);

        return inventoryMapper.toResponse(inventory);
    }

    @Override
    public InventoryResponse update(
            Long id,
            InventoryRequest request
    ) {
        Inventory inventory = inventoryRepository
                .findById(id)
                .orElseThrow();

        if (request.getMaxStock() != null
                && request.getMaxStock() < request.getMinStock()) {
            throw new RuntimeException(
                    "Tồn kho tối đa phải lớn hơn hoặc bằng tồn kho tối thiểu"
            );
        }

        inventoryMapper.updateEntity(inventory, request);

        inventoryRepository.save(inventory);

        return inventoryMapper.toResponse(inventory);
    }

    @Override
    public void delete(Long id) {
        inventoryRepository.deleteById(id);
    }
}