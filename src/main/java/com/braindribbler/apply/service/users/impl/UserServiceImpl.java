package com.braindribbler.apply.service.users.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.braindribbler.apply.dto.users.PasswordDTO;
import com.braindribbler.apply.dto.users.UserDTO;
import com.braindribbler.apply.forms.users.RegistrationForm;
import com.braindribbler.apply.models.users.User;
import com.braindribbler.apply.repositories.users.UserRepository;
import com.braindribbler.apply.service.users.UserService;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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
                        (user.getInactiveDate() == null),
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

        // Only update these if the person performing the action is an Admin
        if (isActualAdmin) {
            user.setAdmin(dto.isAdmin());
            user.setCanEdit(dto.canEdit());
            // if isActive is true, make sure the inactive date is null, otherwise set the
            // set date or the now date
            user.setInactiveDate(
                    (dto.isActive() ? null : (dto.inactiveDate() != null ? dto.inactiveDate() : LocalDateTime.now())));
        }

        String newPass = dto.passwordData().newPassword();
        if (newPass != null && !newPass.isEmpty()) {
            // Hashing happens here in the Service Layer
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
    public User registerNewUser(RegistrationForm form) {
        // Double check here just in case
        if (!isUserNameAvailable(form.getUserName())) {
            throw new RuntimeException("User name already taken");
        }

        User user = new User();
        user.setUserName(form.getUserName());
        user.setFirstName(form.getFirstName());
        user.setLastName(form.getLastName());
        user.setEmail(form.getEmail());

        String newPass = form.getPassword();
        if (newPass != null && !newPass.isEmpty()) {
            // Hashing happens here in the Service Layer
            user.setPassword(passwordEncoder.encode(newPass));
        }

        return userRepository.save(user);
    }
}
