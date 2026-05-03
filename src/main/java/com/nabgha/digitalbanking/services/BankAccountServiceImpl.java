package com.nabgha.digitalbanking.services;

import com.nabgha.digitalbanking.entities.*;
import com.nabgha.digitalbanking.enums.AccountStatus;
import com.nabgha.digitalbanking.enums.OperationType;
import com.nabgha.digitalbanking.exceptions.BalanceNotSufficientException;
import com.nabgha.digitalbanking.exceptions.BankAccountNotFoundException;
import com.nabgha.digitalbanking.exceptions.CustomerNotFoundException;
import com.nabgha.digitalbanking.repositories.BankAccountRepository;
import com.nabgha.digitalbanking.repositories.CustomerRepository;
import com.nabgha.digitalbanking.repositories.OperationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BankAccountServiceImpl implements BankAccountService {

    private final CustomerRepository customerRepository;
    private final BankAccountRepository bankAccountRepository;
    private final OperationRepository operationRepository;

    @Override
    public Customer saveCustomer(Customer customer) {
        Customer savedCustomer = customerRepository.save(customer);
        log.info("Saving new customer");
        return savedCustomer;
    }

    @Override
    public BankAccount saveCurrentBankAccount(double initialSold, double overDraft, UUID customerId) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null) throw new CustomerNotFoundException("Customer not found");
        CurrentAccount currentAccount = new CurrentAccount();
        currentAccount.setBalance(initialSold);
        currentAccount.setCreatedAt(LocalDateTime.now());
        currentAccount.setOverDraft(overDraft);
        currentAccount.setStatus(AccountStatus.CREATED);
        currentAccount.setCustomer(customer);
        return currentAccount;
    }

    @Override
    public BankAccount saveSavingBankAccount(double initialSold, double interestRate, UUID customerId) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null) throw new CustomerNotFoundException("Customer not found");
        SavingAccount savingAccount = new SavingAccount();
        savingAccount.setBalance(initialSold);
        savingAccount.setCreatedAt(LocalDateTime.now());
        savingAccount.setStatus(AccountStatus.CREATED);
        savingAccount.setInterestRate(interestRate);
        savingAccount.setCustomer(customer);
        return savingAccount;
    }

    @Override
    public List<Customer> listCustomer() {
        return customerRepository.findAll();
    }

    @Override
    public BankAccount getBankAccount(UUID accountId) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountNotFoundException("Bank account not found"));

        return bankAccount;
    }

    @Override
    public void debit(UUID accountId, double amount) throws BalanceNotSufficientException {
        BankAccount bankAccount = bankAccountRepository.getBankAccountsById(accountId);
        if (bankAccount.getBalance() < amount) {
            throw new BalanceNotSufficientException("Balance not sufficient");
        }
        Operation operation = new Operation();
        operation.setAmount(amount);
        operation.setDate(LocalDateTime.now());
        operation.setType(OperationType.DEBIT);
        operation.setBankAccount(bankAccount);
        operationRepository.save(operation);
        bankAccount.setBalance(bankAccount.getBalance()-amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void credit(UUID accountId, double amount) {
        BankAccount bankAccount = bankAccountRepository.getBankAccountsById(accountId);

        Operation operation = new Operation();
        operation.setAmount(amount);
        operation.setDate(LocalDateTime.now());
        operation.setType(OperationType.CREDIT);
        operation.setBankAccount(bankAccount);
        operationRepository.save(operation);
        bankAccount.setBalance(bankAccount.getBalance()+amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void transfer(UUID accountSource, UUID accountDestination, double amount, String description) throws BalanceNotSufficientException {
        debit(accountSource, amount);
        credit(accountDestination, amount);
    }
}
