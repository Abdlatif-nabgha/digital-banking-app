package com.nabgha.digitalbanking.services;

import com.nabgha.digitalbanking.dtos.requests.CustomerRequestDTO;
import com.nabgha.digitalbanking.dtos.responses.CustomerResponseDTO;
import com.nabgha.digitalbanking.entities.BankAccount;
import com.nabgha.digitalbanking.exceptions.BalanceNotSufficientException;
import com.nabgha.digitalbanking.exceptions.BankAccountNotFoundException;
import com.nabgha.digitalbanking.exceptions.CustomerNotFoundException;

import java.util.List;
import java.util.UUID;

public interface BankAccountService {
    CustomerResponseDTO saveCustomer(CustomerRequestDTO customer);
    BankAccount saveCurrentBankAccount(double initialSold, double overDraft, UUID customerId) throws CustomerNotFoundException;
    BankAccount saveSavingBankAccount(double initialSold, double interestRate, UUID customerId) throws CustomerNotFoundException;
    List<CustomerResponseDTO> listCustomer();
    CustomerResponseDTO getCustomer(UUID customerId) throws CustomerNotFoundException;
    BankAccount getBankAccount(UUID accountId) throws BankAccountNotFoundException;
    void debit(UUID accountId, double amount, String description) throws BalanceNotSufficientException, BankAccountNotFoundException;
    void credit(UUID accountId, double amount, String description) throws BankAccountNotFoundException;
    void transfer(UUID sourceId, UUID destinationId, double amount, String description) throws BalanceNotSufficientException;
}
