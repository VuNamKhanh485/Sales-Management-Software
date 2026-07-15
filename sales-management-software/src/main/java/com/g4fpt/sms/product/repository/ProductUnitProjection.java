package com.g4fpt.sms.product.repository;

import java.math.BigDecimal;

public interface ProductUnitProjection {
    Long getId();
    String getSku();
    BigDecimal getPrice();
    ProductInfo getProduct();
    UnitInfo getUnit();

    interface ProductInfo {
        String getName();
        String getImageUrl();
    }

    interface UnitInfo {
        String getName();
    }
}
