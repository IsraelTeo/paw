package com.veterinary.paw.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.veterinary.paw.enums.AppointmentStatusEnum;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record VeterinaryAppointmentCreateResponseDTO (
        Long id,

        AppointmentStatusEnum status,

        String observations,

        @JsonProperty("register_date")
        LocalDate registerDate,

        @JsonProperty("id_pet")
        Long idPet,

        @JsonProperty("id_veterinary")
        Long idVeterinary,

        @JsonProperty("id_service")
        Long idVeterinaryService,

        @JsonProperty("id_shift")
        Long idShift
) {}
