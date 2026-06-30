package com.g4fpt.sms.inventory.dto;

import com.g4fpt.sms.branch.entity.BranchStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryBranchSummaryResponse {
    private Long branchId;
    private String branchCode;
    private String branchName;
    private BranchStatus status;
    private Long totalProductCount;
}
