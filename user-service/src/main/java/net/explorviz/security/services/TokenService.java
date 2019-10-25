package net.explorviz.security.services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;
import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;
import net.explorviz.security.user.User;
import net.explorviz.shared.config.annotations.Config;
import net.explorviz.shared.security.TokenDetails;
import net.explorviz.shared.security.TokenParserService;
import org.jvnet.hk2.annotations.Service;

/**
 * Injectable service that contains utility methods for token creation and refreshment.
 */
@Service
public class TokenService {

  private static final String ROLES_CLAIM_IDENTIFIER = "roles";
  private static final String REFRESH_COUNT_CLAIM_IDENTIFIER = "refreshCount";
  private static final String REFRESH_LIMITCLAIM_IDENTIFIER = "refreshLimit";

  @Config("jwt.secret")
  private String secret;

  @Config("jwt.audience")
  private String audience;

  @Config("jwt.issuer")
  private String issuer;

  @Config("jwt.validFor")
  private int validFor;

  @Config("jwt.refreshLimit")
  private int refreshLimit;

  @Inject
  private TokenParserService tokenParser;

  /**
   * This method issues a JSON Web Token (JWT) for a passed user. The token can be refreshed once,
   * via {@link TokenService#refreshToken(TokenDetails)}.
   *
   * @param user - Container for username and password
   * @return Stringified JWT for the passed user with refresh count = 0
   */
  public String issueNewToken(final User user) {
    final String id = UUID.randomUUID().toString();

    final ZonedDateTime issuedDate = ZonedDateTime.now();
    final ZonedDateTime expirationDate = issuedDate.plusSeconds(this.validFor);

    return Jwts.builder()
        .setId(id)
        .setIssuer(this.issuer)
        .setAudience(this.audience)
        .setSubject(user.getUsername())
        .setIssuedAt(Date.from(issuedDate.toInstant()))
        .claim("userid", user.getId())
        .setExpiration(Date.from(expirationDate.toInstant()))
        .claim(ROLES_CLAIM_IDENTIFIER, user.getRoles())
        .claim(REFRESH_COUNT_CLAIM_IDENTIFIER, 0)
        .claim(REFRESH_LIMITCLAIM_IDENTIFIER, this.refreshLimit)
        .signWith(SignatureAlgorithm.HS256, this.secret)
        .compact();
  }

  /**
   * This method issues a JSON Web Token (JWT) for a passed user. The token can be refreshed once,
   * via {@link TokenService#refreshToken(TokenDetails)}.
   *
   * @param currentTokenDetails - {@link TokenDetails} of the to-be refreshed token
   * @return Stringified and refreshed JWT for the passed token with an incremented refresh count
   */
  public String refreshToken(final TokenDetails currentTokenDetails) {

    if (!currentTokenDetails.isEligibleForRefreshment()) {
      throw new ForbiddenException("This token cannot be refreshed");
    }

    final ZonedDateTime issuedDate = ZonedDateTime.now();
    final ZonedDateTime expirationDate = issuedDate.plusSeconds(this.validFor);

    final TokenDetails newTokenDetails =
        new TokenDetails.Builder().withId(currentTokenDetails.getId())
            .withUsername(currentTokenDetails.getUsername())
            .withAuthorities(currentTokenDetails.getRoles())
            .withIssuedDate(issuedDate)
            .withExpirationDate(expirationDate)
            .withRefreshCount(currentTokenDetails.getRefreshCount() + 1)
            .withRefreshLimit(this.refreshLimit)
            .build();

    return this.issueRefreshmentToken(newTokenDetails);
  }

  public TokenDetails parseToken(final String token) {
    return this.tokenParser.parseToken(token);
  }



  private String issueRefreshmentToken(final TokenDetails newTokenDetails) {
    final String id = UUID.randomUUID().toString();

    return Jwts.builder()
        .setId(id)
        .setIssuer(this.issuer)
        .setAudience(this.audience)
        .setSubject(newTokenDetails.getUsername())
        .setIssuedAt(Date.from(newTokenDetails.getIssuedDate().toInstant()))
        .setExpiration(Date.from(newTokenDetails.getExpirationDate().toInstant()))
        .claim(ROLES_CLAIM_IDENTIFIER, newTokenDetails.getRoles())
        .claim(REFRESH_COUNT_CLAIM_IDENTIFIER, newTokenDetails.getRefreshCount())
        .claim(REFRESH_LIMITCLAIM_IDENTIFIER, this.refreshLimit)
        .signWith(SignatureAlgorithm.HS256, this.secret)
        .compact();


  }

}
