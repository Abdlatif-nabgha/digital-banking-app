package com.nabgha.digitalbanking.dtos.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.UUID;

public record TransferRequestDTO(
        @NotNull(message = "Source account ID is mandatory")
        UUID sourceId,

        @NotNull(message = "Destination account ID is mandatory")
        UUID destinationId,

        @Positive(message = "Amount must be greater than zero")
        double amount,

        String description
) {}
