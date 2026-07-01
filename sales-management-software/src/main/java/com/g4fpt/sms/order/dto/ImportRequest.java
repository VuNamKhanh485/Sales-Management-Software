package com.g4fpt.sms.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImportRequest {
    private Long branchId;
    private Long supplierId;
    private String note;
    private List<ImportItemRequest> items = new ArrayList<>();
}
