package com.nabgha.digitalbanking.services;

import com.nabgha.digitalbanking.dtos.requests.CustomerRequestDTO;
import com.nabgha.digitalbanking.dtos.responses.AccountHistoryDTO;
import com.nabgha.digitalbanking.dtos.responses.AccountResponseDTO;
import com.nabgha.digitalbanking.dtos.responses.CustomerResponseDTO;
import com.nabgha.digitalbanking.exceptions.BalanceNotSufficientException;
import com.nabgha.digitalbanking.exceptions.BankAccountNotFoundException;
import com.nabgha.digitalbanking.exceptions.CustomerNotFoundException;

import java.util.List;
import java.util.UUID;

public interface BankAccountService {

    // ─────────────────────────────────────────────
    // ADMIN ONLY
    // ─────────────────────────────────────────────
    List<CustomerResponseDTO> listCustomers();
    CustomerResponseDTO getCustomer(UUID customerId) throws CustomerNotFoundException;
    List<CustomerResponseDTO> searchCustomers(String keyword);
    List<AccountResponseDTO> listBankAccounts();
    AccountResponseDTO getBankAccount(UUID accountId) throws BankAccountNotFoundException;
    AccountHistoryDTO getAccountHistory(UUID accountId, int page, int size) throws BankAccountNotFoundException;
    void deleteCustomer(UUID customerId) throws CustomerNotFoundException;

    // ─────────────────────────────────────────────
    // CUSTOMER ONLY
    // ─────────────────────────────────────────────
    List<AccountResponseDTO> getMyAccounts(UUID customerId);
    AccountHistoryDTO getMyAccountHistory(UUID accountId, UUID customerId, int page, int size) throws BankAccountNotFoundException;

    // ─────────────────────────────────────────────
    // ADMIN & CUSTOMER
    // ─────────────────────────────────────────────
    CustomerResponseDTO saveCustomer(CustomerRequestDTO request);
    CustomerResponseDTO updateCustomer(UUID customerId, CustomerRequestDTO request) throws CustomerNotFoundException;
    AccountResponseDTO saveCurrentAccount(double initialBalance, double overDraft, UUID customerId) throws CustomerNotFoundException;
    AccountResponseDTO saveSavingAccount(double initialBalance, double interestRate, UUID customerId) throws CustomerNotFoundException;
    List<AccountResponseDTO> getAccountsByCustomer(UUID customerId);
    void debit(UUID accountId, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficientException;
    void credit(UUID accountId, double amount, String description) throws BankAccountNotFoundException;
    void transfer(UUID sourceId, UUID destinationId, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficientException;
}
