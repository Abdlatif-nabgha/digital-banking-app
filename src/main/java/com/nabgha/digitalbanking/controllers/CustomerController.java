package com.nabgha.digitalbanking.controllers;

import com.nabgha.digitalbanking.dtos.requests.*;
import com.nabgha.digitalbanking.dtos.responses.AccountHistoryDTO;
import com.nabgha.digitalbanking.dtos.responses.AccountResponseDTO;
import com.nabgha.digitalbanking.dtos.responses.ApiResponse;
import com.nabgha.digitalbanking.entities.AppUser;
import com.nabgha.digitalbanking.exceptions.BalanceNotSufficientException;
import com.nabgha.digitalbanking.exceptions.BankAccountNotFoundException;
import com.nabgha.digitalbanking.exceptions.CustomerNotFoundException;
import com.nabgha.digitalbanking.repositories.BankAccountRepository;
import com.nabgha.digitalbanking.services.BankAccountService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
@PreAuthorize("hasRole('USER')")
@RequiredArgsConstructor
public class CustomerController {

    private final BankAccountService bankAccountService;
    private final BankAccountRepository bankAccountRepository;

    // ── GET ───────────────────────────────────────────

    @GetMapping("/mine")
    public ResponseEntity<ApiResponse<List<AccountResponseDTO>>> getMyAccounts(
            Authentication authentication) {
        UUID customerId = getCustomerId(authentication);
        return ResponseEntity.ok(ApiResponse.success(
                bankAccountService.getMyAccounts(customerId), "OK"));
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<ApiResponse<AccountHistoryDTO>> getMyHistory(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) throws BankAccountNotFoundException {
        UUID customerId = getCustomerId(authentication);
        return ResponseEntity.ok(ApiResponse.success(
                bankAccountService.getMyAccountHistory(id, customerId, page, size), "OK"));
    }

    // ── CREATE ────────────────────────────────────────

    @PostMapping("/current")
    public ResponseEntity<ApiResponse<AccountResponseDTO>> createCurrent(
             @RequestBody @Valid CurrentAccountRequestDTO dto,
            Authentication authentication) throws CustomerNotFoundException {
        UUID customerId = getCustomerId(authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                bankAccountService.saveCurrentAccount(dto.initialBalance(), dto.overDraft(), customerId),
                "Current account created"));
    }

    @PostMapping("/saving")
    public ResponseEntity<ApiResponse<AccountResponseDTO>> createSaving(
            @RequestBody @Valid SavingAccountRequestDTO dto,
            Authentication authentication) throws CustomerNotFoundException {
        UUID customerId = getCustomerId(authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                bankAccountService.saveSavingAccount(dto.initialBalance(), dto.interestRate(), customerId),
                "Saving account created"));
    }

    // ── OPERATIONS ────────────────────────────────────

    @PostMapping("/debit")
    public ResponseEntity<ApiResponse<Void>> debit(
            @RequestBody @Valid DebitRequestDTO dto,
            Authentication authentication)
            throws BankAccountNotFoundException, BalanceNotSufficientException {
        checkOwnership(dto.accountId(), getCustomerId(authentication));
        bankAccountService.debit(dto.accountId(), dto.amount(), dto.description());
        return ResponseEntity.ok(ApiResponse.success("Debit successful"));
    }

    @PostMapping("/credit")
    public ResponseEntity<ApiResponse<Void>> credit(
            @RequestBody @Valid CreditRequestDTO dto,
            Authentication authentication) throws BankAccountNotFoundException {
        checkOwnership(dto.accountId(), getCustomerId(authentication));
        bankAccountService.credit(dto.accountId(), dto.amount(), dto.description());
        return ResponseEntity.ok(ApiResponse.success("Credit successful"));
    }

    @PostMapping("/transfer")
    public ResponseEntity<ApiResponse<Void>> transfer(
            @RequestBody @Valid TransferRequestDTO dto,
            Authentication authentication)
            throws BankAccountNotFoundException, BalanceNotSufficientException {
        // Vérifie seulement le compte SOURCE
        checkOwnership(dto.sourceId(), getCustomerId(authentication));
        bankAccountService.transfer(
                dto.sourceId(), dto.destinationId(), dto.amount(), dto.description());
        return ResponseEntity.ok(ApiResponse.success("Transfer successful"));
    }

    // ── HELPERS ───────────────────────────────────────

    private UUID getCustomerId(Authentication authentication) {
        AppUser user = (AppUser) authentication.getPrincipal();
        if (user.getCustomer() == null) {
            throw new RuntimeException("User has no associated customer profile");
        }
        return user.getCustomer().getId();
    }

    private void checkOwnership(UUID accountId, UUID customerId)
            throws BankAccountNotFoundException {
        bankAccountRepository.findByIdAndCustomerId(accountId, customerId)
                .orElseThrow(() -> new BankAccountNotFoundException(
                        "Account not found or does not belong to you"));
    }

}
