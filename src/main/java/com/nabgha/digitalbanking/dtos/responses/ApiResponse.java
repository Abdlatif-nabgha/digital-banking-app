package com.nabgha.digitalbanking.dtos.responses;

<<<<<<< HEAD

import java.time.LocalDateTime;

public record ApiResponse<T>(
        boolean success,
        String message,
        T data,
        LocalDateTime timestamp
)
{
=======
import java.time.LocalDateTime;

/**
 * Standardized API response wrapper.
 */
public record ApiResponse<T>(
    boolean success,
    String message,
    T data,
    LocalDateTime timestamp
) {
>>>>>>> main
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, message, data, LocalDateTime.now());
    }

<<<<<<< HEAD
=======
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message, null, LocalDateTime.now());
    }

>>>>>>> main
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null, LocalDateTime.now());
    }
}
