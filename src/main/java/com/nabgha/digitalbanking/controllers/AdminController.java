package com.nabgha.digitalbanking.controllers;

import com.nabgha.digitalbanking.dtos.responses.AccountHistoryDTO;
import com.nabgha.digitalbanking.dtos.responses.AccountResponseDTO;
import com.nabgha.digitalbanking.dtos.responses.ApiResponse;
import com.nabgha.digitalbanking.dtos.responses.CustomerResponseDTO;
import com.nabgha.digitalbanking.exceptions.BankAccountNotFoundException;
import com.nabgha.digitalbanking.exceptions.CustomerNotFoundException;
import com.nabgha.digitalbanking.services.BankAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final BankAccountService bankAccountService;

    @GetMapping("/customers")
    public ResponseEntity<ApiResponse<List<CustomerResponseDTO>>> getAllCustomers() {
        return ResponseEntity.ok(ApiResponse.success(bankAccountService.listCustomers(), "OK"));
    }

    @GetMapping("/customers/{id}")
    public ResponseEntity<ApiResponse<CustomerResponseDTO>> getCustomer(
            @PathVariable UUID id) throws CustomerNotFoundException {
        return ResponseEntity.ok(ApiResponse.success(bankAccountService.getCustomer(id), "OK"));
    }

    @GetMapping("/customers/search")
    public ResponseEntity<ApiResponse<List<CustomerResponseDTO>>> search(
            @RequestParam String keyword) {
        return ResponseEntity.ok(ApiResponse.success(bankAccountService.searchCustomers(keyword), "OK"));
    }

    @DeleteMapping("/customers/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCustomer(
            @PathVariable UUID id) throws CustomerNotFoundException {
        bankAccountService.deleteCustomer(id);
        return ResponseEntity.ok(ApiResponse.success("Customer deleted"));
    }

    @GetMapping("/accounts")
    public ResponseEntity<ApiResponse<List<AccountResponseDTO>>> getAllAccounts() {
        return ResponseEntity.ok(ApiResponse.success(bankAccountService.listBankAccounts(), "OK"));
    }

    @GetMapping("/accounts/{id}")
    public ResponseEntity<ApiResponse<AccountResponseDTO>> getAccount(
            @PathVariable UUID id) throws BankAccountNotFoundException {
        return ResponseEntity.ok(ApiResponse.success(bankAccountService.getBankAccount(id), "OK"));
    }

    @GetMapping("/accounts/{id}/history")
    public ResponseEntity<ApiResponse<AccountHistoryDTO>> getHistory(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) throws BankAccountNotFoundException {
        return ResponseEntity.ok(ApiResponse.success(
                bankAccountService.getAccountHistory(id, page, size), "OK"));
    }

    @GetMapping("/customers/{customerId}/accounts")
    public ResponseEntity<ApiResponse<List<AccountResponseDTO>>> getCustomerAccounts(
            @PathVariable UUID customerId) {
        return ResponseEntity.ok(ApiResponse.success(
                bankAccountService.getAccountsByCustomer(customerId), "OK"));
    }

}
