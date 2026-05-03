package com.nabgha.digitalbanking.dtos.responses;

import java.time.LocalDateTime;

/**
 * Standardized API response wrapper.
 * 
 * @param success   Whether the request was successful.
 * @param message   Descriptive message (e.g., "Success", "Customer not found").
 * @param data      The payload. Can be null if the operation doesn't return data (e.g., DELETE).
 * @param timestamp Time of response (useful for frontend tracking).
 */
public record ApiResponse<T>(
    boolean success,
    String message,
    T data,
    LocalDateTime timestamp
) {
    // Success with data
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, message, data, LocalDateTime.now());
    }

    // Success without data (for simple confirmations like deletions)
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message, null, LocalDateTime.now());
    }

    // Error response
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null, LocalDateTime.now());
    }
}
