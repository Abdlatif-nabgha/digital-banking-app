package com.nabgha.digitalbanking.dtos.responses;


import java.util.UUID;

public record AccountResponseDTO(UUID id, double balance, String type, CustomerResponseDTO customer){}
