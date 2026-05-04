package com.nabgha.digitalbanking.dtos.requests;


public record AuthResponseDTO(
        String accessToken,
        String refreshToken
) {
}
