package com.nabgha.digitalbanking.repositories;

import com.nabgha.digitalbanking.entities.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    Page<Customer> findByNameContainingIgnoreCase(String keyword, Pageable pageable);
    List<Customer> findByNameContainingIgnoreCase(String keyword);
}
