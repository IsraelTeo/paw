package com.veterinary.paw.dto;

import lombok.Builder;

import java.util.List;
@Builder
public record ErrorDTO(
        String description,
        List<String> reasons
) {
}
