package com.veterinary.paw.mapper;

import com.veterinary.paw.domain.Shift;
import com.veterinary.paw.dto.ShiftResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShiftMapper {

    private final VeterinaryMapper veterinaryMapper;

    public ShiftResponseDTO toResponseDTO(Shift shift) {
        if (shift == null) return null;

        return ShiftResponseDTO.builder()
                .id(shift.getId())
                .date(shift.getDate())
                .startTime(shift.getStartTime())
                .endTime(shift.getEndTime())
                .available(shift.getAvailable())
                .veterinaryResponseDTO(veterinaryMapper.toResponseDTO(shift.getVeterinary()))
                .build();
    }
}
