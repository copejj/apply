package com.braindribbler.apply.dto.logs;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.braindribbler.apply.dto.companies.CompanyAddressDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LogDTO(
		Long logId,
		LocalDateTime createdDate,

		@NotNull Long userId,

		@NotBlank(message = "Job title is required") String title,
		String jobNumber,
		String nextStep,
		String jobLink,
		String description,
		String notes,
		String confirmation,
		String contact,
		String contactNumber,

		Long companyId,
		String companyName,

		String companyEmail,
		String companyWebsite,
		CompanyAddressDTO companyAddress,

		@DateTimeFormat(pattern = "yyyy-MM-dd") @NotNull(message = "Action date is required") LocalDate actionDate,
		Long weekId,
		LocalDate startDate,
		LocalDate endDate,
		Long latestStatusId,
		LogStatusDTO latestStatus,
		List<LogStatusDTO> logStatuses) {
}
