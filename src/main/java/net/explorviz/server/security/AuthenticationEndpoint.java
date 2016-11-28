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

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/***
 * Provides the endpoint for authentication: http:\/\/*IP*:*Port*\/sessions.
 * Clients obtain their JWT here.
 * 
 * @author akr
 *
 */
@Path("/sessions")
public class AuthenticationEndpoint {

	public static String token = null;

	/***
	 * 
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

			String token = issueToken(username);

			AuthenticationEndpoint.token = token;

			JsonNodeFactory factory = JsonNodeFactory.instance;
			ObjectNode jsonNode = factory.objectNode();
			jsonNode.put("token", token);
			jsonNode.put("username", username);

			return Response.ok(jsonNode).build();

		} else {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
	}

	private boolean authenticate(String username, String password) {
		return true;
	}

	private String issueToken(String username) {
		Random random = new SecureRandom();
		return new BigInteger(130, random).toString(32);
	}

}
