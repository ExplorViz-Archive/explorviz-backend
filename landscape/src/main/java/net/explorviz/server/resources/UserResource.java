package net.explorviz.server.resources;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.Random;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jasminb.jsonapi.JSONAPIDocument;
import com.github.jasminb.jsonapi.ResourceConverter;
import com.github.jasminb.jsonapi.exceptions.DocumentSerializationException;
import com.github.jasminb.jsonapi.models.errors.Error;

import net.explorviz.model.security.User;
import net.explorviz.server.security.HibernateSessionFactory;
import net.explorviz.server.security.PasswordStorage;

/**
 * REST resource providing user data for the frontend
 */
@Path("users")
public class UserResource {

	private static Logger logger = LoggerFactory.getLogger(UserResource.class.getName());

	private final HibernateSessionFactory sessionFactory;
	private final ResourceConverter converter;

	@Inject
	public UserResource(final HibernateSessionFactory sessionFactory, final ResourceConverter converter) {
		this.sessionFactory = sessionFactory;
		this.converter = converter;
	}

	@GET
	@Produces(value = "application/vnd.api+json")
	public Response getUser(@QueryParam("username") final String username) {
		User currentUser = null;

		// retrieve user
		final Session session = sessionFactory.beginTransaction();
		currentUser = session.find(new User().getClass(), username);

		sessionFactory.commitTransactionAndClose(session);

		if (currentUser != null) {
			currentUser.initializeID();
			currentUser.setToken(null);
			currentUser.setPassword(null);
			currentUser.setAuthenticated(false);
			currentUser.setHashedPassword(null);

			final JSONAPIDocument<User> document = new JSONAPIDocument<User>(currentUser);

			try {
				return Response.ok(this.converter.writeDocument(document)).type("application/vnd.api+json").build();
			} catch (final DocumentSerializationException e) {
				logger.debug(e.getMessage());
				return getAuthenticationErrorResponse();
			}
		} else {
			return getAuthenticationErrorResponse();
		}
	}

	@PATCH
	@Path("authenticate")
	public Response authenticateUser(final User userToAuthenticate) {

		final User possibleUser = authenticate(userToAuthenticate);

		if (possibleUser != null) {

			final String token = issueToken();
			possibleUser.setToken(token);
			possibleUser.setPassword(null);
			possibleUser.setId(userToAuthenticate.getId());
			possibleUser.setAuthenticated(true);

			final Session session = sessionFactory.beginTransaction();
			session.update(possibleUser);
			sessionFactory.commitTransactionAndClose(session);

			final JSONAPIDocument<User> document = new JSONAPIDocument<User>(possibleUser);

			try {
				return Response.ok(this.converter.writeDocument(document)).type("application/vnd.api+json").build();
			} catch (final DocumentSerializationException e) {
				logger.debug(e.getMessage());
				return getAuthenticationErrorResponse();
			}

		} else {
			return getAuthenticationErrorResponse();
		}

	}

	private User authenticate(final User userToAuthenticate) {
		User currentUser = null;

		final Session session = sessionFactory.beginTransaction();
		currentUser = session.find(new User().getClass(), userToAuthenticate.getUsername());
		sessionFactory.commitTransactionAndClose(session);

		try {
			if (currentUser != null && PasswordStorage.verifyPassword(userToAuthenticate.getPassword(),
					currentUser.getHashedPassword())) {
				return currentUser;
			}
		} catch (final Exception e) {
			logger.error("Error when authenticating user: ", e);
			return null;
		}

		return null;
	}

	private String issueToken() {
		final Random random = new SecureRandom();
		return new BigInteger(130, random).toString(32);
	}

	private Response getAuthenticationErrorResponse() {
		final Error error = new Error();
		error.setStatus(Response.Status.UNAUTHORIZED.toString());
		error.setTitle("Invalid credentials");
		error.setDetail("You have entered an invalid username or password");

		final JSONAPIDocument<?> errorDocument = JSONAPIDocument.createErrorDocument(Collections.singleton(error));

		try {
			return Response.status(Response.Status.UNAUTHORIZED).type("application/vnd.api+json")
					.entity(this.converter.writeDocument(errorDocument)).build();
		} catch (final DocumentSerializationException e) {
			logger.debug(e.getMessage());
			return getAuthenticationErrorResponse();
		}
	}

}
