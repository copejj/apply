package com.braindribbler.apply.dto.logs;

import java.time.LocalDate;

public record WeekDTO(
		Long weekId,
		LocalDate startDate,
		LocalDate endDate,
		Integer logCounts) {
}
