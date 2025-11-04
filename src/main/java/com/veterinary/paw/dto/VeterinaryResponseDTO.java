package com.veterinary.paw.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record VeterinaryResponseDTO(
        Long id,

        @JsonProperty("first_name")
        String firstName,

        @JsonProperty("last_name")
        String lastName,

        @JsonProperty("birth_date")
        LocalDate birthDate,

        String speciality,

        @JsonProperty("phone_number")
        String phoneNumber,

        String email,

        String dni
) {
}
