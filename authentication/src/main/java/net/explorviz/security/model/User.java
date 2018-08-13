package net.explorviz.security.model;

import java.util.ArrayList;
import java.util.List;

public class User {

	private String username;
	private String password;

	private List<String> roles = new ArrayList<>();

	public User(final String username) {
		this.username = username;
		// For Testing
		roles.add("admin");
	}

	public User() {
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(final String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(final String password) {
		this.password = password;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(final List<String> roles) {
		this.roles = roles;
	}
}