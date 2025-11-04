package com.veterinary.paw.mapper;

import com.veterinary.paw.domain.Veterinary;
import com.veterinary.paw.dto.VeterinaryResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VeterinaryMapper {

    public VeterinaryResponseDTO toResponseDTO(Veterinary veterinary) {
        if (veterinary == null) return null;

        return VeterinaryResponseDTO.builder()
                .id(veterinary.getId())
                .firstName(veterinary.getFirstName())
                .lastName(veterinary.getLastName())
                .birthDate(veterinary.getBirthDate())
                .speciality(veterinary.getSpeciality())
                .phoneNumber(veterinary.getPhoneNumber())
                .email(veterinary.getEmail())
                .dni(veterinary.getDni())
                .build();
    }
}
