package com.veterinary.paw.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UserCreateRequestDTO (
        @NotBlank(message = "El email no puede estar vacío")
        @Size(max = 255, message = "El email debe tener como máximo 255 caracteres")
        @Email(message = "El formato del email es incorrecto")
        String email,

        @NotBlank(message = "La contraseña no puede estar vacía")
        @Size(min = 8, max = 100, message = "La contraseña debe tener entre 8 y 100 caracteres")
        String password
) {
}
