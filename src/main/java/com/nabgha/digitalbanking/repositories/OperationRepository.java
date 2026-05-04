package com.nabgha.digitalbanking.repositories;

import com.nabgha.digitalbanking.entities.Operation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OperationRepository extends JpaRepository<Operation, Long> {
    Page<Operation> findByBankAccountIdOrderByDateDesc(UUID bankAccountId, Pageable pageable);
}
