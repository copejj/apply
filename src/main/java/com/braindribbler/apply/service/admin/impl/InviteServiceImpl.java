package com.braindribbler.apply.service.admin.impl;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.braindribbler.apply.models.admin.Invite;
import com.braindribbler.apply.repositories.admin.InviteRepository;
import com.braindribbler.apply.repositories.users.UserRepository;
import com.braindribbler.apply.service.admin.InviteService;

import jakarta.transaction.Transactional;

@Service
public class InviteServiceImpl implements InviteService {
	private final InviteRepository inviteRepository;
	private final UserRepository userRepository;

	public InviteServiceImpl(InviteRepository inviteRepository, UserRepository userRepository) {
		this.inviteRepository = inviteRepository;
		this.userRepository = userRepository;
	}

	@Override
	public List<Invite> getAllInvites() {
		return inviteRepository.findAll();
	}

	@Override
	@Transactional
	@PreAuthorize("hasRole('ADMIN')")
	public Invite saveInvite(Invite invite) {
		if (invite == null) {
			throw new IllegalArgumentException("Invite cannot be null");
		}

		String email = invite.getEmail();

		// 1. Check if they are already a User
		if (userRepository.existsByEmail(email)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists with this email.");
		}

		// 2. Check if an Invite is already pending
		if (inviteRepository.existsByEmail(email)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "An invite has already been sent to this email.");
		}

		return inviteRepository.save(invite);
	}

	@Override
	@Transactional
	@PreAuthorize("hasRole('ADMIN')")
	public void deleteInvite(Long inviteId) {
		if (inviteId != null) {
			inviteRepository.deleteById(inviteId);
		}
	}

	@Override
	public Invite getValidInvite(String key) {
		Invite invite = inviteRepository.findByKey(key);
		// Only return if it exists AND hasn't been used (userId is null)
		if (invite == null || invite.getUserId() != null) {
			return null;
		}
		return invite;
	}

	@Override
	public void completeInvite(Invite invite, Long newUserId) {
		invite.setUserId(newUserId);
		inviteRepository.save(invite);
	}

	@Override
	public boolean isEmailTaken(String email) {
		return userRepository.existsByEmail(email) || inviteRepository.existsByEmail(email);
	}
}