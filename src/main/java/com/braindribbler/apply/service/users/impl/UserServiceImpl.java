package com.braindribbler.apply.service.users.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.braindribbler.apply.dto.users.PasswordDTO;
import com.braindribbler.apply.dto.users.UserDTO;
import com.braindribbler.apply.forms.users.RegistrationForm;
import com.braindribbler.apply.models.users.User;
import com.braindribbler.apply.repositories.users.UserRepository;
import com.braindribbler.apply.service.MailgunEmailService;
import com.braindribbler.apply.service.users.UserService;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailgunEmailService mailgunEmailService;

    @Value("${app.api.base-url}")
    private String appBaseUrl;

    public UserServiceImpl(UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            MailgunEmailService mailgunEmailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailgunEmailService = mailgunEmailService;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserDtoById(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID must not be null");
        }
        return userRepository.findById(userId)
                .map(user -> new UserDTO(user.getUserId(),
                        user.getUserName(),
                        user.getEmail(),
                        user.getFirstName(),
                        user.getLastName(),
                        PasswordDTO.empty(),
                        user.isAdmin(),
                        user.isCanEdit(),
                        (user.getInactiveDate() == null && user.isVerified()), // Integrated verified flag to status
                                                                               // mapping
                        user.getInactiveDate()))
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    @Transactional
    public void updateUser(UserDTO dto, boolean isActualAdmin) {
        Long userId = dto.userId();
        if (userId == null) {
            throw new IllegalArgumentException("User ID must not be null");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setUserName(dto.userName());
        user.setEmail(dto.email());
        user.setFirstName(dto.firstName());
        user.setLastName(dto.lastName());

        if (isActualAdmin) {
            user.setAdmin(dto.isAdmin());
            user.setCanEdit(dto.canEdit());
            user.setInactiveDate(
                    (dto.isActive() ? null : (dto.inactiveDate() != null ? dto.inactiveDate() : LocalDateTime.now())));
        }

        String newPass = dto.passwordData().newPassword();
        if (newPass != null && !newPass.isEmpty()) {
            user.setPassword(passwordEncoder.encode(newPass));
        }

        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public boolean isUserNameAvailable(String userName) {
        return !userRepository.existsByUserName(userName);
    }

    @Override
    @Transactional
    public User registerNewUser(RegistrationForm form) {
        if (!isUserNameAvailable(form.getUserName())) {
            throw new RuntimeException("User name already taken");
        }

        User user = new User();
        user.setUserName(form.getUserName());
        user.setFirstName(form.getFirstName());
        user.setLastName(form.getLastName());
        user.setEmail(form.getEmail());

        user.setVerified(false);
        UUID verificationToken = UUID.randomUUID();
        user.setVerificationToken(verificationToken);

        String newPass = form.getPassword();
        if (newPass != null && !newPass.isEmpty()) {
            user.setPassword(passwordEncoder.encode(newPass));
        }

        User savedUser = userRepository.save(user);

        // Standardize the activation URL using your platform environment configurations
        // Ensures trailing slashes from your base-url string are handled safely
        String cleanBaseUrl = appBaseUrl.endsWith("/") ? appBaseUrl : appBaseUrl + "/";
        String activationUrl = cleanBaseUrl + "auth/verify?token=" + verificationToken;

        String emailBody = "Welcome to Brain Dribbler Apply! Please activate your account by clicking the link below:\n\n"
                + activationUrl;

        try {
            mailgunEmailService.sendEmail(savedUser.getEmail(), "Verify Your Account", emailBody);
        } catch (Exception e) {
            throw new RuntimeException("Account saved, but failed to dispatch confirmation email.", e);
        }

        return savedUser;
    }

    @Override
    @Transactional
    public boolean verifyUserToken(UUID token) {
        if (token == null) {
            return false;
        }

        return userRepository.findByVerificationToken(token)
                .map(user -> {
                    user.setVerified(true);
                    user.setVerificationToken(null); // Clear token once consumed
                    userRepository.save(user);
                    return true;
                })
                .orElse(false);
    }

    @Override
    @Transactional
    public void initiatePasswordReset(String email) {
        if (email == null || email.isBlank()) {
            return;
        }

        userRepository.findByEmail(email).ifPresent(user -> {
            UUID resetToken = UUID.randomUUID();
            user.setResetPasswordToken(resetToken);
            user.setResetPasswordExpiresAt(LocalDateTime.now().plusMinutes(30)); // 30-minute expiry window
            userRepository.save(user);

            String cleanBaseUrl = appBaseUrl.endsWith("/") ? appBaseUrl : appBaseUrl + "/";
            String resetUrl = cleanBaseUrl + "auth/reset-password?token=" + resetToken;

            String emailBody = "A password reset request was made for your Brain Dribbler Apply account. "
                    + "Click the link below to securely choose a new password. This link expires in 30 minutes:\n\n"
                    + resetUrl;

            try {
                mailgunEmailService.sendEmail(user.getEmail(), "Reset Your Password", emailBody);
            } catch (Exception e) {
                throw new RuntimeException("Failed to send password recovery email via Mailgun", e);
            }
        });
    }

}
