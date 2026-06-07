package com.g4fpt.sms.product.service;

import com.g4fpt.sms.product.dto.request.BrandRequest;
import com.g4fpt.sms.product.dto.response.BrandResponse;


import java.util.List;


public interface BrandService {
    void create(BrandRequest brandRequest);
    void deleteById(long id);
    void update(long id, BrandRequest brandRequest);
    List<BrandResponse> findAll();
    BrandResponse findById(long id);


}
