package com.nabgha.digitalbanking.dtos.requests;


import java.util.UUID;

public record DebitRequestDTO(UUID accountId, double amount, String description){}
