package net.explorviz.security.model;

import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Type;

@Type("user-credentials")
public class UserCredentials {

	@Id
	private String id;

	private String username;
	private String password;

	public UserCredentials() {

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
}