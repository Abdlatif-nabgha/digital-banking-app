package com.nabgha.digitalbanking.dtos.responses;


public record AuthResponseDTO(
        String accessToken,
        String refreshToken
) {
}
