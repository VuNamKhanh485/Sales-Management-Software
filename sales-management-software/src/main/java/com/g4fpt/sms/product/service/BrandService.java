package com.g4fpt.sms.product.service;

import com.g4fpt.sms.product.dto.request.BrandRequest;
import com.g4fpt.sms.product.dto.response.BrandResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BrandService {
    void create(BrandRequest brandRequest);
    void deleteById(long id);
    void update(long id, BrandRequest brandRequest);
    Page<BrandResponse> findAll(String keyword, int page, int size, String sortField, String sortDirection);
    BrandResponse findById(long id);
    List<BrandResponse> findAll();
}
