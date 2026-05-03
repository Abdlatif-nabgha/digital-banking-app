package com.nabgha.digitalbanking.dtos.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.UUID;

public record CreditRequestDTO(
        @NotNull(message = "Account ID is mandatory")
        UUID accountId,

        @Positive(message = "Amount must be greater than zero")
        double amount,

        String description
) {}
