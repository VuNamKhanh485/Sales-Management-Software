package com.g4fpt.sms.report.projection;

import java.math.BigDecimal;

// Dùng để lấy giá nhập gần nhất (đơn giá) của 1 SKU tại 1 thời điểm, từ OrderTransactionDetail
public interface LastImportPriceProjection {
    Long getProductUnitId();
    BigDecimal getImportPrice();
}
