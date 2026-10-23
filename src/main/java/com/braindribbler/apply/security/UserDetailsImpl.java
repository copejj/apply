package com.braindribbler.apply.security;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.braindribbler.apply.models.users.User;

public class UserDetailsImpl implements UserDetails {
	private final User user;

	public UserDetailsImpl(User user) {
		this.user = user;
	}

	public Long getUserId() {
		return user.getUserId();
	}

	public User getUser() {
		return user;
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getUserName();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Arrays.stream(user.getRoles().split(","))
				.map(role -> (GrantedAuthority) () -> role.trim())
				.collect(Collectors.toList());
	}

	@Override
	public boolean isEnabled() {
		return user.getInactiveDate() == null;
	}

	// Usually return true for these unless you have specific logic
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}
}
