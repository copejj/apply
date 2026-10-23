package com.braindribbler.spring.service.users.impl;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.braindribbler.spring.models.users.User;
import com.braindribbler.spring.repositories.users.UserRepository;
import com.braindribbler.spring.security.UserDetailsImpl;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	private UserRepository userRepository;

	public UserDetailsServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// Fetch user from the database
		// Return the security wrapper
		User user = userRepository.findByUserName(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
		return new UserDetailsImpl(user);
	}
}