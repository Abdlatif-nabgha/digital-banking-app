package com.nabgha.digitalbanking.repositories;

import com.nabgha.digitalbanking.entities.BankAccount;
import com.nabgha.digitalbanking.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, UUID> {
    List<BankAccount> findByCustomerId(UUID customerId);

    @Modifying
    void deleteByCustomerId(UUID customerId);

    Optional<BankAccount> findByIdAndCustomerId(UUID id, UUID customerId);

    @Query("SELECT COUNT(b) FROM BankAccount b WHERE b.customer.id = :customerId AND TYPE(b) = :type")
    long countByCustomerIdAndType(UUID customerId, String type);
}
