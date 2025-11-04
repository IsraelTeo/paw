package com.veterinary.paw.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;

@Builder
public record ShiftResponseDTO(
        Long id,

        @JsonProperty("shift_date")
        LocalDate date,

        @JsonProperty("start_time")
        LocalTime startTime,

        @JsonProperty("end_time")
        LocalTime endTime,

        @JsonProperty("available")
        Boolean available,

        @JsonProperty("veterinary")
        VeterinaryResponseDTO veterinaryResponseDTO
) {
}
