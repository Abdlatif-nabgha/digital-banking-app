package com.nabgha.digitalbanking.dtos.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nabgha.digitalbanking.enums.AccountStatus;
import com.nabgha.digitalbanking.enums.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountResponseDTO {
        private UUID id;
        private String type;          // "CURRENT" ou "SAVING"
        private double balance;
        private AccountStatus status;
        private Currency currency;
        private LocalDateTime createdAt;
        private Double overDraft;     // null si SavingAccount
        private Double interestRate;  // null si CurrentAccount
        private UUID customerId;
        private String customerName;
}
