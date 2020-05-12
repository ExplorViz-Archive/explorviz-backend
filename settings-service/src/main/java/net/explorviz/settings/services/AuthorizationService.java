package net.explorviz.settings.services;

import java.util.Locale;
import javax.inject.Inject;
import net.explorviz.shared.security.TokenDetails;
import net.explorviz.shared.security.TokenParserService;
import org.jvnet.hk2.annotations.Service;

/**
 * Handles authorization.
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
   * @param uid        user id
   * @param authHeader authorization header
   * @return if and only if the user id is the same as the id given in the authorization header
   */
  public boolean isSameUser(final String uid, final String authHeader) {
    try {
      final TokenDetails details = this.tps.parseToken(authHeader.substring(7));
      return details.getUserId().contentEquals(uid);
    } catch (final NullPointerException e) { // NOPMD
      // Todo: where does this NPE come from?
      // No token given
      return false;
    }
  }

  /**
   * Checks whether the token belongs to a user with admin role.
   *
   * @param authHeader authorization header
   * @return if and only if the list of roles in the token contains the admin right
   */
  public boolean isAdmin(final String authHeader) {
    try {
      final TokenDetails details = this.tps.parseToken(authHeader.substring(7));
      return details.getRoles().stream()
          .map(s -> s.toLowerCase(Locale.ENGLISH))
          .anyMatch("admin"::equals);
    } catch (final NullPointerException e) { // NOPMD
      // Todo: where does this NPE come from?
      // No token
      return false;
    }
  }

}
