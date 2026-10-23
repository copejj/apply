package com.braindribbler.apply.controllers.admin;

import java.util.List;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.braindribbler.apply.dto.users.UserDTO;
import com.braindribbler.apply.models.users.User;
import com.braindribbler.apply.service.users.UserService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/users")
public class UserController {
	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping("/edit/{userId}")
	@PreAuthorize("hasRole('ADMIN') or #userId == principal.user.userId")
	public String getUserById(@PathVariable Long userId, Model model, Authentication authentication) {
		boolean isAdmin = authentication.getAuthorities().stream()
				.anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

		UserDTO userDto = userService.getUserDtoById(userId);

		model.addAttribute("user", userDto);
		model.addAttribute("isAdmin", isAdmin);
		model.addAttribute("location", "Edit User");
		model.addAttribute("title", "User Information");

		return "admin/users/edit";
	}

	@PutMapping
	@PreAuthorize("hasRole('ADMIN') or #userDto.userId == principal.user.userId")
	public String updateUser(@Valid @ModelAttribute("userDto") UserDTO userDto,
			BindingResult result,
			Model model,
			RedirectAttributes ra,
			Authentication authentication) {

		String newPassword = userDto.passwordData().newPassword();
		String confirmPassword = userDto.passwordData().confirmPassword();
		if (newPassword != null && !newPassword.isEmpty() && !newPassword.equals(confirmPassword)) {
			model.addAttribute("saveError", "Passwords do not match.");
		} else if (result.hasErrors()) {
			model.addAttribute("saveError", "Save failed");
		}

		if (model.containsAttribute("saveError")) {
			return getUserById(userDto.userId(), model, authentication);
		}

		boolean isActualAdmin = authentication.getAuthorities().stream()
				.anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

		// Pass the 'isActualAdmin' flag to the service so it knows whether
		// to ignore or apply the sensitive fields
		userService.updateUser(userDto, isActualAdmin);

		ra.addFlashAttribute("saveSuccess", "User information updated successfully.");

		return "redirect:/admin/users/edit/" + userDto.userId();
	}

	@GetMapping
	@Secured("ROLE_ADMIN")
	public String getAll(Model model) {

		List<User> users = userService.getAllUsers();

		model.addAttribute("location", "Admin: Users");
		model.addAttribute("title", "User List");

		model.addAttribute("users", users);

		return "admin/users/list";
	}
}
