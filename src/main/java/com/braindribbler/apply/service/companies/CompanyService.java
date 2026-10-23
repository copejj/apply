package com.braindribbler.apply.service.companies;

import java.util.List;

import com.braindribbler.apply.dto.companies.CompanyDTO;
import com.braindribbler.apply.forms.companies.CompanyForm;
import com.braindribbler.apply.models.companies.Company;

public interface CompanyService {
	CompanyDTO getCompanyDTOById(Long companyId);

	Long createCompany(CompanyForm form, Long userId);

	void updateCompany(CompanyForm form);

	CompanyForm convertToForm(CompanyDTO dto);

	List<Company> getAll(Long userId);
}
