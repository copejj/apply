package com.braindribbler.apply.service.companies.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.braindribbler.apply.dto.companies.CompanyAddressDTO;
import com.braindribbler.apply.dto.companies.CompanyDTO;
import com.braindribbler.apply.forms.common.AddressFormItem;
import com.braindribbler.apply.forms.companies.CompanyForm;
import com.braindribbler.apply.models.common.Address;
import com.braindribbler.apply.models.common.AddressType;
import com.braindribbler.apply.models.common.State;
import com.braindribbler.apply.models.companies.Company;
import com.braindribbler.apply.models.companies.CompanyAddress;
import com.braindribbler.apply.repositories.common.AddressTypeRepository;
import com.braindribbler.apply.repositories.common.StateRepository;
import com.braindribbler.apply.repositories.companies.CompanyRepository;
import com.braindribbler.apply.service.companies.CompanyService;

@Service
public class CompanyServiceImpl implements CompanyService {

	private final CompanyRepository companyRepository;
	private final AddressTypeRepository addressTypeRepository;
	private final StateRepository stateRepository;

	public CompanyServiceImpl(CompanyRepository companyRepository, AddressTypeRepository addressTypeRepository,
			StateRepository stateRepository) {
		this.companyRepository = companyRepository;
		this.addressTypeRepository = addressTypeRepository;
		this.stateRepository = stateRepository;
	}

	public List<CompanyAddressDTO> getCompanyAddresses(Long companyId) {
		Company company = companyRepository.findByIdWithAddresses(companyId)
				.orElseThrow();

		return company.getCompanyAddresses().stream()
				.map(CompanyAddressDTO::fromEntity)
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public CompanyDTO getCompanyDTOById(Long companyId) {
		Company company = companyRepository.findByCompanyId(companyId)
				.orElseThrow(() -> new RuntimeException("Company not found"));

		List<CompanyAddressDTO> addressDTOs = company.getCompanyAddresses().stream()
				.map(CompanyAddressDTO::fromEntity).toList();

		return new CompanyDTO(company.getCompanyId(),
				company.getCompanyName(),
				company.getCompanyEmail(),
				company.getCompanyWebsite(),
				company.getCompanyPhone(),
				company.getCompanyFax(),
				company.getUserId(),
				addressDTOs);
	}

	@Override
	@Transactional
	public Long createCompany(CompanyForm form, Long userId) {
		Company company = new Company();
		company.setUserId(userId); // Assuming new companies need an owner

		mapFormToEntity(form, company);
		Company saved = companyRepository.save(company);
		return saved.getCompanyId();
	}

	@Override
	@Transactional
	public void updateCompany(CompanyForm form) {
		Company company = companyRepository.findByIdWithAddresses(form.getCompanyId())
				.orElseThrow(() -> new RuntimeException("Company not found"));

		// Crucial for updates: clear old relations before re-adding
		company.getCompanyAddresses().clear();

		mapFormToEntity(form, company);
		companyRepository.save(company);
	}

	private void mapFormToEntity(CompanyForm form, Company company) {
		company.setCompanyName(form.getCompanyName());
		company.setCompanyEmail(form.getCompanyEmail());
		company.setCompanyWebsite(form.getCompanyWebsite());
		company.setCompanyPhone(form.getCompanyPhone());
		company.setCompanyFax(form.getCompanyFax());

		form.getAddresses().forEach(formItem -> {
			if (formItem.getStreet() == null || formItem.getStreet().isBlank()) {
				return;
			}

			Address address = new Address();
			address.setStreet(formItem.getStreet());
			address.setStreetExt(formItem.getStreetExt());
			address.setCity(formItem.getCity());
			address.setZip(formItem.getZip());

			State state = stateRepository.findByAbbr(formItem.getStateAbbr())
					.orElseThrow(() -> new RuntimeException("State not found"));
			address.setState(state);

			CompanyAddress join = new CompanyAddress();
			join.setAddress(address);

			AddressType type = addressTypeRepository.findByName(formItem.getType())
					.orElseThrow(() -> new RuntimeException("Type not found"));
			join.setAddressType(type);

			company.addCompanyAddress(join);
		});
	}

	@Override
	public CompanyForm convertToForm(CompanyDTO dto) {
		CompanyForm form = new CompanyForm();

		// 1. Map top-level Company fields
		form.setCompanyId(dto.companyId());
		form.setCompanyName(dto.companyName());
		form.setCompanyEmail(dto.companyEmail());
		form.setCompanyWebsite(dto.companyWebsite());
		form.setCompanyPhone(dto.companyPhone());
		form.setCompanyFax(dto.companyFax());

		// 2. Map the nested Address Records to Address Form POJOs
		List<AddressFormItem> formAddresses = dto.addresses().stream()
				.map(addrDto -> {
					AddressFormItem item = new AddressFormItem();
					item.setType(addrDto.type());
					item.setStreet(addrDto.street());
					item.setStreetExt(addrDto.streetExt());
					item.setCity(addrDto.city());
					item.setStateAbbr(addrDto.stateAbbr());
					item.setZip(addrDto.zip());
					return item;
				}).collect(Collectors.toList());

		form.setAddresses(formAddresses);
		return form;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Company> getAll(Long userId) {
		return companyRepository.findAllByUserIdOrderByCompanyNameAsc(userId);
	}
}
