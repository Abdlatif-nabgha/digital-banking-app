package com.nabgha.digitalbanking.dtos.requests;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CustomerRequestDTO(
        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 100)
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email
) {}