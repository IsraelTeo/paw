package com.veterinary.paw.mapper;

import com.veterinary.paw.domain.VeterinaryService;
import com.veterinary.paw.dto.VeterinaryServiceResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VeterinaryServiceMapper {

    public VeterinaryServiceResponseDTO toResponseDTO(VeterinaryService veterinaryService) {
        if (veterinaryService == null) return null;

        return VeterinaryServiceResponseDTO.builder()
                .id(veterinaryService.getId())
                .name(veterinaryService.getName())
                .description(veterinaryService.getDescription())
                .price(veterinaryService.getPrice())
                .build();
    }
}
