package net.explorviz.security.server.resources;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import net.explorviz.security.model.Token;
import net.explorviz.security.model.User;
import net.explorviz.security.model.UserCredentials;
import net.explorviz.security.services.TokenService;
import net.explorviz.security.services.UserService;

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
		t.setToken(this.tokenService.issueToken(user));

		return t;
	}

	/*
	 * @POST
	 *
	 * @Path("refresh")
	 *
	 * @Produces(MediaType.APPLICATION_JSON) public Response refresh() {
	 *
	 * final AuthenticationTokenDetails tokenDetails = ((TokenBasedSecurityContext)
	 * securityContext) .getAuthenticationTokenDetails(); final String token =
	 * authenticationTokenService.refreshToken(tokenDetails);
	 *
	 * final AuthenticationToken authenticationToken = new AuthenticationToken();
	 * authenticationToken.setToken(token); return
	 * Response.ok(authenticationToken).build(); }
	 */

}