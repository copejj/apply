package com.braindribbler.apply.models.logs;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "job_log_statuses")
public class LogStatus {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "job_log_status_id")
	private Long logStatusId;

	@CreationTimestamp
	@Column(name = "status_date", nullable = false, updatable = false)
	private LocalDateTime statusDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "job_log_id")
	@JsonBackReference
	private Log log;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "status_id")
	private Status status;

	public Long getLogStatusId() {
		return logStatusId;
	}

	public void setLogStatusId(Long logStatusId) {
		this.logStatusId = logStatusId;
	}

	public LocalDateTime getStatusDate() {
		return statusDate;
	}

	public void setStatusDate(LocalDateTime statusDate) {
		this.statusDate = statusDate;
	}

	public Log getLog() {
		return log;
	}

	public void setLog(Log log) {
		this.log = log;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
}
