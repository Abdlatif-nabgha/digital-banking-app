package com.nabgha.digitalbanking.dtos.requests;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;
public record TransferRequestDTO(
        @NotNull UUID sourceId,
        @NotNull UUID destinationId,
        @Positive(message = "Amount must be positive")
        double amount,
        String description
) {}