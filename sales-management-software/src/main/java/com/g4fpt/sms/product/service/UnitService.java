package com.g4fpt.sms.product.service;


import com.g4fpt.sms.product.dto.request.BrandRequest;
import com.g4fpt.sms.product.dto.request.UnitRequest;
import com.g4fpt.sms.product.dto.response.UnitResponse;
import com.g4fpt.sms.product.entity.Unit;
import com.g4fpt.sms.product.util.ValidationError;

import java.util.List;

public interface UnitService {
    public void create(UnitRequest unitRequest);

    public void update(Long id, UnitRequest unitRequest);

    public void deleteById(Long id);

    public List<UnitResponse> findAll();

    UnitResponse findById(Long id);
}
