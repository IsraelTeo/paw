package com.veterinary.paw.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.veterinary.paw.enums.AppointmentStatusEnum;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record VeterinaryAppointmentResponseDTO(
        Long id,

        AppointmentStatusEnum status,

        String observations,

        @JsonProperty("register_date")
        LocalDate registerDate,

        @JsonProperty("pet")
        PetResponseDTO petResponseDTO,

        @JsonProperty("veterinary")
        VeterinaryResponseDTO veterinaryResponseDTO,

        @JsonProperty("veterinary_service")
        VeterinaryServiceResponseDTO veterinaryServiceResponseDTO,

        @JsonProperty("shift")
        ShiftResponseDTO shiftResponseDTO
) {
}
