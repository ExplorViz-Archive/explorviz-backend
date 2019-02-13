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
import net.explorviz.security.model.Token;
import net.explorviz.security.model.UserCredentials;
import net.explorviz.security.services.TokenService;
import net.explorviz.security.services.UserValidationService;
import net.explorviz.shared.security.TokenBasedSecurityContext;
import net.explorviz.shared.security.annotations.Secured;
import net.explorviz.shared.security.model.TokenDetails;
import net.explorviz.shared.security.model.User;

/**
 * The token resource class provides endpoints for token obtainment and refreshment.
 */
@Path("v1/tokens")
public class TokenResource {


  // private static final Logger LOGGER = LoggerFactory.getLogger(TokenResource.class); // NOPMD

  private static final String MEDIA_TYPE = "application/vnd.api+json";

  @Inject
  private UserValidationService userService;

  @Inject
  private TokenService tokenService;



  /**
   * This method issues a Json Web Token (JWT) for passed user credentials. The token expires after
   * 1 hour and can be refreshed once. After that, users must issue a new token.
   *
   * @param credentials - Username and password for authentication
   * @return JWT that contains user information
   */
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MEDIA_TYPE)
  @PermitAll
  public User issueToken(final UserCredentials credentials) {

    // curl -X POST
    // 'http://localhost:8082/v1/tokens/'
    // -H 'Content-Type: application/json'
    // -d '{ "username": "admin", "password": "password" }'

    final User user = this.userService.validateUserCredentials(credentials);
    user.setToken(this.tokenService.issueNewToken(user));

    return user;
  }

  /**
   * This method refreshes a Json Web Token (JWT). The HTTP POST body must not contain data and the
   * to-be refreshed token inside of the ' Authorization: Bearer' header.
   *
   * @param context - the context of the container
   * @return Refreshed JWT with an incremented refresh counter.
   */
  @POST
  @Path("refresh")
  @Produces(MediaType.APPLICATION_JSON)
  @Secured
  public Token refresh(@Context final ContainerRequestContext context) {

    // curl -X POST
    // 'http://localhost:8082/v1/tokens/refresh/'
    // -H 'Accept: application/json'
    // -H 'Authorization: Bearer <authentication-token>'

    final TokenBasedSecurityContext sec = (TokenBasedSecurityContext) context.getSecurityContext();

    final TokenDetails tokenDetails = sec.getTokenDetails();
    final String token = this.tokenService.refreshToken(tokenDetails);

    final Token authenticationToken = new Token();
    authenticationToken.setToken(token);
    return authenticationToken;
  }

}
