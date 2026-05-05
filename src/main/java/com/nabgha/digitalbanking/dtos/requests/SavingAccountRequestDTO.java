package com.nabgha.digitalbanking.dtos.requests;

import jakarta.validation.constraints.PositiveOrZero;

public record SavingAccountRequestDTO(
        @PositiveOrZero(message = "Initial balance must be positive or zero")
        double initialBalance,

        @PositiveOrZero(message = "Interest rate must be positive or zero")
        double interestRate
) {}
