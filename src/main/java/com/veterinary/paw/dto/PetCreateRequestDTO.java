package com.veterinary.paw.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.veterinary.paw.enums.PetGenderEnum;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record PetCreateRequestDTO(
        @JsonProperty("first_name")
        @NotBlank(message = "El nombre no puede estar vacío")
        @Size(max = 100, message = "El nombre debe tener como máximo 100 caracteres")
        String firstName,

        @JsonProperty("last_name")
        @NotBlank(message = "El apellido no puede estar vacío")
        @Size(max = 100, message = "El apellido debe tener como máximo 100 caracteres")
        String lastName,

        @NotNull(message = "El género es obligatorio")
        PetGenderEnum gender,

        @NotBlank(message = "La especie no puede estar vacía")
        @Size(max = 50, message = "La especie debe tener como máximo 50 caracteres")
        String specie,

        @JsonProperty("birth_date")
        @NotNull(message = "La fecha de nacimiento es obligatoria")
        @Past(message = "La fecha de nacimiento debe ser una fecha pasada")
        LocalDate birthDate,

        @JsonProperty("owner_id")
        @NotNull(message = "El ID del dueño es obligatorio")
        @Positive(message = "El ID del dueño debe ser un valor positivo")
        Long ownerId
) {
}
