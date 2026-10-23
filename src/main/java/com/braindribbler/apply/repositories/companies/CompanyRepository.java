package com.braindribbler.apply.repositories.companies;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.braindribbler.apply.models.companies.Company;

public interface CompanyRepository extends JpaRepository<Company, Long> {
	Optional<Company> findByCompanyId(Long companyId);

	List<Company> findAllByUserIdOrderByCompanyNameAsc(Long userId);

	List<Company> findByUserId(Long userId);

	@Query("SELECT c FROM Company c " +
			"LEFT JOIN FETCH c.companyAddresses ca " +
			"LEFT JOIN FETCH ca.address a " +
			"LEFT JOIN FETCH a.state " +
			"LEFT JOIN FETCH ca.addressType " +
			"WHERE c.companyId = :id")
	Optional<Company> findByIdWithAddresses(@Param("id") Long id);
}
