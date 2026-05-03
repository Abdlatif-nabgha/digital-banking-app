package com.nabgha.digitalbanking.dtos.responses;


import java.util.List;
import java.util.UUID;

public record AccountHistoryDTO(
        UUID accountId,
        double balance,
        int currentPage,
        int totalPages,
        int pageSize,
        List<OperationResponseDTO> operations
) {
}
