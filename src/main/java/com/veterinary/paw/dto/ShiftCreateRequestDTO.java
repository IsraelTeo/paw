package com.veterinary.paw.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;

@Builder
public record ShiftCreateRequestDTO(
        @JsonProperty("shift_date")
        @NotNull(message = "La fecha del turno es obligatoria")
        @FutureOrPresent(message = "La fecha del turno debe ser hoy o en el futuro")
        LocalDate date,

        @JsonProperty("start_time")
        @NotNull(message = "La hora de inicio es obligatoria")
        LocalTime startTime,

        @JsonProperty("end_time")
        @NotNull(message = "La hora de fin es obligatoria")
        LocalTime endTime,

        @JsonProperty("veterinary_id")
        @NotNull(message = "El ID del veterinario es obligatorio")
        @Positive(message = "El ID del veterinario debe ser un valor positivo")
        Long veterinaryId
) {
}
