package com.nabgha.digitalbanking.controllers;

import com.nabgha.digitalbanking.dtos.responses.AuthResponseDTO;
import com.nabgha.digitalbanking.dtos.requests.LoginRequestDTO;
import com.nabgha.digitalbanking.dtos.requests.RegisterRequestDTO;
import com.nabgha.digitalbanking.dtos.responses.ApiResponse;
import com.nabgha.digitalbanking.dtos.responses.UserResponseDTO;
import com.nabgha.digitalbanking.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling authentication requests.
 * Provides endpoints for registration, login, email verification, and token refresh.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Registers a new user.
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponseDTO>> register(
            @RequestBody @Valid RegisterRequestDTO dto) {
        UserResponseDTO response = authService.register(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Registration successful. Please check your email to verify your account."));
    }

    /**
     * Verifies a user's email address using a verification token.
     */
    @GetMapping("/verify")
    public ResponseEntity<ApiResponse<Void>> verify(
            @RequestParam String token) {
        authService.verifyEmail(token);
        return ResponseEntity.ok(
                ApiResponse.success("Email verified successfully. You can now login.")
        );
    }

    /**
     * Authenticates a user and returns JWT access and refresh tokens.
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> login(
            @RequestBody @Valid LoginRequestDTO dto) {
        AuthResponseDTO response = authService.login(dto);
        return ResponseEntity.ok(ApiResponse.success(response, "Login successful"));
    }

    /**
     * Refreshes an expired access token using a valid refresh token.
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> refresh(
            @RequestHeader("Authorization") String authHeader) {
        // Extract the token from the "Authorization: Bearer <token>" header
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Invalid or missing Authorization header"));
        }
        
        String refreshToken = authHeader.substring(7);
        AuthResponseDTO response = authService.refresh(refreshToken);
        return ResponseEntity.ok(ApiResponse.success(response, "Token refreshed"));
    }
}
