package com.g4fpt.sms.product.service;


import com.g4fpt.sms.product.dto.UnitRequest;
import com.g4fpt.sms.product.entity.Unit;

import java.util.List;

public interface UnitService {
    public List<Unit> findAll();
    public Unit create(UnitRequest unitRequest);
    public Unit update(Long id, UnitRequest unitRequest);
    public void delete(Long id);

    Unit findById(Long id);
}
