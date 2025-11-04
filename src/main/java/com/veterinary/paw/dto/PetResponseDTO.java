package com.veterinary.paw.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.veterinary.paw.enums.PetGenderEnum;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record PetResponseDTO(
        Long id,

        @JsonProperty("first_name")
        String firstName,

        @JsonProperty("last_name")
        String lastName,

        Integer age,

        PetGenderEnum gender,

        String specie,

        @JsonProperty("last_name")
        LocalDate birthDate,

        CustomerResponseDTO owner
) {
}
