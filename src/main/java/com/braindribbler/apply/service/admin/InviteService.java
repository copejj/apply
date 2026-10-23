package com.braindribbler.apply.service.admin;

import java.util.List;

import com.braindribbler.apply.models.admin.Invite;

public interface InviteService {
	List<Invite> getAllInvites();

	Invite saveInvite(Invite invite);

	Invite getValidInvite(String key);

	void completeInvite(Invite invite, Long newUserId);

	void deleteInvite(Long id);

	boolean isEmailTaken(String email);
}
