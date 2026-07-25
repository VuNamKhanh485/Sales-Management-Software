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

    // Period selector: "1"-"12" months, "Q1"-"Q4" quarters, "H1"/"H2" half-year, "" all
    private String reportPeriod;
    private Integer year;

    // Pagination
    private Integer page = 1;
    private Integer pageSize = 10;
}
