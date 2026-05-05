package com.nabgha.digitalbanking.dtos.requests;

import jakarta.validation.constraints.PositiveOrZero;

public record CurrentAccountRequestDTO(
        @PositiveOrZero(message = "Initial balance must be positive or zero")
        double initialBalance,

        @PositiveOrZero(message = "Overdraft must be positive or zero")
        double overDraft
) {}
