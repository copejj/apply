package com.braindribbler.apply.models.logs;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.JoinFormula;

import com.braindribbler.apply.models.companies.Company;
import com.braindribbler.apply.models.users.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

@Entity
@Table(name = "job_logs")
public class Log {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "job_log_id")
	private Long logId;

	@CreationTimestamp
	@Column(name = "created_date", nullable = false, updatable = false)
	private LocalDateTime createdDate;

	@Column(name = "action_date")
	private LocalDate actionDate;

	@Column(name = "title")
	private String title;

	@Column(name = "job_number")
	private String jobNumber;

	@Column(name = "next_step")
	private String nextStep;

	@Column(name = "job_link")
	private String jobLink;

	@Column(name = "description", columnDefinition = "TEXT")
	private String description;

	@Column(name = "notes", columnDefinition = "TEXT")
	private String notes;

	@Column(name = "confirmation")
	private String confirmation;

	@Column(name = "contact")
	private String contact;

	@Column(name = "contact_number")
	private String contactNumber;

	@Column(name = "user_id")
	private Long userId;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", insertable = false, updatable = false)
	@JsonBackReference
	private User user;

	@Column(name = "week_id")
	private Long weekId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "week_id", insertable = false, updatable = false)
	@JsonBackReference
	private Week week;

	@Column(name = "company_id")
	private Long companyId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "company_id", insertable = false, updatable = false)
	@JsonBackReference
	private Company company;

	@OneToMany(mappedBy = "log", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@OrderBy("statusDate DESC")
	@JsonManagedReference
	private List<LogStatus> logStatuses;

	@Formula("(select ls.status_id from job_log_statuses ls where ls.job_log_id = {alias}.job_log_id order by ls.status_date desc limit 1)")
	private Long latestStatusId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinFormula("(SELECT ls.job_log_status_id FROM job_log_statuses ls WHERE ls.job_log_id = {alias}.job_log_id ORDER BY ls.status_date DESC LIMIT 1)")
	private LogStatus latestStatus;

	public Long getLatestStatusId() {
		return latestStatusId;
	}

	public LogStatus getLatestStatus() {
		return latestStatus;
	}

	public Long getLogId() {
		return logId;
	}

	public void setLogId(Long logId) {
		this.logId = logId;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public LocalDate getActionDate() {
		return actionDate;
	}

	public void setActionDate(LocalDate actionDate) {
		this.actionDate = actionDate;
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

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Long getWeekId() {
		return weekId;
	}

	public void setWeekId(Long weekId) {
		this.weekId = weekId;
	}

	public Week getWeek() {
		return week;
	}

	public void setWeek(Week week) {
		this.week = week;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public List<LogStatus> getLogStatuses() {
		return logStatuses;
	}

	public void setLogStatuses(List<LogStatus> logStatuses) {
		this.logStatuses = logStatuses;
	}

}
