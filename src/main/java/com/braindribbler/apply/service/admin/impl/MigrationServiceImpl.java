package com.braindribbler.apply.service.admin.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.braindribbler.apply.models.admin.Migrations;
import com.braindribbler.apply.repositories.admin.MigrationRepository;
import com.braindribbler.apply.service.admin.MigrationService;

@Service
public class MigrationServiceImpl implements MigrationService {

	private final MigrationRepository migrationRepository;

	public MigrationServiceImpl(MigrationRepository migrationRepository) {
		this.migrationRepository = migrationRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Migrations> getAll() {
		return migrationRepository.findAll();
	}

}
