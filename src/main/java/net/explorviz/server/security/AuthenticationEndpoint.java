package net.explorviz.server.security;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.hibernate.Session;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import net.explorviz.server.repository.HibernateSessionFactory;
import net.explorviz.server.security.PasswordStorage.CannotPerformOperationException;
import net.explorviz.server.security.PasswordStorage.InvalidHashException;

/***
 * Provides the endpoint for authentication: http:\/\/*IP*:*Port*\/sessions.
 * Clients obtain their authentication token here.
 * 
 * @author akr
 *
 */
@Path("/sessions")
public class AuthenticationEndpoint {

	/***
	 * Creates and returns a randomized token for users. A client request must
	 * send "username=admin&password=123456" in the body of a POST request. If
	 * authentication of credentials succeeds, a randomized token will be
	 * returned.
	 * 
	 * Tokens, their expiration dates and the related users are stored on the
	 * backend. Every frontend request must send this token in its
	 * "Authorization" header, since all other resources are token-based secured
	 * and only accessible with a valid token.
	 * 
	 * @author akr
	 * @param username
	 * @param password
	 * @return If authentication succeeds, the return will be a HTTP-Response
	 *         with status code 200 and body:{"token":randomizedToken,
	 *         "username": username}. If authentication fails, this return will
	 *         be status code 401.
	 */
	@Path("/create")
	@POST
	@Produces("application/json")
	@Consumes("application/x-www-form-urlencoded")
	public Response authenticateUser(@FormParam("username") String username, @FormParam("password") String password) {

		if (authenticate(username, password)) {

			String token = issueToken();

			Session session = HibernateSessionFactory.getInstance().openSession();

			User currentUser = null;

			session.beginTransaction();
			currentUser = session.find(User.class, username);

			if (currentUser != null) {
				currentUser.setToken(token);
				session.update(currentUser);
			}

			session.getTransaction().commit();
			session.close();

			JsonNodeFactory factory = JsonNodeFactory.instance;
			ObjectNode jsonNode = factory.objectNode();
			jsonNode.put("username", username);
			jsonNode.put("token", token);

			return Response.ok(jsonNode).build();

		} else {
			return Response.status(Response.Status.UNAUTHORIZED).type("text/plain")
					.entity("You have entered an invalid username or password").build();
		}

	}

	/***
	 * Authenticates user and password against database.
	 * 
	 * @author akr
	 * @param username
	 * @param password
	 * @return True if the authentication succeeds, otherwise false.
	 */
	private boolean authenticate(String username, String password) {

		Session session = HibernateSessionFactory.getInstance().openSession();

		User currentUser = null;

		session.beginTransaction();
		currentUser = session.find(User.class, username);
		session.getTransaction().commit();
		session.close();

		try {
			if (currentUser != null && PasswordStorage.verifyPassword(password, currentUser.getHashedPassword())) {
				return true;
			}
		} catch (CannotPerformOperationException | InvalidHashException e) {
			e.printStackTrace();
			return false;
		}

		return false;
	}

	/***
	 * Creates token for future client requests.
	 * 
	 * @return Randomized token string
	 */
	private String issueToken() {
		Random random = new SecureRandom();
		return new BigInteger(130, random).toString(32);
	}

}
