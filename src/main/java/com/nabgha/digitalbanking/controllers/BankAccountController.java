package com.nabgha.digitalbanking.controllers;

import com.nabgha.digitalbanking.dtos.requests.CreditRequestDTO;
import com.nabgha.digitalbanking.dtos.requests.CurrentAccountRequestDTO;
import com.nabgha.digitalbanking.dtos.requests.DebitRequestDTO;
import com.nabgha.digitalbanking.dtos.requests.SavingAccountRequestDTO;
import com.nabgha.digitalbanking.dtos.requests.TransferRequestDTO;
import com.nabgha.digitalbanking.dtos.responses.AccountHistoryDTO;
import com.nabgha.digitalbanking.dtos.responses.AccountResponseDTO;
import com.nabgha.digitalbanking.dtos.responses.ApiResponse;
import com.nabgha.digitalbanking.exceptions.BalanceNotSufficientException;
import com.nabgha.digitalbanking.exceptions.BankAccountNotFoundException;
import com.nabgha.digitalbanking.exceptions.CustomerNotFoundException;
import com.nabgha.digitalbanking.services.BankAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class BankAccountController {

    private final BankAccountService bankAccountService;

    /// ── CREATE ACCOUNTS ──────────────────────────────

    @PostMapping("/current")
    public ResponseEntity<ApiResponse<AccountResponseDTO>> createCurrentAccount(
            @Valid @RequestBody CurrentAccountRequestDTO request
    ) throws CustomerNotFoundException {
        AccountResponseDTO account = bankAccountService.saveCurrentAccount(
                request.initialBalance(), 
                request.overDraft(), 
                request.customerId()
        );
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(account, "Current account created successfully"));
    }

    @PostMapping("/saving")
    public ResponseEntity<ApiResponse<AccountResponseDTO>> createSavingAccount(
            @Valid @RequestBody SavingAccountRequestDTO request
    ) throws CustomerNotFoundException {
        AccountResponseDTO account = bankAccountService.saveSavingAccount(
                request.initialBalance(), 
                request.interestRate(), 
                request.customerId()
        );
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(account, "Saving account created successfully"));
    }

    /// ── GET ACCOUNTS ─────────────────────────────────

    @GetMapping
    public ResponseEntity<ApiResponse<List<AccountResponseDTO>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(bankAccountService.listBankAccounts(), "OK"));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse<List<AccountResponseDTO>>> getByCustomer(
            @PathVariable UUID customerId) {
        return ResponseEntity.ok(ApiResponse.success(bankAccountService.getAccountsByCustomer(customerId), "OK"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AccountResponseDTO>> getById(
            @PathVariable UUID id) throws BankAccountNotFoundException {
        return ResponseEntity.ok(ApiResponse.success(bankAccountService.getBankAccount(id), "OK"));
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<ApiResponse<AccountHistoryDTO>> getHistory(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) throws BankAccountNotFoundException {
        return ResponseEntity.ok(ApiResponse.success(bankAccountService.getAccountHistory(id, page, size), "OK"));
    }

    /// ── OPERATIONS ────────────────────────────────────

    @PostMapping("/debit")
    public ResponseEntity<ApiResponse<Void>> debit(@RequestBody @Valid DebitRequestDTO dto) throws BankAccountNotFoundException, BalanceNotSufficientException {
        bankAccountService.debit(dto.accountId(), dto.amount(), dto.description());
        return ResponseEntity.ok(ApiResponse.success("Debit operation successful"));
    }

    @PostMapping("/credit")
    public ResponseEntity<ApiResponse<Void>> credit(
            @RequestBody @Valid CreditRequestDTO dto) throws BankAccountNotFoundException {
        bankAccountService.credit(dto.accountId(), dto.amount(), dto.description());
        return ResponseEntity.ok(ApiResponse.success("Credit operation successful"));
    }

    @PostMapping("/transfer")
    public ResponseEntity<ApiResponse<Void>> transfer(@RequestBody @Valid TransferRequestDTO dto) throws BankAccountNotFoundException, BalanceNotSufficientException {
        bankAccountService.transfer(dto.sourceId(), dto.destinationId(), dto.amount(), dto.description());
        return ResponseEntity.ok(ApiResponse.success("Transfer successful"));
    }
}
