package com.nabgha.digitalbanking.controllers;

import com.nabgha.digitalbanking.dtos.requests.CustomerRequestDTO;
import com.nabgha.digitalbanking.dtos.responses.ApiResponse;
import com.nabgha.digitalbanking.dtos.responses.CustomerResponseDTO;
import com.nabgha.digitalbanking.exceptions.CustomerNotFoundException;
import com.nabgha.digitalbanking.services.BankAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.parser.Entity;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customers")
public class CustomerController {

    private final BankAccountService bankAccountService;

    @PostMapping
    public ResponseEntity<ApiResponse<CustomerResponseDTO>> save(
         @Valid @RequestBody CustomerRequestDTO dto
    )
    {
        CustomerResponseDTO customer = bankAccountService.saveCustomer(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(customer, "Customer created successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CustomerResponseDTO>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(bankAccountService.listCustomers(), "OK"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerResponseDTO>> getById(@PathVariable UUID id) throws CustomerNotFoundException {
        return ResponseEntity.ok(ApiResponse.success(bankAccountService.getCustomer(id), "OK"));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<CustomerResponseDTO>>> search(
            @RequestParam String keyword
    ){
        return ResponseEntity.ok(ApiResponse.success(bankAccountService.searchCustomers(keyword), "OK"));
    }


}
