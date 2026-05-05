package com.nabgha.digitalbanking.dtos.responses;

import com.nabgha.digitalbanking.enums.Role;
import java.util.UUID;

public record UserResponseDTO(
        UUID id,
        String email,
        String firstName,
        String lastName,
        Role role,
        boolean enabled
) {
}
