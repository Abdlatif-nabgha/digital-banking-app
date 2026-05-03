package com.nabgha.digitalbanking.services;

import com.nabgha.digitalbanking.dtos.requests.CustomerRequestDTO;
<<<<<<< HEAD
import com.nabgha.digitalbanking.dtos.responses.CustomerResponseDTO;
import com.nabgha.digitalbanking.entities.BankAccount;
=======
import com.nabgha.digitalbanking.dtos.responses.AccountHistoryDTO;
import com.nabgha.digitalbanking.dtos.responses.AccountResponseDTO;
import com.nabgha.digitalbanking.dtos.responses.CustomerResponseDTO;
>>>>>>> main
import com.nabgha.digitalbanking.exceptions.BalanceNotSufficientException;
import com.nabgha.digitalbanking.exceptions.BankAccountNotFoundException;
import com.nabgha.digitalbanking.exceptions.CustomerNotFoundException;

import java.util.List;
import java.util.UUID;

public interface BankAccountService {
<<<<<<< HEAD
    CustomerResponseDTO saveCustomer(CustomerRequestDTO customer);
    BankAccount saveCurrentBankAccount(double initialSold, double overDraft, UUID customerId) throws CustomerNotFoundException;
    BankAccount saveSavingBankAccount(double initialSold, double interestRate, UUID customerId) throws CustomerNotFoundException;
    List<CustomerResponseDTO> listCustomer();
    CustomerResponseDTO getCustomer(UUID customerId) throws CustomerNotFoundException;
    BankAccount getBankAccount(UUID accountId) throws BankAccountNotFoundException;
    void debit(UUID accountId, double amount, String description) throws BalanceNotSufficientException, BankAccountNotFoundException;
    void credit(UUID accountId, double amount, String description) throws BankAccountNotFoundException;
    void transfer(UUID sourceId, UUID destinationId, double amount, String description) throws BalanceNotSufficientException;
=======

    ///  CUSTOMER
    CustomerResponseDTO saveCustomer(CustomerRequestDTO request);
    CustomerResponseDTO updateCustomer(UUID customerId, CustomerRequestDTO request) throws CustomerNotFoundException;
    void deleteCustomer(UUID customerId) throws CustomerNotFoundException;
    List<CustomerResponseDTO> listCustomers();
    CustomerResponseDTO getCustomer(UUID customerId) throws CustomerNotFoundException;
    List<CustomerResponseDTO> searchCustomers(String keyword);

    /// ACCOUNT
    AccountResponseDTO saveCurrentAccount(double initialBalance, double overDraft, UUID customerId) throws CustomerNotFoundException;
    AccountResponseDTO saveSavingAccount(double initialBalance, double interestRating, UUID customerId) throws CustomerNotFoundException;
    List<AccountResponseDTO> listBankAccounts();
    List<AccountResponseDTO> getAccountsByCustomer(UUID customerId);
    AccountResponseDTO getBankAccount(UUID accountId) throws BankAccountNotFoundException;
    AccountHistoryDTO getAccountHistory(UUID accountId, int page, int size) throws BankAccountNotFoundException;

    /// OPERATIONS
    void debit(UUID accountId, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficientException;
    void credit(UUID accountId, double amount, String description) throws BankAccountNotFoundException;
    void transfer(UUID sourceId, UUID destinationId, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficientException;

>>>>>>> main
}
