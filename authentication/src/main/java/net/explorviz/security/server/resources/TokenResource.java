package net.explorviz.security.server.resources;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import net.explorviz.security.model.Token;
import net.explorviz.security.model.UserCredentials;
import net.explorviz.security.services.TokenService;
import net.explorviz.security.services.UserService;
import net.explorviz.shared.annotations.Secured;
import net.explorviz.shared.security.TokenBasedSecurityContext;
import net.explorviz.shared.security.TokenDetails;
import net.explorviz.shared.security.User;

@Path("v1/tokens")
public class TokenResource {

	private final UserService userService;
	private final TokenService tokenService;

	@Inject
	public TokenResource(final UserService userService, final TokenService tokenService) {
		this.userService = userService;
		this.tokenService = tokenService;
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@PermitAll
	public Token issueToken(final UserCredentials credentials) {

		// curl -X POST
		// 'http://localhost:8082/v1/tokens/'
		// -H 'Content-Type: application/json'
		// -d '{ "username": "admin", "password": "password" }'

		final User user = this.userService.validateUserCredentials(credentials);

		final Token t = new Token();
		t.setToken(this.tokenService.issueNewToken(user));

		return t;
	}

	@POST
	@Path("refresh")
	@Produces(MediaType.APPLICATION_JSON)
	@Secured
	public Response refresh(@Context final ContainerRequestContext context) {

		// curl -X POST
		// 'http://localhost:8082/v1/tokens/refresh/'
		// -H 'Accept: application/json'
		// -H 'Authorization: Bearer <authentication-token>'

		final TokenBasedSecurityContext sec = (TokenBasedSecurityContext) context.getSecurityContext();

		final TokenDetails tokenDetails = sec.getTokenDetails();
		final String token = this.tokenService.issueRefreshmentToken(tokenDetails);

		final Token authenticationToken = new Token();
		authenticationToken.setToken(token);
		return Response.ok(authenticationToken).build();
	}

}