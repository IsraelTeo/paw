package com.veterinary.paw.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.veterinary.paw.enums.PetGenderEnum;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record PetCreateRequestDTO(
        @JsonProperty("first_name")
        @NotBlank(message = "First name cannot be blank")
        @Size(max = 100, message = "First name cannot exceed 100 characters")
        String firstName,

        @JsonProperty("last_name")
        @NotBlank(message = "Last name cannot be blank")
        @Size(max = 100, message = "Last name cannot exceed 100 characters")
        String lastName,

        @NotNull(message = "Gender is required")
        PetGenderEnum gender,

        @NotBlank(message = "Specie cannot be blank")
        @Size(max = 50, message = "Specie cannot exceed 50 characters")
        String specie,

        @JsonProperty("last_name")
        @NotNull(message = "Birth date is required")
        @Past(message = "Birth date must be in the past")
        LocalDate birthDate,

        @JsonProperty("owner_id")
        @NotNull(message = "Owner ID is required")
        @Positive(message = "Owner ID must be positive")
        Long ownerId
) {
}
