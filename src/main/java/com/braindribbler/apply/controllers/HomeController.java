package com.braindribbler.apply.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

	@GetMapping("/")
	public String index(Model model) {
		model.addAttribute("location", "Home");
		model.addAttribute("title", "Welcome to your Job Search Hub!");
		return "index";
	}

	@GetMapping("/about")
	public String about(Model model) {
		model.addAttribute("location", "About");
		model.addAttribute("title", "About Brain Dribbler");
		return "about";
	}
}
