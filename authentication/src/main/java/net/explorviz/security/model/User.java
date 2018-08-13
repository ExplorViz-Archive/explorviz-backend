package net.explorviz.security.model;

import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Type;

/**
 * Model representing a user within our system
 */
@Type("user")
public class User {

	@Id
	private String id;

	private String username;
	private String password;
	private String hashedPassword;
	private String token;
	private boolean authenticated;

	public User(final String id) {
		this.id = id;
	}

	public User(final String username, final String hashedPassword) {
		this.username = username;
		this.hashedPassword = hashedPassword;
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

	public String getHashedPassword() {
		return hashedPassword;
	}

	public void setHashedPassword(final String hashedPassword) {
		this.hashedPassword = hashedPassword;
	}

	public String getToken() {
		return token;
	}

	public void setToken(final String token) {
		this.token = token;
	}

	public boolean isAuthenticated() {
		return authenticated;
	}

	public void setAuthenticated(final boolean authenticated) {
		this.authenticated = authenticated;
	}

}