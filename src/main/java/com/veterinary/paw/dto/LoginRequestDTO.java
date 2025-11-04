package com.veterinary.paw.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record LoginRequestDTO(

        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Email must be a valid email address")
        @Size(max = 255, message = "Email cannot exceed 255 characters")
        String email,

        @NotBlank(message = "Password cannot be blank")
        @Size(max = 255, message = "Password cannot exceed 255 characters")
        String password

) {
}