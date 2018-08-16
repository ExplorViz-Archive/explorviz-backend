package net.explorviz.security.services;

import javax.ws.rs.ForbiddenException;

import org.jvnet.hk2.annotations.Service;

import net.explorviz.security.model.UserCredentials;
import net.explorviz.shared.security.User;

@Service
public class UserService {

	public User validateUserCredentials(final UserCredentials userCredentials) {

		if (!checkIfDataIsNotNull(userCredentials)) {
			new ForbiddenException("Enter username and password");
		}

		// TODO valid DB query
		if (userCredentials.getUsername().equals("admin") && userCredentials.getPassword().equals("password")) {

			final User user = new User("admin");

			// For Testing,
			user.getRoles().add("admin");

			return user;
		} else {
			throw new ForbiddenException("Wrong username or password");
		}
	}

	private boolean checkIfDataIsNotNull(final UserCredentials credentials) {
		if (credentials != null && credentials.getUsername() != null && credentials.getPassword() != null) {
			return true;
		}

		return false;
	}

}
