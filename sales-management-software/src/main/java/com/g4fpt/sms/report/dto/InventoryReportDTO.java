package com.g4fpt.sms.report.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InventoryReportDTO {
    private Long branchId;
    private String branchName;
    private Long productUnitId;
    private String sku;
    private String productName;
    private String unitName;
    private String categoryName;

    private Integer tonDauKy;
    private BigDecimal giaTriDauKy;
    private Integer nhapTrongKy;
    private BigDecimal giaTriNhap;
    private Integer xuatTrongKy;
    private BigDecimal giaTriXuat;
    private Integer tonCuoiKy;
    private BigDecimal giaTriCuoiKy;
}
