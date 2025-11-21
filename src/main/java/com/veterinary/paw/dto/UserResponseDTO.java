package com.veterinary.paw.dto;

import lombok.Builder;

@Builder
public record UserResponseDTO(
        Long id,
        String email
) {
}
