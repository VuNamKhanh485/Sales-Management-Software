package com.g4fpt.sms.product.service.impl;

import com.g4fpt.sms.product.dto.BrandRequest;
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
    public Brand save(BrandRequest brandRequest) {
        Brand brand = new Brand();
        brand.setName(brandRequest.getBrandName());
        return brandRepository.save(brand);
    }

    @Override
    public List<Brand> findAll() {
        return brandRepository.findAll();
    }

    @Override
    public Brand findById(long id) {
        return brandRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteById(long id) {
        brandRepository.deleteById(id);
    }

    @Override
    public Brand update(BrandRequest brandRequest) {
        return null;
    }

}
