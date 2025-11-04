package com.veterinary.paw.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record VeterinaryServiceResponseDTO(
        Long id,

        String name,

        String description,

        BigDecimal price

) {
}
