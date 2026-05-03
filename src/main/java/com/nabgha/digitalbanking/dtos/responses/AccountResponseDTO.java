package com.nabgha.digitalbanking.dtos.responses;


import com.nabgha.digitalbanking.enums.AccountStatus;
import com.nabgha.digitalbanking.enums.Currency;

import java.time.LocalDateTime;
import java.util.UUID;

public record AccountResponseDTO(
        UUID id,
        String type,          // "CURRENT" ou "SAVING"
        double balance,
        AccountStatus status,
        Currency currency,
        LocalDateTime createdAt,
        double overDraft,     // 0 si SavingAccount
        double interestRate,  // 0 si CurrentAccount
        UUID customerId,
        String customerName

) {
}
