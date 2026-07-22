package com.g4fpt.sms.report.projection;

import java.math.BigDecimal;

public interface InventoryMovementProjection {
    Long getBranchId();
    Long getProductUnitId();
    Integer getQty();
    BigDecimal getValue();
}
