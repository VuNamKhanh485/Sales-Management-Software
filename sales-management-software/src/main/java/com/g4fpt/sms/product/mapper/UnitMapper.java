package com.g4fpt.sms.product.mapper;

import com.g4fpt.sms.product.dto.request.UnitRequest;
import com.g4fpt.sms.product.dto.response.UnitResponse;
import com.g4fpt.sms.product.entity.Unit;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
public class UnitMapper {

    public Unit toEntity(UnitRequest request) {
        if (request == null) {
            return null;
        }

        Unit unit = new Unit();

        unit.setName(request.getName().trim());

        return unit;
    }

    public UnitResponse toResponse(Unit unit) {
        if (unit == null) {
            return null;
        }

        UnitResponse response = new UnitResponse();

        response.setId(unit.getId());
        response.setName(unit.getName());

        return response;
    }
}
