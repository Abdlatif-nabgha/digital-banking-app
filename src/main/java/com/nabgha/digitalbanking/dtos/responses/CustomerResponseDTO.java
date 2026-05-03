package com.nabgha.digitalbanking.dtos.responses;


import java.util.UUID;

public record CustomerResponseDTO(UUID id, String name, String email){}
