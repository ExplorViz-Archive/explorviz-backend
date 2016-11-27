package net.explorviz.server.filters;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import net.explorviz.server.security.AuthenticationEndpoint;
import net.explorviz.server.security.Secured;

@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationRequestFilter implements ContainerRequestFilter{

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		
		String authorizationHeader = 
	            requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

	        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
	            throw new NotAuthorizedException("Authorization header must be provided");
	        }
	        
	        String token = authorizationHeader.substring("Bearer".length()).trim();
	        
	        if(!validateToken(token)) {
	        	requestContext.abortWith(
	 	                Response.status(Response.Status.UNAUTHORIZED).build());
	        } 
	        
	        // user authenticated => everything ok
	}
	
	  private boolean validateToken(String token)  {
		  // hardcoded for the moment => future: db with all tokens
		  return token.equals(AuthenticationEndpoint.token);
	    }

}
