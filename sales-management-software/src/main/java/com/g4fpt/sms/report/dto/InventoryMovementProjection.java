package com.g4fpt.sms.report.dto;

import java.math.BigDecimal;

public interface InventoryMovementProjection {
    Long getBranchId();
    Long getProductUnitId();
    Integer getQty();
    BigDecimal getValue();
}
