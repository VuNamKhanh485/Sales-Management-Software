package com.g4fpt.sms.report.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryReportDTO {
    private Long branchId;
    private String branchName;
    private Long productUnitId;
    private String sku;
    private String productName;
    private String unitName;
    private String categoryName;

    private Integer openingStock;
    private BigDecimal openingValue;
    private Integer stockIn;
    private BigDecimal stockInValue;
    private Integer stockOut;
    private BigDecimal stockOutValue;
    private Integer closingStock;
    private BigDecimal closingValue;
}
