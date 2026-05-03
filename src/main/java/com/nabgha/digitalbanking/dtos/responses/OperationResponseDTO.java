package com.nabgha.digitalbanking.dtos.responses;


import com.nabgha.digitalbanking.enums.OperationType;

import java.time.LocalDateTime;

public record OperationResponseDTO(
        Long id,
        LocalDateTime date,
        double amount,
        String description,
        OperationType type
) {
}
