package com.g4fpt.sms.product.service;

import com.g4fpt.sms.product.dto.BrandRequest;
import com.g4fpt.sms.product.entity.Brand;

import java.util.List;


public interface BrandService {
    Brand save(BrandRequest brandRequest);
    List<Brand> findAll();
    Brand findById(long id);
    void deleteById(long id);
    Brand update(long id, BrandRequest brandRequest);


}
