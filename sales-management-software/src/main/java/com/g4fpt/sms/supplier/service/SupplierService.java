package com.g4fpt.sms.supplier.service;

import com.g4fpt.sms.supplier.dto.request.SupplierRequest;
import com.g4fpt.sms.supplier.dto.response.SupplierResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface SupplierService {
    void create(SupplierRequest supplier);
    void update(SupplierRequest supplier, Long id);
    void deleteById(Long id);
    Page<SupplierResponse> findAll(String keyword, int page, int size, String sortField, String sortDirection);
    SupplierResponse findById(long id);
    List<SupplierResponse> findAll();
}
