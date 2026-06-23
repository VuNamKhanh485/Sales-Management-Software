package com.g4fpt.sms.supplier.mapper;

import com.g4fpt.sms.supplier.dto.request.SupplierRequest;
import com.g4fpt.sms.supplier.dto.response.SupplierResponse;
import com.g4fpt.sms.supplier.entity.Supplier;
import org.springframework.stereotype.Component;

@Component
public class SupplierMapper {

    public Supplier toEntity(SupplierRequest supplierRequest) {
        if (supplierRequest == null) {
            return null;
        }
        Supplier supplier = new Supplier();
        supplier.setCode(supplierRequest.getCode());
        supplier.setName(supplierRequest.getName());
        supplier.setPhone(supplierRequest.getPhone());
        supplier.setEmail(supplierRequest.getEmail());
        supplier.setAddress(supplierRequest.getAddress());
        supplier.setStatus(supplierRequest.getStatus());
        supplier.setNote(supplierRequest.getNote());

        return supplier;
    }

    public SupplierResponse toResponse(Supplier supplier) {
        if (supplier == null) {
            return null;
        }

        SupplierResponse supplierResponse = new SupplierResponse();
        supplierResponse.setId(supplier.getId());
        supplierResponse.setCode(supplier.getCode());
        supplierResponse.setName(supplier.getName());
        supplierResponse.setPhone(supplier.getPhone());
        supplierResponse.setEmail(supplier.getEmail());
        supplierResponse.setAddress(supplier.getAddress());
        supplierResponse.setStatus(supplier.getStatus());
        supplierResponse.setNote(supplier.getNote());

        return supplierResponse;
    }
}
