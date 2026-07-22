package com.g4fpt.sms.report.dto;

import com.g4fpt.sms.report.emuns.SnapshotType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InventoryReportFilterRequest {
    private LocalDate fromDate;
    private LocalDate toDate;
    private Long branchId;
    private Long categoryId;
    private Long brandId;
    private String keyword;
    private boolean groupByBranch;
    private SnapshotType snapshotType;
}
