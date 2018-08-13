package net.explorviz.security.model;

import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Type;

@Type("authentication-token")
public class Token {

	@Id
	private final String id;

	private String token;

	public Token(final String id) {
		this.id = id;
	}

	public String getToken() {
		return token;
	}

	public void setToken(final String token) {
		this.token = token;
	}
}