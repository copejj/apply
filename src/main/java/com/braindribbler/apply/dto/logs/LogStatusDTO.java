package com.braindribbler.apply.dto.logs;

import java.time.LocalDateTime;

public record LogStatusDTO(
		Long logStatusId,
		LocalDateTime statusDate,
		String status,
		Long statusId) {
}
