package com.nabgha.digitalbanking.dtos.requests;


import java.util.UUID;

public record CreditRequestDTO(UUID accountId, double amount, String description){}
