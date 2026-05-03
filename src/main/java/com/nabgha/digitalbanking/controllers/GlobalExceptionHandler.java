package com.nabgha.digitalbanking.controllers;

import com.nabgha.digitalbanking.dtos.responses.ApiResponse;
import com.nabgha.digitalbanking.exceptions.BalanceNotSufficientException;
import com.nabgha.digitalbanking.exceptions.BankAccountNotFoundException;
import com.nabgha.digitalbanking.exceptions.CustomerNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleCustomerNotFound(CustomerNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
    }

    @ExceptionHandler(BankAccountNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccountNotFound(BankAccountNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
    }

    @ExceptionHandler(BalanceNotSufficientException.class)
    public ResponseEntity<ApiResponse<Void>> handleBalanceNotSufficient(BalanceNotSufficientException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(message));
    }
}
