package com.g4fpt.sms.order.mapper;

import com.g4fpt.sms.order.dto.ImportDetailResponse;
import com.g4fpt.sms.order.dto.ImportResponse;
import com.g4fpt.sms.order.entity.OrderTransaction;
import com.g4fpt.sms.order.entity.OrderTransactionDetail;
import org.springframework.stereotype.Component;

@Component
public class ImportMapper {

    public ImportResponse toResponse(OrderTransaction tx, String branchName, String supplierName, String creatorName) {
        if (tx == null) {
            return null;
        }
        ImportResponse res = new ImportResponse();
        res.setId(tx.getId());
        res.setCode(tx.getCode());
        res.setBranchName(branchName);
        res.setSupplierName(supplierName);
        res.setCreatorName(creatorName);
        res.setStatus(tx.getStatus());
        res.setTotalAmount(tx.getFinalAmount()); // finalAmount or totalAmount can be used
        res.setCreatedAt(tx.getCreatedAt());
        res.setNote(tx.getNote());
        return res;
    }

    public ImportDetailResponse toDetailResponse(OrderTransactionDetail detail) {
        if (detail == null) {
            return null;
        }
        ImportDetailResponse res = new ImportDetailResponse();
        res.setProductName(detail.getProductUnit().getProduct().getName());
        res.setUnitName(detail.getProductUnit().getUnit().getName());
        res.setSku(detail.getProductUnit().getSku());
        res.setQuantity(detail.getQuantity());
        res.setImportPrice(detail.getImportPrice());
        res.setTotalAmount(detail.getTotalAmount());
        return res;
    }
}
