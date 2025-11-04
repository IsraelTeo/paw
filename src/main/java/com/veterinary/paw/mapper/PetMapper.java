package com.veterinary.paw.mapper;

import com.veterinary.paw.domain.Pet;
import com.veterinary.paw.dto.PetResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class PetMapper {

    public PetResponseDTO toResponseDTO(Pet pet) {
        return PetResponseDTO.builder()
                .id(pet.getId())
                .firstName(pet.getFirstName())
                .lastName(pet.getLastName())
                .age(pet.getAge())
                .gender(pet.getGender())
                .specie(pet.getSpecie())
                .birthDate(pet.getBirthDate())
                .build();
    }
}
