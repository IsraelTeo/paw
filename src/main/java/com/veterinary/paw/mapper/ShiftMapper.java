package com.veterinary.paw.mapper;

import com.veterinary.paw.domain.Shift;
import com.veterinary.paw.domain.Veterinary;
import com.veterinary.paw.dto.ShiftCreateRequestDTO;
import com.veterinary.paw.dto.ShiftResponseDTO;
import com.veterinary.paw.dto.VeterinaryResponseDTO;
import com.veterinary.paw.enums.ApiErrorEnum;
import com.veterinary.paw.exception.PawException;
import com.veterinary.paw.repository.VeterinaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ShiftMapper {

    private final VeterinaryRepository veterinaryRepository;

    private final VeterinaryMapper veterinaryMapper;

    public ShiftResponseDTO toResponseDTO(Shift shift) {
        if (shift == null) {
            return null;
        }

        VeterinaryResponseDTO veterinaryResponseDTO = Optional.ofNullable(shift.getVeterinary())
                .map(veterinaryMapper::toResponseDTO)
                .orElse(null);


        return ShiftResponseDTO.builder()
                .id(shift.getId())
                .date(shift.getDate())
                .startTime(shift.getStartTime())
                .endTime(shift.getEndTime())
                .available(shift.getAvailable())
                .veterinaryResponseDTO(veterinaryResponseDTO)
                .build();
    }

    public Shift toEntity(ShiftCreateRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        Veterinary veterinary = veterinaryRepository.findById(dto.veterinaryId())
                .orElseThrow(() -> new PawException(ApiErrorEnum.VETERINARY_NOT_FOUND));

        return Shift.builder()
                .date(dto.date())
                .startTime(dto.startTime())
                .endTime(dto.endTime())
                .veterinary(veterinary)
                .available(true)
                .build();
    }

    public void updateEntityFromDto(Shift shift, ShiftCreateRequestDTO dto) {
        if (shift != null && dto != null) {
            shift.setDate(dto.date());
            shift.setStartTime(dto.startTime());
            shift.setEndTime(dto.endTime());

            if (!shift.getVeterinary().getId().equals(dto.veterinaryId())) {
                Veterinary newVeterinary = veterinaryRepository.findById(dto.veterinaryId())
                        .orElseThrow(() -> new PawException(ApiErrorEnum.VETERINARY_NOT_FOUND));
                shift.setVeterinary(newVeterinary);
            }
        }
    }
}
