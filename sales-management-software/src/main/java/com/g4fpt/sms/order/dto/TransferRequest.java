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
public class TransferRequest {
    private Long id;
    private Long fromBranchId;
    private Long toBranchId; // Mặc định là branch của người đăng nhập
    private String note;
    private List<TransferItemRequest> items = new ArrayList<>();
}
