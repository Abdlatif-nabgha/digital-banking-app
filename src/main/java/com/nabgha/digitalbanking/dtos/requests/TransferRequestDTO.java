package com.nabgha.digitalbanking.dtos.requests;


import java.util.UUID;

public record TransferRequestDTO(UUID sourceId, UUID destinationId, double amount, String description){}
