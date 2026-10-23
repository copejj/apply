package com.braindribbler.apply.dto.admin;

import java.time.LocalDateTime;

public record SchemaMigrationsDTO(
		Long schemaMigrationId,
		String filename,
		String status,
		LocalDateTime startedAt,
		LocalDateTime endedAt,
		String errorMessage) {
}
