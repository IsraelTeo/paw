package com.veterinary.paw.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record CustomerResponseDTO (
        Long id,

        @JsonProperty("first_name")
        String firstName,

        @JsonProperty("last_name")
        String lastName,

        String dni,

        @JsonProperty("phone_number")
        String phoneNumber,

        String email
){
}
