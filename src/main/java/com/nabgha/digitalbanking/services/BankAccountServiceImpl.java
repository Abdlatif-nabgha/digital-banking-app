package com.nabgha.digitalbanking.services;

import com.nabgha.digitalbanking.dtos.requests.CustomerRequestDTO;
import com.nabgha.digitalbanking.dtos.responses.AccountHistoryDTO;
import com.nabgha.digitalbanking.dtos.responses.AccountResponseDTO;
import com.nabgha.digitalbanking.dtos.responses.CustomerResponseDTO;
import com.nabgha.digitalbanking.entities.*;
import com.nabgha.digitalbanking.enums.AccountStatus;
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


    // ==========================================
    // CUSTOMER MANAGEMENT
    // ==========================================

    @Override
    public CustomerResponseDTO saveCustomer(CustomerRequestDTO request) {
        log.info("Saving new customer: {}", request.name());
        /// 1. Map RequestDto → Entity
        Customer customer = customerMapper.toEntity(request);

        /// 2. Save entity to database
        Customer savedCustomer = customerRepository.save(customer);

        /// 3. Map Entity → ResponseDto & Return Response DTO
        return customerMapper.toDto(savedCustomer);
    }

    @Override
    public CustomerResponseDTO updateCustomer(UUID customerId, CustomerRequestDTO request) throws CustomerNotFoundException {
        log.info("Updating customer with ID: {}", customerId);
        /// 1. Fetch existing customer from DB or throw Exception
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer Not Found with ID: " + customerId));

        /// 2. Update entity fields with data from RequestDto (using MapStruct @MappingTarget)
        customerMapper.updateEntityFromDto(request, customer);

        /// 3. Save updated entity
        Customer updatedCustomer = customerRepository.save(customer);

        /// 4. Map and return ResponseDto
        return customerMapper.toDto(updatedCustomer);
    }

    @Override
    public void deleteCustomer(UUID customerId) throws CustomerNotFoundException {
        log.info("Deleting customer with ID: {}", customerId);
        /// 1. Check if customer exists or throw Exception
        if (!customerRepository.existsById(customerId)) {
            throw new CustomerNotFoundException("Cannot delete: Customer Not Found with ID: " + customerId);
        }
        /// 2. Delete customer from database
        customerRepository.deleteById(customerId);
    }

    @Override
    public List<CustomerResponseDTO> listCustomers() {
        log.info("Fetching all customers");
        /// 1. Fetch all customers from DB
        List<Customer> customers = customerRepository.findAll();
        /// 2. Map list of entities to list of ResponseDtos and Return it
        return customerMapper.toDtoList(customers);
    }

    @Override
    public CustomerResponseDTO getCustomer(UUID customerId) throws CustomerNotFoundException {
        log.info("Fetching customer with ID: {}", customerId);
        /// 1. Fetch customer by ID or throw Exception
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer Not Found with ID: " + customerId));
        /// 2. Map and return ResponseDto
        return customerMapper.toDto(customer);
    }

    @Override
    public List<CustomerResponseDTO> searchCustomers(String keyword) {
        log.info("Searching customers with keyword: {}", keyword);
        /// 1. Fetch customers matching keyword from DB
        List<Customer> customers = customerRepository.findByNameContainingIgnoreCase(keyword);
        /// 2. Map and return list of ResponseDtos
        return customerMapper.toDtoList(customers);
    }


    // ==========================================
    // ACCOUNT MANAGEMENT
    // ==========================================

    @Override
    public AccountResponseDTO saveCurrentAccount(double initialBalance, double overDraft, UUID customerId) throws CustomerNotFoundException {
        log.info("Saving new Current Account for customer: {}", customerId);
        /// 1. Fetch Customer from DB or throw Exception
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer Not Found with ID: " + customerId));

        /// 2. Create and initialize CurrentAccount entity
        CurrentAccount currentAccount = new CurrentAccount();
        currentAccount.setCreatedAt(LocalDateTime.now());
        currentAccount.setBalance(initialBalance);
        currentAccount.setStatus(AccountStatus.CREATED);
        currentAccount.setCustomer(customer);
        currentAccount.setOverDraft(overDraft);

        /// 3. Save account
        CurrentAccount savedAccount = bankAccountRepository.save(currentAccount);

        /// 4. Map and return ResponseDto
        return bankAccountMapper.currentAccountToDto(savedAccount);
    }

    @Override
    public AccountResponseDTO saveSavingAccount(double initialBalance, double interestRating, UUID customerId) throws CustomerNotFoundException {
        log.info("Saving new Saving Account for customer: {}", customerId);
        /// 1. Fetch Customer from DB or throw Exception
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer Not Found with ID: " + customerId));

        /// 2. Create and initialize SavingAccount entity
        SavingAccount savingAccount = new SavingAccount();
        savingAccount.setCreatedAt(LocalDateTime.now());
        savingAccount.setBalance(initialBalance);
        savingAccount.setStatus(AccountStatus.CREATED);
        savingAccount.setCustomer(customer);
        savingAccount.setInterestRate(interestRating);

        /// 3. Save account
        SavingAccount savedAccount = bankAccountRepository.save(savingAccount);

        /// 4. Map and return ResponseDto
        return bankAccountMapper.savingAccountToDto(savedAccount);
    }

    @Override
    public List<AccountResponseDTO> listBankAccounts() {
        log.info("Fetching all bank accounts");
        /// 1. Fetch all accounts from DB
        List<BankAccount> accounts = bankAccountRepository.findAll();

        /// 2. Use polymorphic mapper to return list of ResponseDtos
        return bankAccountMapper.toDtoList(accounts);
    }

    @Override
    public List<AccountResponseDTO> getAccountsByCustomer(UUID customerId) {
        log.info("Fetching accounts for customer ID: {}", customerId);
        /// 1. Fetch accounts for a specific customer from DB
        List<BankAccount> accounts = bankAccountRepository.findByCustomerId(customerId);

        /// 2. Map and return list of ResponseDtos
        return bankAccountMapper.toDtoList(accounts);
    }

    @Override
    public AccountResponseDTO getBankAccount(UUID accountId) throws BankAccountNotFoundException {
        log.info("Fetching Bank Account with ID: {}", accountId);
        /// 1. Fetch account or throw Exception
        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountNotFoundException("Bank Account Not Found with ID: " + accountId));

        /// 2. Map and return ResponseDto
        return bankAccountMapper.toDto(bankAccount);
    }

    @Override
    public AccountHistoryDTO getAccountHistory(UUID accountId, int page, int size) throws BankAccountNotFoundException {
        log.info("Fetching History for Account ID: {}, Page: {}, Size: {}", accountId, page, size);
        /// 1. Fetch account or throw Exception
        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountNotFoundException("Bank Account Not Found"));

        /// 2. Fetch paginated operations for this account from DB
        Page<Operation> operations = operationRepository.findByBankAccountIdOrderByDateDesc(accountId, PageRequest.of(page, size));

        /// 3. Build and return AccountHistoryDTO
        return new AccountHistoryDTO(
                bankAccount.getId(),
                bankAccount.getBalance(),
                page,
                operations.getTotalPages(),
                size,
                operationMapper.toDtoList(operations.getContent())
        );
    }


    // ==========================================
    // OPERATIONS MANAGEMENT
    // ==========================================

    @Override
    public void debit(UUID accountId, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficientException {
        log.info("Executing Debit on Account: {}, Amount: {}", accountId, amount);
        /// 1. Fetch account or throw Exception
        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountNotFoundException("Bank Account Not Found"));

        /// 2. Validate balance
        if (bankAccount.getBalance() < amount) {
            throw new BalanceNotSufficientException("Balance Not Sufficient for this operation");
        }

        /// 3. Create Operation (DEBIT) and save
        Operation operation = new Operation();
        operation.setType(OperationType.DEBIT);
        operation.setAmount(amount);
        operation.setDescription(description);
        operation.setDate(LocalDateTime.now());
        operation.setBankAccount(bankAccount);
        operationRepository.save(operation);

        /// 4. Update account balance and save
        bankAccount.setBalance(bankAccount.getBalance() - amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void credit(UUID accountId, double amount, String description) throws BankAccountNotFoundException {
        log.info("Executing Credit on Account: {}, Amount: {}", accountId, amount);
        /// 1. Fetch account or throw Exception
        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountNotFoundException("Bank Account Not Found"));

        /// 2. Create Operation (CREDIT) and save
        Operation operation = new Operation();
        operation.setType(OperationType.CREDIT);
        operation.setAmount(amount);
        operation.setDescription(description);
        operation.setDate(LocalDateTime.now());
        operation.setBankAccount(bankAccount);
        operationRepository.save(operation);

        /// 3. Update account balance and save
        bankAccount.setBalance(bankAccount.getBalance() + amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void transfer(UUID sourceId, UUID destinationId, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficientException {
        log.info("Executing Transfer from {} to {}, Amount: {}", sourceId, destinationId, amount);
        /// 1. Call debit(sourceId, amount)
        debit(sourceId, amount, "Transfer to " + destinationId + ": " + description);
        /// 2. Call credit(destinationId, amount)
        credit(destinationId, amount, "Transfer from " + sourceId + ": " + description);
    }
}
