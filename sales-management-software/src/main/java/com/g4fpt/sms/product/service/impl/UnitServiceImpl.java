package com.g4fpt.sms.product.service.impl;

import com.g4fpt.sms.product.dto.request.UnitRequest;
import com.g4fpt.sms.product.dto.response.UnitResponse;
import com.g4fpt.sms.product.entity.Unit;
import com.g4fpt.sms.product.exception.DuplicateException;
import com.g4fpt.sms.product.exception.NotFoundException;
import com.g4fpt.sms.product.mapper.UnitMapper;
import com.g4fpt.sms.product.repository.UnitRepository;
import com.g4fpt.sms.product.service.UnitService;
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
    public List<UnitResponse> findAll() {
        return unitRepository.findAll()
                .stream()
                .map(unitMapper::toResponse)
                .toList();
    }

    @Override
    public void create(UnitRequest unitRequest) {
        if(unitRepository.existsByName(unitRequest.getName())){
            throw new DuplicateException("This name is already in use");
        }
        Unit unit = unitMapper.toEntity(unitRequest);
        unitRepository.save(unit);
    }

    @Override
    public void update(Long id, UnitRequest unitRequest) {
        Unit unit = getUnitById(id);
        unit.setName(unitRequest.getName());
        unitRepository.save(unit);
    }

    @Override
    public void deleteById(Long id) {
        //cần có phần orderTranscation
    }

    @Override
    public UnitResponse findById(Long id) {
        return unitMapper.toResponse(getUnitById(id));
    }

    private Unit getUnitById(Long id){
        return unitRepository.findById(id).orElseThrow(() -> new NotFoundException("unit not found"));
    }
}
