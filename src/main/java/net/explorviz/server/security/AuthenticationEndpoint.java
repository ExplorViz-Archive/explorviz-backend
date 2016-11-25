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

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

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
		
		if (authenticate(username, password)) {
	
			String token = issueToken(username);
			
			JsonNodeFactory factory = JsonNodeFactory.instance;
			ObjectNode jsonNode = factory.objectNode();
			jsonNode.put("token", token);
				
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
