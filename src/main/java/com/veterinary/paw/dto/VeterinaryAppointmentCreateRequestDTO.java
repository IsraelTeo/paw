package com.veterinary.paw.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.veterinary.paw.enums.AppointmentStatusEnum;
import jakarta.validation.constraints.*;
import lombok.Builder;

@Builder
public record VeterinaryAppointmentCreateRequestDTO(

        @NotNull(message = "Status cannot be null")
        AppointmentStatusEnum status,

        @Size(max = 255, message = "Observations cannot exceed 255 characters")
        String observations,

        @JsonProperty("id_pet")
        @NotNull(message = "Pet ID must be provided")
        @Positive(message = "Pet ID must be a positive number")
        @Min(1)
        Long idPet,

        @JsonProperty("id_veterinary")
        @NotNull(message = "Veterinary ID must be provided")
        @Positive(message = "Veterinary ID must be a positive number")
        @Min(1)
        Long idVeterinary,

        @JsonProperty("id_veterinary_service")
        @NotNull(message = "Veterinary service ID must be provided")
        @Positive(message = "Veterinary service ID must be a positive number")
        @Min(1)
        Long idVeterinaryService,

        @JsonProperty("id_shift")
        @NotNull(message = "Shift ID must be provided")
        @Positive(message = "Shift ID must be a positive number")
        @Min(1)
        Long idShift
){
    public VeterinaryAppointmentCreateRequestDTO {
        if (status == null) {
            status = AppointmentStatusEnum.PENDIENTE;
        }
    }
}
