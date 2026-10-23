package com.braindribbler.apply.service.admin;

import java.util.List;

import com.braindribbler.apply.models.admin.Migrations;

public interface MigrationService {
	List<Migrations> getAll();
}
