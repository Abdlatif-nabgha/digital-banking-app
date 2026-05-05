package com.nabgha.digitalbanking.services;

import com.nabgha.digitalbanking.dtos.requests.CustomerRequestDTO;
import com.nabgha.digitalbanking.dtos.responses.AccountHistoryDTO;
import com.nabgha.digitalbanking.dtos.responses.AccountResponseDTO;
import com.nabgha.digitalbanking.dtos.responses.CustomerResponseDTO;
import com.nabgha.digitalbanking.entities.*;
import com.nabgha.digitalbanking.enums.AccountStatus;
import com.nabgha.digitalbanking.enums.Currency;
import com.nabgha.digitalbanking.enums.OperationType;
import com.nabgha.digitalbanking.exceptions.BalanceNotSufficientException;
import com.nabgha.digitalbanking.exceptions.BankAccountNotFoundException;
import com.nabgha.digitalbanking.exceptions.CustomerNotFoundException;
import com.nabgha.digitalbanking.mappers.BankAccountMapper;
import com.nabgha.digitalbanking.mappers.CustomerMapper;
import com.nabgha.digitalbanking.mappers.OperationMapper;
import com.nabgha.digitalbanking.repositories.BankAccountRepository;
import com.nabgha.digitalbanking.repositories.CustomerRepository;
import com.nabgha.digitalbanking.repositories.OperationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    private final CustomerMapper customerMapper;
    private final BankAccountMapper bankAccountMapper;
    private final OperationMapper operationMapper;


    // ─────────────────────────────────────────────
    // ADMIN ONLY
    // ─────────────────────────────────────────────
    @Override
    public List<CustomerResponseDTO> listCustomers() {
        // Fetch all customer entities from the database
        List<Customer> customers = customerRepository.findAll();
        // Map entity list to DTO list and return
        return customerMapper.toDtoList(customers);
    }

    @Override
    public CustomerResponseDTO getCustomer(UUID customerId) throws CustomerNotFoundException {
        // Find customer or throw exception if not found
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found: " + customerId));
        // Map entity to DTO and return
        return customerMapper.toDto(customer);
    }

    @Override
    public List<CustomerResponseDTO> searchCustomers(String keyword) {
        // Search customers by name containing keyword
        List<Customer> customers = customerRepository.findByNameContainingIgnoreCase(keyword);
        // Map results to DTO list
        return customerMapper.toDtoList(customers);
    }

    @Override
    public List<AccountResponseDTO> listBankAccounts() {
        // Fetch all bank account entities
        List<BankAccount> bankAccounts = bankAccountRepository.findAll();
        // Map to DTO list
        return bankAccountMapper.toDtoList(bankAccounts);
    }

    @Override
    public AccountResponseDTO getBankAccount(UUID accountId) throws BankAccountNotFoundException {
        // Find account or throw exception
        BankAccount account = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountNotFoundException("Account not found: " + accountId));
        // Map to DTO
        return bankAccountMapper.toDto(account);
    }

    @Override
    public AccountHistoryDTO getAccountHistory(UUID accountId, int page, int size)
            throws BankAccountNotFoundException {
        // 1. Fetch account entity
        BankAccount account = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountNotFoundException("Account not found: " + accountId));
        // 2. Fetch paginated operations
        Page<Operation> pageResult = operationRepository
                .findByBankAccountIdOrderByDateDesc(accountId, PageRequest.of(page, size));
        // 3. Construct and return history DTO
        return new AccountHistoryDTO(
                accountId,
                account.getBalance(),
                pageResult.getNumber(),
                pageResult.getTotalPages(),
                size,
                operationMapper.toDtoList(pageResult.getContent()));
    }

    @Override
    public void deleteCustomer(UUID customerId) throws CustomerNotFoundException {
        // 1. Verify customer exists
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found: " + customerId));
        // 2. Cascade deletion to bank accounts
        bankAccountRepository.deleteByCustomerId(customerId);
        // 3. Delete customer entity
        customerRepository.delete(customer);
    }

    // ─────────────────────────────────────────────
    // CUSTOMER ONLY
    // ─────────────────────────────────────────────

    @Override
    public List<AccountResponseDTO> getMyAccounts(UUID customerId) {
        // Fetch accounts belonging to the specific customer
        return bankAccountMapper.toDtoList(
                bankAccountRepository.findByCustomerId(customerId));
    }

    @Override
    public AccountHistoryDTO getMyAccountHistory(UUID accountId, UUID customerId, int page, int size)
            throws BankAccountNotFoundException {
        // 1. Find account belonging specifically to this customer
        BankAccount account = bankAccountRepository.findByIdAndCustomerId(accountId, customerId)
                .orElseThrow(() -> new BankAccountNotFoundException(
                        "Account not found or does not belong to you"));
        // 2. Fetch paginated operations
        Page<Operation> pageResult = operationRepository
                .findByBankAccountIdOrderByDateDesc(accountId, PageRequest.of(page, size));
        // 3. Construct and return history DTO
        return new AccountHistoryDTO(
                accountId,
                account.getBalance(),
                pageResult.getNumber(),
                pageResult.getTotalPages(),
                size,
                operationMapper.toDtoList(pageResult.getContent()));
    }

    // ─────────────────────────────────────────────
    // ADMIN & CUSTOMER
    // ─────────────────────────────────────────────

    @Override
    public CustomerResponseDTO saveCustomer(CustomerRequestDTO request) {
        // 1. Map Request DTO -> Entity
        Customer customer = customerMapper.toEntity(request);
        // 2. Save Entity -> Map to Response DTO
        return customerMapper.toDto(customerRepository.save(customer));
    }

    @Override
    public CustomerResponseDTO updateCustomer(UUID customerId, CustomerRequestDTO request)
            throws CustomerNotFoundException {
        // 1. Fetch existing entity
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found: " + customerId));
        // 2. Update fields from Request DTO
        customer.setName(request.name());
        customer.setEmail(request.email());
        // 3. Save updated entity and return Response DTO
        return customerMapper.toDto(customerRepository.save(customer));
    }

    @Override
    public AccountResponseDTO saveCurrentAccount(double initialBalance, double overDraft, UUID customerId)
            throws CustomerNotFoundException {
        // 1. Find owner customer
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found: " + customerId));

        // 2. Business rule check: only one current account allowed
        long count = bankAccountRepository.countByCustomerIdAndType(customerId, "CurrentAccount");
        if (count >= 1) throw new RuntimeException("You already have a current account");

        // 3. Initialize new CurrentAccount entity
        CurrentAccount account = new CurrentAccount();
        account.setId(UUID.randomUUID());
        account.setCreatedAt(LocalDateTime.now());
        account.setBalance(initialBalance);
        account.setStatus(AccountStatus.CREATED);
        account.setCurrency(Currency.MAD);
        account.setOverDraft(overDraft);
        account.setCustomer(customer);

        // 4. Save and return Response DTO
        return bankAccountMapper.toDto(bankAccountRepository.save(account));
    }

    @Override
    public AccountResponseDTO saveSavingAccount(double initialBalance, double interestRate, UUID customerId)
            throws CustomerNotFoundException {
        // 1. Find owner customer
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found: " + customerId));

        // 2. Business rule check: only one saving account allowed
        long count = bankAccountRepository.countByCustomerIdAndType(customerId, "SAVING");
        if (count >= 1) throw new RuntimeException("You already have a saving account");

        // 3. Initialize new SavingAccount entity
        SavingAccount account = new SavingAccount();
        account.setId(UUID.randomUUID());
        account.setCreatedAt(LocalDateTime.now());
        account.setBalance(initialBalance);
        account.setStatus(AccountStatus.CREATED);
        account.setCurrency(Currency.MAD);
        account.setInterestRate(interestRate);
        account.setCustomer(customer);

        // 4. Save and return Response DTO
        return bankAccountMapper.toDto(bankAccountRepository.save(account));
    }

    @Override
    public List<AccountResponseDTO> getAccountsByCustomer(UUID customerId) {
        // Fetch and map accounts for a specific customer
        return bankAccountMapper.toDtoList(
                bankAccountRepository.findByCustomerId(customerId));
    }

    @Override
    public void debit(UUID accountId, double amount, String description)
            throws BankAccountNotFoundException, BalanceNotSufficientException {
        // 1. Fetch account
        BankAccount account = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountNotFoundException("Account not found: " + accountId));
        // 2. Check balance sufficiency
        if (account.getBalance() < amount)
            throw new BalanceNotSufficientException("Insufficient balance");

        // 3. Record DEBIT operation
        Operation operation = new Operation();
        operation.setType(OperationType.DEBIT);
        operation.setAmount(amount);
        operation.setDescription(description);
        operation.setDate(LocalDateTime.now());
        operation.setBankAccount(account);
        operationRepository.save(operation);

        // 4. Update account balance
        account.setBalance(account.getBalance() - amount);
        bankAccountRepository.save(account);
    }

    @Override
    public void credit(UUID accountId, double amount, String description)
            throws BankAccountNotFoundException {
        // 1. Fetch account
        BankAccount account = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountNotFoundException("Account not found: " + accountId));

        // 2. Record CREDIT operation
        Operation operation = new Operation();
        operation.setType(OperationType.CREDIT);
        operation.setAmount(amount);
        operation.setDescription(description);
        operation.setDate(LocalDateTime.now());
        operation.setBankAccount(account);
        operationRepository.save(operation);

        // 3. Update account balance
        account.setBalance(account.getBalance() + amount);
        bankAccountRepository.save(account);
    }

    @Override
    public void transfer(UUID sourceId, UUID destinationId, double amount, String description)
            throws BankAccountNotFoundException, BalanceNotSufficientException {
        // Perform debit on source and credit on destination
        debit(sourceId, amount, "Transfer to " + destinationId + " - " + description);
        credit(destinationId, amount, "Transfer from " + sourceId + " - " + description);
    }


}
