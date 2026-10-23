package com.braindribbler.apply.controllers.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.braindribbler.apply.models.admin.Invite;
import com.braindribbler.apply.service.admin.InviteService;

@Controller
@RequestMapping("/admin/invite")
public class InviteController {
	private final InviteService inviteService;

	public InviteController(InviteService inviteService) {
		this.inviteService = inviteService;
	}

	@GetMapping
	public String listInvites(Model model) {
		model.addAttribute("title", "Invites List");
		model.addAttribute("location", "Admin: Invites");
		model.addAttribute("invites", inviteService.getAllInvites());
		model.addAttribute("newInvite", new Invite());

		return "admin/invites/list";
	}

	@PostMapping("/save")
	public String saveInvite(@ModelAttribute("newInvite") Invite invite, BindingResult result, Model model) {

		if (inviteService.isEmailTaken(invite.getEmail())) {
			result.rejectValue("email", "error.invite", "This email is already in use by a user or pending invite.");
		}

		if (result.hasErrors()) {
			model.addAttribute("invites", inviteService.getAllInvites());
			return "admin/invites/list";
		}

		inviteService.saveInvite(invite);
		return "redirect:/admin/invite";
	}

	@PostMapping("/delete/{id}")
	public String deleteInvite(@PathVariable Long id) {
		inviteService.deleteInvite(id);
		return "redirect:/admin/invite";
	}
}
