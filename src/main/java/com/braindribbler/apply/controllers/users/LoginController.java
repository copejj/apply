package com.braindribbler.apply.controllers.users;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

	@GetMapping("/login")
	public String login(Model model) {
		model.addAttribute("location", "Login");

		return "admin/users/login";
	}

	@GetMapping("/logout")
	public String dashboard(Model model) {
		model.addAttribute("location", "Logout");

		return "admin/users/logout";
	}
}
