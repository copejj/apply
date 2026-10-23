package com.braindribbler.apply.models.companies;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Formula;

import com.braindribbler.apply.models.logs.Log;
import com.braindribbler.apply.models.users.User;
import com.fasterxml.jackson.annotation.JsonBackReference;

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
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "companies")
public class Company {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "company_id")
	private Long companyId;

	@CreationTimestamp
	@Column(name = "created_date", nullable = false, updatable = false)
	private LocalDateTime createdDate;

	@Column(name = "name")
	private String companyName;

	@Column(name = "email")
	private String companyEmail;

	@Column(name = "website")
	private String companyWebsite;

	@Transient
	private String companyUrl;

	@Column(name = "phone")
	private String companyPhone;

	@Column(name = "fax")
	private String companyFax;

	@Column(name = "user_id")
	private Long userId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", insertable = false, updatable = false)
	@JsonBackReference
	private User user;

	@OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CompanyAddress> companyAddresses = new ArrayList<>();

	@OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
	private List<Log> logs = new ArrayList<>();

	@Formula("(SELECT COUNT(*) FROM job_logs l WHERE l.company_id = company_id)")
	private int logCount;

	public int getLogCount() {
		return logCount;
	}

	public void addCompanyAddress(CompanyAddress companyAddress) {
		companyAddresses.add(companyAddress);
		companyAddress.setCompany(this); // Crucial for the foreign key!
	}

	public void removeCompanyAddress(CompanyAddress companyAddress) {
		companyAddresses.remove(companyAddress);
		companyAddress.setCompany(null);
	}

	private String formatUrl(String url) {
		if (!url.matches("^[a-zA-Z0-9]+://.*$")) {
			url = "https://" + url;
		}
		return url;
	}

	@PrePersist
	@PreUpdate
	private void initWebsite() {
		if (this.companyWebsite != null && !this.companyWebsite.isEmpty()) {
			this.companyWebsite = formatUrl(this.companyWebsite);
		}
	}

	@PostLoad
	public void initCompanyUrl() {
		try {
			initWebsite();
			if (!this.companyWebsite.isEmpty()) {
				URI uri = new URI(this.companyWebsite);
				this.companyUrl = uri.getScheme() + "://" + uri.getHost();
			}
		} catch (URISyntaxException ex) {
			this.companyUrl = "";
		}
	}

	public String getCompanyUrl() {
		initCompanyUrl();
		return companyUrl;
	}

	/* Getters and Setters */
	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCompanyEmail() {
		return companyEmail;
	}

	public void setCompanyEmail(String companyEmail) {
		this.companyEmail = companyEmail;
	}

	public String getCompanyWebsite() {
		return companyWebsite;
	}

	public void setCompanyWebsite(String companyWebsite) {
		this.companyWebsite = companyWebsite;
	}

	public String getCompanyPhone() {
		return companyPhone;
	}

	public void setCompanyPhone(String companyPhone) {
		this.companyPhone = companyPhone;
	}

	public String getCompanyFax() {
		return companyFax;
	}

	public void setCompanyFax(String companyFax) {
		this.companyFax = companyFax;
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

	public List<CompanyAddress> getCompanyAddresses() {
		return companyAddresses;
	}

	public void setCompanyAddresses(List<CompanyAddress> companyAddresses) {
		this.companyAddresses = companyAddresses;
	}
}
