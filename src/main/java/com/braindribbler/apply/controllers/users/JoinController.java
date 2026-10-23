package com.braindribbler.apply.controllers.users;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.braindribbler.apply.forms.users.RegistrationForm;
import com.braindribbler.apply.models.admin.Invite;
import com.braindribbler.apply.models.users.User;
import com.braindribbler.apply.service.admin.InviteService;
import com.braindribbler.apply.service.users.UserService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/join") // Publicly accessible prefix
public class JoinController {

    private final InviteService inviteService;
    private final UserService userService;

    public JoinController(InviteService inviteService, UserService userService) {
        this.inviteService = inviteService;
        this.userService = userService;
    }

    @GetMapping("/verify")
    public String showRegistrationForm(@RequestParam("key") String key, Model model) {
        Invite invite = inviteService.getValidInvite(key);
        if (invite == null) {
            return "redirect:/login?error=invalid-invite";
        }

        RegistrationForm form = new RegistrationForm();
        form.setKey(invite.getKey());
        form.setFirstName(invite.getFirstName());
        form.setLastName(invite.getLastName());
        form.setEmail(invite.getEmail());

        model.addAttribute("registrationForm", form);
        return "public/register";
    }

    @PostMapping("/complete")
    public String registerUser(@Valid @ModelAttribute("registrationForm") RegistrationForm form, BindingResult result) {
        if (!userService.isUserNameAvailable(form.getUserName())) {
            result.rejectValue("userName", "error.registrationForm", "User name already taken");
        }

        if (!form.getPassword().equals(form.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "error.registrationForm", "Passwords do not match");
        }

        if (result.hasErrors()) {
            return "public/register";
        }

        User newUser = userService.registerNewUser(form);
        Invite invite = inviteService.getValidInvite(form.getKey());
        inviteService.completeInvite(invite, newUser.getUserId());

        return "redirect:/login?registered=true";
    }
}
