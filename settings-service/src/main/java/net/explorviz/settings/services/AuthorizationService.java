package net.explorviz.settings.services;

import javax.inject.Inject;
import net.explorviz.shared.security.TokenParserService;
import net.explorviz.shared.security.model.TokenDetails;
import org.jvnet.hk2.annotations.Service;

/**
 * Handles authorization.
 *
 */
@Service
public class AuthorizationService {

  private final TokenParserService tps;


  @Inject
  public AuthorizationService(final TokenParserService tokenParserService) {
    this.tps = tokenParserService;
  }

  /**
   * Checks whether the given user id matches to the one given in the auth header.
   *
   * @param id user id
   * @param authHeader authorization header
   * @return if and only if the user id is the same as the id given in the authorization header
   */
  public boolean isSameUser(final String id, final String authHeader) {
    final TokenDetails details = this.tps.parseToken(authHeader.substring(7));

    return details.getUserId().equals(id);
  }

}
