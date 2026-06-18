package com.g4fpt.sms.product.service.impl;

import com.g4fpt.sms.product.dto.request.BrandRequest;
import com.g4fpt.sms.product.dto.response.BrandResponse;
import com.g4fpt.sms.product.entity.Brand;
import com.g4fpt.sms.product.exception.DuplicateException;
import com.g4fpt.sms.product.exception.NotFoundException;
import com.g4fpt.sms.product.mapper.BrandMapper;
import com.g4fpt.sms.product.repository.BrandRepository;
import com.g4fpt.sms.product.service.BrandService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrandServiceImpl implements BrandService {
    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;

    public BrandServiceImpl(BrandRepository brandRepository, BrandMapper brandMapper) {
        this.brandMapper = brandMapper;
        this.brandRepository = brandRepository;
    }


    @Override
    public void create(BrandRequest brandRequest) {
        if(brandRepository.existsByNameIgnoreCase(brandRequest.getBrandName())){
            throw new DuplicateException("This name is already in use");
        }
        Brand brand = brandMapper.toEntity(brandRequest);
        brandRepository.save(brand);
    }

    @Override
    public Page<BrandResponse> findAll(String keyword, int size, int page, String sortField, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Brand> brandPage;
        if(keyword == null||keyword.isBlank()){
            brandPage = brandRepository.findAll(pageable);
        }else{
            brandPage = brandRepository.findByNameContainingIgnoreCase(keyword, pageable);
        }

        return brandPage.map(brandMapper::toResponse);
    }

    @Override
    public List<BrandResponse> findAll() {
        return brandRepository.findAll()
                .stream()
                .map(brandMapper::toResponse)
                .toList();
    }

    @Override
    public BrandResponse findById(long id) {
        return brandMapper.toResponse(getBrandById(id));
    }

    @Override
    public void deleteById(long id) {
        //cần có phần orderTranscation
        brandRepository.deleteById(id);
    }

    @Override
    public void update(long id, BrandRequest brandRequest) {
        Brand brand = getBrandById(id);
        if(brandRepository.existsByNameIgnoreCaseAndIdNot(brandRequest.getBrandName(), id)){
            throw new DuplicateException("This name is already in use");
        }
        brand.setName(brandRequest.getBrandName());
        brand.setStatus(brandRequest.getBrandStatus());
        brandRepository.save(brand);
    }

    private Brand getBrandById(long id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Brand not found"));
    }

}
