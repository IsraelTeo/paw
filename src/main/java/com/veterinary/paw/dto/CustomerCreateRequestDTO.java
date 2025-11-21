package com.veterinary.paw.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CustomerCreateRequestDTO(

        @JsonProperty("first_name")
        @NotNull(message = "El nombre no puede ser nulo")
        @NotBlank(message = "El nombre no puede estar vacío")
        @Size(max = 100, message = "El nombre debe tener como máximo 100 caracteres")
        String firstName,

        @JsonProperty("last_name")
        @NotNull(message = "El apellido no puede ser nulo")
        @NotBlank(message = "El apellido no puede estar vacío")
        @Size(max = 100, message = "El apellido debe tener como máximo 100 caracteres")
        String lastName,

        @NotNull(message = "El DNI no puede ser nulo")
        @NotBlank(message = "El DNI no puede estar vacío")
        @Size(max = 20, message = "El DNI debe tener como máximo 20 caracteres")
        String dni,

        @JsonProperty("phone_number")
        @NotNull(message = "El número de teléfono no puede ser nulo")
        @NotBlank(message = "El número de teléfono no puede estar vacío")
        @Size(max = 20, message = "El número de teléfono debe tener como máximo 20 caracteres")
        String phoneNumber,

        @NotNull(message = "El correo electrónico no puede ser nulo")
        @NotBlank(message = "El correo electrónico no puede estar vacío")
        @Size(max = 100, message = "El correo electrónico debe tener como máximo 100 caracteres")
        @Email(message = "El correo electrónico debe ser una dirección válida")
        String email
) {
}
