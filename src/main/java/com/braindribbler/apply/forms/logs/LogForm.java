package com.braindribbler.apply.forms.logs;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.braindribbler.apply.dto.logs.LogStatusDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class LogForm {
	private Long logId;

	@NotBlank(message = "Job title is required")
	private String title;

	private String jobNumber;
	private String nextStep;
	private String jobLink;
	private String description;
	private String notes;
	private String confirmation;
	private String contact;
	private String contactNumber;

	@NotNull(message = "Please select a company")
	private Long companyId;

	@NotNull(message = "Action date is required")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate actionDate = LocalDate.now();

	private Long latestStatusId;
	private LogStatusDTO latestStatus;
	private List<LogStatusDTO> logStatuses;

	public Long getLogId() {
		return logId;
	}

	public void setLogId(Long logId) {
		this.logId = logId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getJobNumber() {
		return jobNumber;
	}

	public void setJobNumber(String jobNumber) {
		this.jobNumber = jobNumber;
	}

	public String getNextStep() {
		return nextStep;
	}

	public void setNextStep(String nextStep) {
		this.nextStep = nextStep;
	}

	public String getJobLink() {
		return jobLink;
	}

	public void setJobLink(String jobLink) {
		this.jobLink = jobLink;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getConfirmation() {
		return confirmation;
	}

	public void setConfirmation(String confirmation) {
		this.confirmation = confirmation;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public LocalDate getActionDate() {
		return actionDate;
	}

	public void setActionDate(LocalDate actionDate) {
		this.actionDate = actionDate;
	}

	public Long getLatestStatusId() {
		return latestStatusId;
	}

	public void setLatestStatusId(Long latestStatusId) {
		this.latestStatusId = latestStatusId;
	}

	public List<LogStatusDTO> getLogStatuses() {
		return logStatuses;
	}

	public void setLogStatuses(List<LogStatusDTO> logStatuses) {
		this.logStatuses = logStatuses;
	}

	public LogStatusDTO getLatestStatus() {
		return latestStatus;
	}

	public void setLatestStatus(LogStatusDTO latestStatus) {
		this.latestStatus = latestStatus;
	}
}
