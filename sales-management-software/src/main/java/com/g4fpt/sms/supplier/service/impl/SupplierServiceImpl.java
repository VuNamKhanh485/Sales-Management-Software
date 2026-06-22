package com.g4fpt.sms.supplier.service.impl;

import com.g4fpt.sms.common.exception.DuplicateException;
import com.g4fpt.sms.common.exception.NotFoundException;
import com.g4fpt.sms.supplier.dto.request.SupplierRequest;
import com.g4fpt.sms.supplier.dto.response.SupplierResponse;
import com.g4fpt.sms.supplier.entity.Supplier;
import com.g4fpt.sms.supplier.mapper.SupplierMapper;
import com.g4fpt.sms.supplier.repository.SupplierRepository;
import com.g4fpt.sms.supplier.service.SupplierService;
import lombok.AllArgsConstructor;
import org.hibernate.query.SortDirection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@AllArgsConstructor
public class SupplierServiceImpl implements SupplierService {
    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;

    @Override
    public void create(SupplierRequest supplierRequest) {
        if(supplierRepository.existsByCodeIgnoreCase(supplierRequest.getCode())){
            throw new DuplicateException("This code is already in use");
        }
        Supplier supplier = supplierMapper.toEntity(supplierRequest);
        supplierRepository.save(supplier);
    }

    @Override
    public void update(SupplierRequest supplierRequest, Long id) {
        if(supplierRepository.existsByCodeIgnoreCaseAndIdNot(supplierRequest.getCode(), id)){
            throw new DuplicateException("This code is already in use");
        }
        Supplier supplier = supplierMapper.toEntity(supplierRequest);
        supplierRepository.save(supplier);
    }

    @Override
    public void delete(Long id) {
        Supplier supplier = getSupplierById(id);
        //check transaction
        supplierRepository.delete(supplier);
    }

    @Override
    public Page<SupplierResponse> findAll(String keyword, int page, int size, String sortField, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Supplier> supplierPage;

        if(keyword != null){
            supplierPage = supplierRepository.findByNameContainingIgnoreCase(keyword, pageable);
        }else {
            supplierPage = supplierRepository.findAll(pageable);
        }
        return supplierPage.map(supplierMapper::toResponse);
    }

    @Override
    public SupplierResponse findById(long id) {
        return supplierMapper.toResponse(getSupplierById(id));
    }

    @Override
    public List<SupplierResponse> findAll() {
        return supplierRepository.findAll()
                .stream()
                .map(supplierMapper::toResponse)
                .toList();
    }

    private Supplier getSupplierById(Long id){
        return supplierRepository.findById(id).orElseThrow(() -> new NotFoundException("Supplier not found"));
    }
}
