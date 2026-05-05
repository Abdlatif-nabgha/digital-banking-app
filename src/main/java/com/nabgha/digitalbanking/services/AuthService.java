package com.nabgha.digitalbanking.services;

import com.nabgha.digitalbanking.dtos.responses.AuthResponseDTO;
import com.nabgha.digitalbanking.dtos.requests.LoginRequestDTO;
import com.nabgha.digitalbanking.dtos.requests.RegisterRequestDTO;
import com.nabgha.digitalbanking.dtos.responses.UserResponseDTO;
import com.nabgha.digitalbanking.entities.AppUser;
import com.nabgha.digitalbanking.entities.Customer;
import com.nabgha.digitalbanking.enums.Role;
import com.nabgha.digitalbanking.exceptions.AccountNotVerifiedException;
import com.nabgha.digitalbanking.exceptions.EmailAlreadyInUseException;
import com.nabgha.digitalbanking.exceptions.InvalidTokenException;
import com.nabgha.digitalbanking.repositories.AppUserRepository;
import com.nabgha.digitalbanking.repositories.CustomerRepository;
import com.nabgha.digitalbanking.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service responsible for handling all authentication-related tasks:
 * Registration, Email Verification, Login, and Token Refresh.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AppUserRepository appUserRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    /**
     * Registers a new user and its associated customer.
     * Sends a verification email after successful registration.
     */
    @Transactional
    public UserResponseDTO register(RegisterRequestDTO request) {
        log.info("Processing registration for email: {}", request.email());

        // 1. Check if email is already taken
        if (appUserRepository.existsByEmail(request.email())) {
            log.error("Registration failed: Email {} already in use", request.email());
            throw new EmailAlreadyInUseException("Email is already in use");
        }

        // 2. Create and save the associated Customer entity
        Customer customer = new Customer();
        customer.setName(request.firstName() + " " + request.lastName());
        customer.setEmail(request.email());
        Customer savedCustomer = customerRepository.save(customer);

        // 3. Generate a unique verification token
        String verificationToken = UUID.randomUUID().toString();

        // 4. Create and save the AppUser entity (linked to Customer)
        AppUser user = AppUser.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .enabled(false) // User must verify email before they can log in
                .verificationToken(verificationToken)
                .verificationTokenExpiresAt(LocalDateTime.now().plusMinutes(15))
                .customer(savedCustomer)
                .build();

        AppUser savedUser = appUserRepository.save(user);

        // 5. Trigger the verification email asynchronously
        emailService.sendVerificationEmail(request.email(), verificationToken);
        log.info("Registration successful. Verification email sent to: {}", request.email());

        // Return the DTO manually since we already have all the data perfectly separated
        return new UserResponseDTO(
                savedUser.getId(),
                savedUser.getEmail(),
                request.firstName(),
                request.lastName(),
                savedUser.getRole(),
                savedUser.isEnabled()
        );
    }

    /**
     * Verifies the user's email using the token sent in the registration email.
     */
    @Transactional
    public void verifyEmail(String token) {
        log.info("Verifying email with token: {}", token);

        // 1. Find user by verification token
        AppUser user = appUserRepository.findByVerificationToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid verification token"));

        // 2. Check if the token has expired
        if (user.getVerificationTokenExpiresAt().isBefore(LocalDateTime.now())) {
            log.warn("Verification failed: Token expired for user {}", user.getEmail());
            throw new InvalidTokenException("Verification token expired");
        }

        // 3. Activate the account and clear token info
        user.setEnabled(true);
        user.setVerificationToken(null);
        user.setVerificationTokenExpiresAt(null);
        appUserRepository.save(user);

        log.info("Account successfully activated for user: {}", user.getEmail());
    }

    /**
     * Authenticates a user and generates JWT Access and Refresh tokens.
     */
    public AuthResponseDTO login(LoginRequestDTO dto) {
        log.info("Attempting login for user: {}", dto.email());

        // 1. Authenticate credentials using Spring Security's AuthenticationManager
        // This will automatically throw BadCredentialsException if login fails
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.email(), dto.password())
        );

        // 2. Fetch the user from the database
        AppUser user = appUserRepository.findByEmail(dto.email())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + dto.email()));

        // 3. Ensure the user has verified their email
        if (!user.isEnabled()) {
            log.warn("Login failed: User {} attempted to login without verification", dto.email());
            throw new AccountNotVerifiedException("Account not verified. Please check your email.");
        }

        // 4. Generate the JWT tokens
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        log.info("Login successful for user: {}", dto.email());

        return new AuthResponseDTO(accessToken, refreshToken);
    }

    /**
     * Generates a new Access Token using a valid Refresh Token.
     */
    public AuthResponseDTO refresh(String refreshToken) {
        log.info("Processing token refresh request");

        // 1. Extract the user's email from the refresh token
        String email = jwtService.extractUsername(refreshToken);

        // 2. Load the user
        AppUser user = appUserRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found during refresh"));

        // 3. Validate the token signature and expiration
        if (!jwtService.isTokenValid(refreshToken, user)) {
            log.error("Refresh failed: Invalid or expired refresh token");
            throw new InvalidTokenException("Invalid refresh token");
        }

        // 4. Issue a new access token (keep the same refresh token)
        String newAccessToken = jwtService.generateToken(user);
        log.info("Tokens successfully refreshed for user: {}", email);

        return new AuthResponseDTO(newAccessToken, refreshToken);
    }
}
