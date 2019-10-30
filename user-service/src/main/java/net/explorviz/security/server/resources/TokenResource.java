package net.explorviz.security.server.resources;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import net.explorviz.security.services.exceptions.UserValidationService;
import net.explorviz.security.user.User;
import net.explorviz.shared.security.TokenBasedSecurityContext;
import net.explorviz.shared.security.filters.Secure;
import net.explorviz.shared.security.TokenDetails;


/**
 * The token resource class provides endpoints for token obtainment and refreshment.
 */
@Path("v1/tokens")
@Tag(name = "Token")
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
  @Operation(description = "Request an API token")
  @ApiResponse(responseCode = "200",
      description = "If the credentials are valid, the associated user is returned. "
          + "The object includes a fresh bearer token to be used for authentication"
          + "and authorization at all services."
          + "The token expires after 1 hour and can be refreshed once ",
      content = @Content(schema = @Schema(implementation = User.class)))
  @ApiResponse(responseCode = "403", description = "Invalid credentials.")
  @RequestBody(description = "The credentials",
      content = @Content(schema = @Schema(implementation = UserCredentials.class)))
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
  @PermitAll
  @Operation(description = "This method refreshes a Json Web Token (JWT). "
      + "The HTTP POST body must not contain data and the "
      + "to-be refreshed token inside of the ' Authorization: Bearer' header.")
  @ApiResponse(responseCode = "200",
      description = "New token, which again is valid for 1 hour. "
          + "A refreshed token can't be refreshed further.",
      content = @Content(schema = @Schema(implementation = User.class)))
  @ApiResponse(responseCode = "403", description = "Token can't be refreshed.")
  @SecurityRequirement(name = "token")
  @Secure
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
