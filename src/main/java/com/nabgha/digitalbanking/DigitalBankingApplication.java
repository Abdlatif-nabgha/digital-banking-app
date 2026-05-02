package com.nabgha.digitalbanking;

import com.nabgha.digitalbanking.entities.CurrentAccount;
import com.nabgha.digitalbanking.entities.Customer;
import com.nabgha.digitalbanking.entities.Operation;
import com.nabgha.digitalbanking.entities.SavingAccount;
import com.nabgha.digitalbanking.enums.AccountStatus;
import com.nabgha.digitalbanking.enums.OperationType;
import com.nabgha.digitalbanking.repositories.BankAccountRepository;
import com.nabgha.digitalbanking.repositories.CustomerRepository;
import com.nabgha.digitalbanking.repositories.OperationRepository;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.stream.Stream;

@SpringBootApplication
public class DigitalBankingApplication {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));

        SpringApplication.run(DigitalBankingApplication.class, args);
    }

    @Bean
    CommandLineRunner start(
            CustomerRepository customerRepository,
            BankAccountRepository bankAccountRepository,
            OperationRepository operationRepository
    )
    {
        return args -> {
            Random random = new Random();
            Stream.of("nabgha", "abdelatif", "yassine").forEach(name-> {
                Customer customer = new Customer();
                customer.setName(name);
                customer.setEmail(name+"@gmail.com");
                customer.setPassword(String.valueOf(random.nextInt(3000) + 1000));
                customerRepository.save(customer);
            });
            customerRepository.findAll().forEach(customer -> {
                CurrentAccount currentAccount = new CurrentAccount();
                currentAccount.setBalance(Math.random() * 90000);
                currentAccount.setCreatedAt(LocalDateTime.now());
                currentAccount.setStatus(AccountStatus.CREATED);
                currentAccount.setCustomer(customer);
                currentAccount.setOverDraft(10000);
                bankAccountRepository.save(currentAccount);

                SavingAccount savingAccount = new SavingAccount();
                savingAccount.setBalance(Math.random() * 90000);
                savingAccount.setCreatedAt(LocalDateTime.now());
                savingAccount.setStatus(AccountStatus.CREATED);
                savingAccount.setCustomer(customer);
                savingAccount.setInterestRate(5.5);
                bankAccountRepository.save(savingAccount);
            });

            bankAccountRepository.findAll().forEach(bankAccount -> {
                for (int i = 0; i < 10; i++) {
                    Operation operation = new Operation();
                    operation.setDate(LocalDateTime.now());
                    operation.setAmount(Math.random()*1000);
                    operation.setType(Math.random() > 0.5 ? OperationType.CREDIT : OperationType.DEBIT);
                    operation.setBankAccount(bankAccount);
                    operationRepository.save(operation);
                }
            });

        };
    }

}
