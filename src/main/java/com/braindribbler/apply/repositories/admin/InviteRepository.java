package com.braindribbler.apply.repositories.admin;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.braindribbler.apply.models.admin.Invite;

public interface InviteRepository extends JpaRepository<Invite, Long> {
	Optional<Invite> findByInviteId(Long inviteId);

	Invite findByKey(String key);

	boolean existsByEmail(String email);
}
