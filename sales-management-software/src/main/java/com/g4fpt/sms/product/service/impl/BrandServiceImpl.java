package com.g4fpt.sms.product.service.impl;

import com.g4fpt.sms.product.dto.request.BrandRequest;
import com.g4fpt.sms.product.dto.response.BrandResponse;
import com.g4fpt.sms.product.entity.Brand;
import com.g4fpt.sms.product.repository.BrandRepository;
import com.g4fpt.sms.product.service.BrandService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrandServiceImpl implements BrandService {
    private final BrandRepository brandRepository;

    public BrandServiceImpl(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    @Override
    public void create(BrandRequest brandRequest) {
        Brand brand = new Brand();
        brand.setName(brandRequest.getBrandName());
    }

    @Override
    public List<BrandResponse> findAll() {
        return brandRepository.findAll();
    }

    @Override
    public BrandResponse findById(long id) {
        return brandRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteById(long id) {
        brandRepository.deleteById(id);
    }

    @Override
    public void update(long id, BrandRequest brandRequest) {
        BrandResponse brand = findById(id);
        if (brand != null) {
            brand.setName(brandRequest.getBrandName());
        }
    }

}
