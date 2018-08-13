package net.explorviz.security.services;

import javax.ws.rs.ForbiddenException;

import org.jvnet.hk2.annotations.Service;

import net.explorviz.security.model.User;
import net.explorviz.security.model.UserCredentials;

@Service
public class UserService {

	public User validateUserCredentials(final UserCredentials userCredentials) {
		if (userCredentials.getUsername().equals("admin") && userCredentials.getPassword().equals("password")) {
			return new User("admin");
		} else {
			throw new ForbiddenException("Wrong username or password");
		}
	}

}
