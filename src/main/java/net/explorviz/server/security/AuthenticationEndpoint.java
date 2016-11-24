package net.explorviz.server.security;

import java.io.Serializable;
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
	public Response authenticateUser(@FormParam("username") String username, 
            @FormParam("password") String password) {
		
//		String username = credentials.getUsername();
//		String password = credentials.getPassword();
		
		System.out.println("username: " + username);
		System.out.println("password: " + password);
		
		if (authenticate(username, password)) {
			// Issue a token for the user
			String token = issueToken(username);
			
			System.out.println("token: " + token);

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
	
	private class Credentials implements Serializable {
		private static final long serialVersionUID = 1L;
		private String username;
	    
	    public String getUsername() {
			return username;
		}
		public String getPassword() {
			return password;
		}
		private String password;
	}
}
