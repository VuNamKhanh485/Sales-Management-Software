package com.g4fpt.sms.supplier.mapper;

import com.g4fpt.sms.supplier.dto.request.SupplierRequest;
import com.g4fpt.sms.supplier.dto.response.SupplierResponse;
import com.g4fpt.sms.supplier.entity.Supplier;
import org.springframework.stereotype.Component;

@Component
public class SupplierMapper {

    public Supplier toEntity(Supplier supplier, SupplierRequest supplierRequest) {
        if (supplierRequest == null) {
            return null;
        }
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

    public SupplierRequest toRequest(SupplierResponse supplierResponse) {
        if (supplierResponse == null) {
            return null;
        }
        SupplierRequest supplierRequest = new SupplierRequest();

        supplierRequest.setCode(supplierResponse.getCode());
        supplierRequest.setName(supplierResponse.getName());
        supplierRequest.setPhone(supplierResponse.getPhone());
        supplierRequest.setEmail(supplierResponse.getEmail());
        supplierRequest.setAddress(supplierResponse.getAddress());
        supplierRequest.setStatus(supplierResponse.getStatus());
        supplierRequest.setNote(supplierResponse.getNote());

        return supplierRequest;
    }
}
