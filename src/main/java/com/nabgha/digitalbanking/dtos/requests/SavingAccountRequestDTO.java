package com.nabgha.digitalbanking.dtos.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.UUID;

public record SavingAccountRequestDTO(
        @NotNull(message = "Customer ID is mandatory")
        UUID customerId,

        @PositiveOrZero(message = "Initial balance must be positive or zero")
        double initialBalance,

        @PositiveOrZero(message = "Interest rate must be positive or zero")
        double interestRate
) {}
