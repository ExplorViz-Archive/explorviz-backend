package net.explorviz.server;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/sessions")
public class AuthenticationEndpoint {

	@POST
	@Produces("application/json")
	@Consumes("application/x-www-form-urlencoded")
	@Path("/create")
	public Response authenticateUser(@FormParam("username") String username, @FormParam("password") String password) {
		
		System.out.println("username: " + username);
		
		if (authenticate(username, password)) {
			// Issue a token for the user
			String token = issueToken(username);

			// Return the token on the response
			return Response.ok(token).build();
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
