package com.braindribbler.apply.forms.companies;

import java.util.ArrayList;
import java.util.List;

import com.braindribbler.apply.forms.common.AddressFormItem;
import com.braindribbler.apply.models.users.User;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class CompanyForm {
	private Long companyId;

	@NotBlank(message = "Company Name cannot be empty")
	private String companyName;

	@Email(message = "Invalid email format")
	private String companyEmail;
	private String companyWebsite;
	private String companyUrl;
	private String companyPhone;
	private String companyFax;
	private Long userId;
	private User user;

	@Valid
	private List<AddressFormItem> addresses = new ArrayList<>();

	// Standard Getters/Setters
	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
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

	public String getCompanyUrl() {
		return companyUrl;
	}

	public void setCompanyUrl(String companyUrl) {
		this.companyUrl = companyUrl;
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

	public List<AddressFormItem> getAddresses() {
		return addresses;
	}

	public void setAddresses(List<AddressFormItem> addresses) {
		this.addresses = addresses;
	}
}
