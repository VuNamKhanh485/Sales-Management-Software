package com.g4fpt.sms.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransferResponse {
    private Long id;
    private String code;
    private Long fromBranchId;
    private String fromBranchName;
    private Long toBranchId;
    private String toBranchName;
    private String creatorName;
    private BigDecimal totalAmount;
    private String status; // PENDING_APPROVAL, COMPLETED, REJECTED
    private LocalDateTime createdAt;
    private String note;
}
