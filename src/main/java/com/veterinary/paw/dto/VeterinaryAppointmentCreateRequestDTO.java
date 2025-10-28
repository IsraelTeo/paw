package com.veterinary.paw.dto;

import com.veterinary.paw.enums.AppointmentStatusEnum;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record VeterinaryServiceRequestDTO (
        @NotNull(message = "El estado no puede ser nulo")
        AppointmentStatusEnum status,

        @Size(max = 255, message = "Las observaciones no pueden tener más de 255 caracteres")
        String observations,

        @NotNull(message = "Debe proporcionar el ID de la mascota")
        @Positive(message = "El ID de la mascota debe ser un número positivo")
        Long idPet,

        @NotNull(message = "Debe proporcionar el ID del veterinario")
        @Positive(message = "El ID del veterinario debe ser un número positivo")
        Long idVeterinary,

        @NotNull(message = "Debe proporcionar el ID del servicio veterinario")
        @Positive(message = "El ID del servicio veterinario debe ser un número positivo")
        Long idVeterinaryService,

        @NotNull(message = "Debe proporcionar el ID del turno")
        @Positive(message = "El ID del turno debe ser un número positivo")
        Long idShift
){
    public VeterinaryAppointmentRequestDTO {
        if (status == null) {
            status = AppointmentStatusEnum.PENDIENTE;
        }
    }
    
}
