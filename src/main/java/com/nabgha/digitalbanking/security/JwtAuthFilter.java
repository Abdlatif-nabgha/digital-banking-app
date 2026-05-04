package com.nabgha.digitalbanking.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter that intercepts every HTTP request to check for a JWT token in the Authorization header.
 * It extends OncePerRequestFilter to ensure it's executed exactly once per request.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Get the Authorization header from the request
        final String authHeader = request.getHeader("Authorization");

        // 2. If no header or doesn't start with "Bearer ", skip this filter and continue the chain
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extract the JWT token (everything after "Bearer ")
        final String jwt = authHeader.substring(7);
        final String email;

        try {
            // 4. Extract the user email from the token
            email = jwtService.extractUsername(jwt);
        } catch (Exception e) {
            // If token is malformed or invalid, continue without authentication
            filterChain.doFilter(request, response);
            return;
        }

        // 5. If we have an email and the user is not already authenticated in the current SecurityContext
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            // 6. Load user details from the database
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            // 7. Check if the token is still valid (correct user and not expired)
            if (jwtService.isTokenValid(jwt, userDetails)) {
                
                // 8. Create an Authentication token for Spring Security
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                // 9. Enrich the authentication token with request details (IP, Session ID, etc.)
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 10. Set the user as authenticated in the Security Context
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 11. Continue with the next filter in the chain
        filterChain.doFilter(request, response);
    }
}
