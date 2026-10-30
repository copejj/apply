package com.braindribbler.apply.controllers.users;

import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.braindribbler.apply.service.users.UserService;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/verify")
    public String verifyAccount(@RequestParam(value = "token", required = false) UUID token, Model model) {
        if (token == null) {
            model.addAttribute("error", "Invalid or missing registration token.");
            return "auth/verify-status";
        }

        boolean isVerified = userService.verifyUserToken(token);
        if (isVerified) {
            model.addAttribute("message", "Your account has been successfully verified! You can now log in.");
        } else {
            model.addAttribute("error", "The verification link is invalid or has already been used.");
        }

        return "auth/verify-status";
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, Model model) {
        try {
            userService.initiatePasswordReset(email);
            // We use a vague success message to prevent user enumeration security
            // vulnerabilities
            model.addAttribute("message",
                    "If that email address matches an active account, password recovery instructions have been sent.");
        } catch (Exception e) {
            model.addAttribute("error", "An error occurred while processing your request. Please try again.");
        }
        return "auth/forgot-password";
    }
}
