package net.explorviz.shared.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.InvalidClaimException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Deserializer;
import io.jsonwebtoken.io.JacksonDeserializer;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.validation.constraints.NotNull;
import javax.ws.rs.ForbiddenException;
import net.explorviz.shared.annotations.Config;
import net.explorviz.shared.security.model.TokenDetails;
import net.explorviz.shared.security.model.roles.Role;

/**
 * This injectable service is used to extract and parse the details of a JSON web token. If used,
 * i.e., injected, do not forget to add the injectable properties to the explorviz.properties file.
 */
public class TokenParserService {

  private static final String INVALID_TOKEN_MSG = "Invalid token";

  // Credit: cassiomolin - https://github.com/cassiomolin/jersey-jwt

  @Config("jwt.secret")
  private String secret;

  @Config("jwt.audience")
  private String audience;

  @Config("jwt.clockSkewInSeconds")
  private int clockSkewInSeconds;

  /**
   * Parses a stringified JSON web token and extracts its details into a Java model.
   *
   * @param token - Stringified JWT.
   * @return TokenDetails that contain all JWT details
   */
  public TokenDetails parseToken(final String token) {

    final ObjectMapper mapper = new ObjectMapper();
    final SimpleModule module = new SimpleModule();
    module.addDeserializer(List.class, new RoleJwtDeserializer());
    mapper.registerModule(module);

    final Deserializer<Map<String, ?>> deserializer = new JacksonDeserializer<>(mapper);

    try {

      final Claims claims = Jwts.parser().deserializeJsonWith(deserializer)
          .setSigningKey(this.secret).requireAudience(this.audience)
          .setAllowedClockSkewSeconds(this.clockSkewInSeconds).parseClaimsJws(token).getBody();

      return new TokenDetails.Builder().withId(this.extractTokenIdFromClaims(claims))
          .withUsername(this.extractUsernameFromClaims(claims))
          .withAuthorities(this.extractAuthoritiesFromClaims(claims))
          .withIssuedDate(this.extractIssuedDateFromClaims(claims))
          .withExpirationDate(this.extractExpirationDateFromClaims(claims))
          .withRefreshCount(this.extractRefreshCountFromClaims(claims))
          .withRefreshLimit(this.extractRefreshLimitFromClaims(claims)).build();

    } catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException e) {
      throw new ForbiddenException(INVALID_TOKEN_MSG, e);
    } catch (final ExpiredJwtException e) {
      throw new ForbiddenException("Expired token", e);
    } catch (final InvalidClaimException e) {
      throw new ForbiddenException("Invalid value for claim \"" + e.getClaimName() + "\"", e);
    } catch (final Exception e) { // NOPMD
      throw new ForbiddenException(INVALID_TOKEN_MSG, e);
    }
  }

  /**
   * Extract the token identifier from the token claims.
   *
   * @param claims - The JWT claims set
   * @return Identifier of the JWT token
   */
  private String extractTokenIdFromClaims(@NotNull final Claims claims) {
    return (String) claims.get(Claims.ID);
  }

  /**
   * Extract the username from the token claims.
   *
   * @param claims - The JWT claims set
   * @return Username from the JWT token
   */
  private String extractUsernameFromClaims(@NotNull final Claims claims) {
    return claims.getSubject();
  }

  /**
   * Extract the user authorities from the token claims.
   *
   * @param claims - The JWT claims set
   * @return User authorities from the JWT token
   */
  @SuppressWarnings("unchecked")
  private List<Role> extractAuthoritiesFromClaims(@NotNull final Claims claims) {
    return (List<Role>) claims.getOrDefault("roles", new ArrayList<Role>());
  }

  /**
   * Extract the issued date from the token claims.
   *
   * @param claims - The JWT claims set
   * @return Issued date of the JWT token
   */
  private ZonedDateTime extractIssuedDateFromClaims(@NotNull final Claims claims) {
    return ZonedDateTime.ofInstant(claims.getIssuedAt().toInstant(), ZoneId.systemDefault());
  }

  /**
   * Extract the expiration date from the token claims.
   *
   * @param claims - The JWT claims set
   * @return Expiration date of the JWT token
   */
  private ZonedDateTime extractExpirationDateFromClaims(@NotNull final Claims claims) {
    return ZonedDateTime.ofInstant(claims.getExpiration().toInstant(), ZoneId.systemDefault());
  }

  /**
   * Extract the refresh count from the token claims.
   *
   * @param claims - The JWT claims set
   * @return Refresh count from the JWT token
   */
  private int extractRefreshCountFromClaims(@NotNull final Claims claims) {
    return (int) claims.get("refreshCount");
  }

  /**
   * Extract the refresh limit from the token claims.
   *
   * @param claims - The JWT claims set
   * @return Refresh limit from the JWT token
   */
  private int extractRefreshLimitFromClaims(@NotNull final Claims claims) {
    return (int) claims.get("refreshLimit");
  }
}
