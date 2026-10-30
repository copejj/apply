package com.braindribbler.apply.repositories.users;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.braindribbler.apply.models.users.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUserName(String userName);

	Optional<User> findByEmail(String email);

	Optional<User> findByUserId(Long userId);

	boolean existsByUserName(String userName);

	boolean existsByEmail(String email);

	List<User> findByFirstNameContaining(String keyword, Sort sort);

	// Derived query additions to look up users via incoming Mailgun link tokens
	Optional<User> findByVerificationToken(UUID verificationToken);

	Optional<User> findByResetPasswordToken(UUID resetPasswordToken);
}
