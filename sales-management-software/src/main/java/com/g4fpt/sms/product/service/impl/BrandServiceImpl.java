package com.g4fpt.sms.product.service.impl;

import com.g4fpt.sms.common.exception.ResourceInUseException;
import com.g4fpt.sms.product.dto.request.BrandRequest;
import com.g4fpt.sms.product.dto.response.BrandResponse;
import com.g4fpt.sms.product.entity.Brand;
import com.g4fpt.sms.common.exception.DuplicateException;
import com.g4fpt.sms.common.exception.NotFoundException;
import com.g4fpt.sms.product.enums.BrandStatus;
import com.g4fpt.sms.product.mapper.BrandMapper;
import com.g4fpt.sms.product.repository.BrandRepository;
import com.g4fpt.sms.product.service.BrandService;
import com.g4fpt.sms.product.util.NormalizeWord;
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
    public BrandResponse create(BrandRequest brandRequest) {
        if(brandRepository.existsByNameIgnoreCase(brandRequest.getBrandName())){
            throw new DuplicateException("Tên đã được dùng");
        }
        Brand brand = brandMapper.toEntity(new Brand(),brandRequest);
        Brand savedBrand = brandRepository.save(brand);
        return brandMapper.toResponse(savedBrand);
    }

    @Override
    public Page<BrandResponse> findAll(String keyword, int page, int size, String sortField, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Brand> brandPage;
        if(keyword == null||keyword.isBlank()){
            brandPage = brandRepository.findAll(pageable);
        }else{
            brandPage = brandRepository.findByNameContainingIgnoreCase(NormalizeWord.normalize(keyword), pageable);
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
    public List<BrandResponse> findAllActive() {
        return brandRepository.findByStatus(BrandStatus.ACTIVE)
                .stream()
                .map(brandMapper::toResponse).
                toList();
    }

    @Override
    public BrandResponse findById(long id) {
        return brandMapper.toResponse(getBrandById(id));
    }

    @Override
    public void deleteById(long id) {
        Brand brand = getBrandById(id);

        if(brandRepository.existInOrderTransaction(id)){
            throw new ResourceInUseException("Nhãn hàng đã tồn tại trong giao dịch");
        }

        brandRepository.delete(brand);
    }

    @Override
    public void update(long id, BrandRequest brandRequest) {
        Brand brand = getBrandById(id);
        if(brandRepository.existsByNameIgnoreCaseAndIdNot(brandRequest.getBrandName(), id)){
            throw new DuplicateException("Tên đã được dùng");
        }
        brandMapper.toEntity(brand,brandRequest);
        brandRepository.save(brand);
    }

    private Brand getBrandById(long id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không thấy nhãn hàng"));
    }

}
