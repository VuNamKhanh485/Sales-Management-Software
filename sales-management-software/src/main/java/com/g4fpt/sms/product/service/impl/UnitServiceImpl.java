package com.g4fpt.sms.product.service.impl;

import com.g4fpt.sms.common.exception.ResourceInUseException;
import com.g4fpt.sms.product.dto.request.UnitRequest;
import com.g4fpt.sms.product.dto.response.UnitResponse;
import com.g4fpt.sms.product.entity.Unit;
import com.g4fpt.sms.common.exception.DuplicateException;
import com.g4fpt.sms.common.exception.NotFoundException;
import com.g4fpt.sms.product.mapper.UnitMapper;
import com.g4fpt.sms.product.repository.UnitRepository;
import com.g4fpt.sms.product.service.UnitService;
import com.g4fpt.sms.product.util.NormalizeWord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UnitServiceImpl implements UnitService {

    private final UnitRepository unitRepository;
    private final UnitMapper unitMapper;

    public UnitServiceImpl(UnitRepository unitRepository, UnitMapper unitMapper) {
        this.unitRepository = unitRepository;
        this.unitMapper = unitMapper;
    }

    @Override
    public Page<UnitResponse> findAll(String keyword, int page, int size, String sortField, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Unit> unitPage;
        if(keyword == null||keyword.isBlank()){
            unitPage = unitRepository.findAll(pageable);
        }else{
            unitPage = unitRepository.findByNameContainingIgnoreCase(NormalizeWord.normalize(keyword), pageable);
        }
        return unitPage.map(unitMapper::toResponse);
    }

    @Override
    public List<UnitResponse> findAll() {
        return unitRepository.findAll()
                .stream()
                .map(unitMapper::toResponse)
                .toList();
    }

    @Override
    public UnitResponse create(UnitRequest unitRequest) {
        if(unitRepository.existsByNameIgnoreCase(unitRequest.getName())){
            throw new DuplicateException("This name is already in use");
        }
        Unit unit = unitMapper.toEntity(unitRequest);
        Unit savedUnit = unitRepository.save(unit);
        return unitMapper.toResponse(savedUnit);
    }

    @Override
    public void update(Long id, UnitRequest unitRequest) {
        if(unitRepository.existsByNameIgnoreCaseAndIdNot(unitRequest.getName(),id)){
            throw new DuplicateException("This name is already in use");
        }
        Unit unit = getUnitById(id);
        unit.setName(unitRequest.getName());
        unitRepository.save(unit);
    }

    @Override
    public void deleteById(Long id) {
        Unit unit = getUnitById(id);

        if(unitRepository.existInOrderTransaction(id)){
            throw new ResourceInUseException("Đơn vị đã tồn tại trong giao dịch");
        }

        unitRepository.delete(unit);
    }

    @Override
    public UnitResponse findById(Long id) {
        return unitMapper.toResponse(getUnitById(id));
    }

    private Unit getUnitById(Long id){
        return unitRepository.findById(id).orElseThrow(() -> new NotFoundException("unit not found"));
    }
}
