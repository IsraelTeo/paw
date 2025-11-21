package com.veterinary.paw.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record VeterinaryCreateRequestDTO(

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres")
        @JsonProperty("first_name")
        String firstName,

        @NotBlank(message = "El apellido es obligatorio")
        @Size(max = 100, message = "El apellido no puede exceder los 100 caracteres")
        @JsonProperty("last_name")
        String lastName,

        @NotNull(message = "La fecha de nacimiento es obligatoria")
        @Past(message = "La fecha de nacimiento debe ser en el pasado")
        @JsonProperty("birth_date")
        LocalDate birthDate,

        @NotBlank(message = "La especialidad es obligatoria")
        @Size(max = 100, message = "La especialidad no puede exceder los 100 caracteres")
        String speciality,

        @NotBlank(message = "El número de teléfono es obligatorio")
        @Size(max = 20, message = "El número de teléfono no puede exceder los 20 caracteres")
        @Pattern(regexp = "^\\+?[0-9\\s()-]*$", message = "Formato de número de teléfono inválido")
        @JsonProperty("phone_number")
        String phoneNumber,

        @NotBlank(message = "El correo electrónico es obligatorio")
        @Email(message = "Formato de correo electrónico inválido")
        @Size(max = 100, message = "El correo electrónico no puede exceder los 100 caracteres")
        String email,

        @NotBlank(message = "El DNI es obligatorio")
        @Size(max = 20, message = "El DNI no puede exceder los 20 caracteres")
        String dni
) {
}
