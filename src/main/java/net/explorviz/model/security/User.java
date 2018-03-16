package net.explorviz.model.security;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.jasminb.jsonapi.annotations.Type;

import net.explorviz.model.helper.BaseEntity;

/**
 * Model representing a user within our system
 *
 * @author Christian Zirkelbach (czi@informatik.uni-kiel.de)
 *
 */
@SuppressWarnings("serial")
@Entity(name = "USERS")
@Table(name = "USERS")
@Type("user")
@JsonIgnoreProperties(ignoreUnknown = true)
public class User extends BaseEntity {

	@Id
	private String username;
	private String password;
	private String hashedPassword;
	private String token;
	private boolean authenticated;

	public User(final Long id) {
		this.setId(id);
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