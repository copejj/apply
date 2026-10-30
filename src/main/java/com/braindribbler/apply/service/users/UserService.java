package com.braindribbler.apply.service.users;

import java.util.List;
import java.util.UUID;

import com.braindribbler.apply.dto.users.UserDTO;
import com.braindribbler.apply.forms.users.RegistrationForm;
import com.braindribbler.apply.models.users.User;

public interface UserService {
    UserDTO getUserDtoById(Long userId);

    void updateUser(UserDTO dto, boolean isActualAdmin);

    List<User> getAllUsers();

    User registerNewUser(RegistrationForm form);

    boolean isUserNameAvailable(String userName);

    // New additions to support the Mailgun authentication lifecycle
    boolean verifyUserToken(UUID token);

    void initiatePasswordReset(String email);
}
