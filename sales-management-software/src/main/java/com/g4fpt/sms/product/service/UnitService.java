package com.g4fpt.sms.product.service;



import com.g4fpt.sms.product.dto.request.UnitRequest;
import com.g4fpt.sms.product.dto.response.UnitResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UnitService {
    UnitResponse create(UnitRequest unitRequest);
    void update(Long id, UnitRequest unitRequest);
    void deleteById(Long id);
    Page<UnitResponse> findAll(String keyword, int page, int size, String sortField, String sortDirection);
    UnitResponse findById(Long id);
    List<UnitResponse> findAll();
}
