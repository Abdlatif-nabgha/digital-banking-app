package com.nabgha.digitalbanking.repositories;

import com.nabgha.digitalbanking.entities.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BankAccountRepository extends JpaRepository<BankAccount, UUID> {
    List<BankAccount> findByCustomerId(UUID customerId);

    BankAccount getBankAccountsById(UUID id);

    void deleteByCustomerId(UUID customerId);

}
