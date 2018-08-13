package net.explorviz.security.model;

import java.security.Principal;
import java.util.List;

public final class AuthenticatedUserDetails implements Principal {

	private final String username;
	private final List<String> roles;

	public AuthenticatedUserDetails(final String username, final List<String> roles) {
		this.username = username;
		this.roles = roles;
	}

	public List<String> getRoles() {
		return roles;
	}

	@Override
	public String getName() {
		return username;
	}
}