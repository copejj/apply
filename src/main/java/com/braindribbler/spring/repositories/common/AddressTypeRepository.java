package com.braindribbler.spring.repositories.common;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.braindribbler.spring.models.common.AddressType;

public interface AddressTypeRepository extends JpaRepository<AddressType, Integer> {
	Optional<AddressType> findByName(String name);
}
