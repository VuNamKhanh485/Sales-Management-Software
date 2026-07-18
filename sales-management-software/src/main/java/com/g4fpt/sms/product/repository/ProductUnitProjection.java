package com.g4fpt.sms.product.repository;

import java.math.BigDecimal;

public interface ProductUnitProjection {
    Long getId();
    String getSku();
    BigDecimal getPrice();
    String getProductName();
    String getImageName();
    String getUnitName();
}
