package com.braindribbler.apply.dto.users;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserDTO(
        Long userId,
        @NotBlank(message = "User name is required") String userName,
        @Email(message = "Email should be valid") @NotBlank(message = "Email is required") String email,
        String firstName,
        String lastName,
        PasswordDTO passwordData,

        boolean isAdmin,
        boolean canEdit,
        boolean isActive,

        LocalDateTime inactiveDate) {
}
