package com.veterinary.paw.mapper;

import com.veterinary.paw.domain.Veterinary;
import com.veterinary.paw.dto.VeterinaryCreateRequestDTO;
import com.veterinary.paw.dto.VeterinaryResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VeterinaryMapper {

    public Veterinary toEntity(VeterinaryCreateRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        return Veterinary.builder()
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .birthDate(dto.birthDate())
                .speciality(dto.speciality())
                .phoneNumber(dto.phoneNumber())
                .email(dto.email())
                .dni(dto.dni())
                .build();
    }

    public VeterinaryResponseDTO toResponseDTO(Veterinary veterinary) {
        if (veterinary == null) {
            return null;
        }

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

    public void updateEntityFromDTO(Veterinary veterinary, VeterinaryCreateRequestDTO dto) {
        if (veterinary != null && dto != null) {
            veterinary.setFirstName(dto.firstName());
            veterinary.setLastName(dto.lastName());
            veterinary.setBirthDate(dto.birthDate());
            veterinary.setSpeciality(dto.speciality());
            veterinary.setPhoneNumber(dto.phoneNumber());
            veterinary.setEmail(dto.email());
            veterinary.setDni(dto.dni());
        }
    }
}
