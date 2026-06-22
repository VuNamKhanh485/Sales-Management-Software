package com.g4fpt.sms.inventory.service;

import com.g4fpt.sms.inventory.dto.request.InventoryRequest;
import com.g4fpt.sms.inventory.dto.response.InventoryResponse;

import java.util.List;

public interface InventoryService {

    List<InventoryResponse> getAll();

    InventoryResponse getById(Long id);

    InventoryResponse create(InventoryRequest request);

    InventoryResponse update(Long id, InventoryRequest request);

    void delete(Long id);
}