package com.g4fpt.sms.product.service.impl;

import com.g4fpt.sms.product.dto.UnitRequest;
import com.g4fpt.sms.product.entity.Unit;
import com.g4fpt.sms.product.repository.UnitRepository;
import com.g4fpt.sms.product.service.UnitService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UnitServiceImpl implements UnitService {

    private final UnitRepository unitRepository;

    public UnitServiceImpl(UnitRepository unitRepository) {
        this.unitRepository = unitRepository;
    }

    @Override
    public List<Unit> findAll() {
        return unitRepository.findAll();
    }

    @Override
    public Unit create(UnitRequest unitRequest) {
        Unit unit = new Unit();
        unit.setName(unitRequest.getName());

        unit.setCreatedAt(LocalDateTime.now());
        return unitRepository.save(unit);
    }

    @Override
    public Unit update(Long id, UnitRequest unitRequest) {
        Unit unit = findById(id);
        unit.setName(unitRequest.getName());

        unit.setUpdatedAt(LocalDateTime.now());
        return unitRepository.save(unit);
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public Unit findById(Long id) {
        return unitRepository.findById(id).orElseThrow(() -> new RuntimeException("unit not found"));
    }
}
