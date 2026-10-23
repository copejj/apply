package com.braindribbler.apply.dto.users;

public record PasswordDTO(
		String newPassword,
		String confirmPassword) {
	public static PasswordDTO empty() {
		return new PasswordDTO("", "");
	}
}
