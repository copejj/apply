package com.braindribbler.apply.controllers.admin;

import java.util.List;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.braindribbler.apply.models.admin.Migrations;
import com.braindribbler.apply.service.admin.MigrationService;

@Controller
@RequestMapping("/admin/migrations")
public class MigrationController {
	private final MigrationService migrationService;

	public MigrationController(MigrationService migrationService) {
		this.migrationService = migrationService;
	}

	@GetMapping
	@Secured("ROLE_ADMIN")
	public String getAllMigrations(Model model) {
		List<Migrations> migrations = migrationService.getAll();

		model.addAttribute("location", "Admin: Migrations");
		model.addAttribute("title", " Migrations");

		model.addAttribute("migrations", migrations);
		return "admin/migrations/list";
	}

}
