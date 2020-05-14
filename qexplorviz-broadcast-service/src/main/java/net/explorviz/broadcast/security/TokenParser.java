package net.explorviz.broadcast.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.InvalidClaimException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Deserializer;
import io.jsonwebtoken.jackson.io.JacksonDeserializer;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import javax.ws.rs.ForbiddenException;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * This injectable service is used to extract and parse the details of a JSON web token. If used,
 * i.e., injected, do not forget to add the injectable properties to the explorviz.properties file.
 */
@Singleton
public class TokenParser {

  private static final String INVALID_TOKEN_MSG = "Invalid token";

  // Credit: cassiomolin - https://github.com/cassiomolin/jersey-jwt


  @ConfigProperty(name = "jwt.secret")
  public String secret;
  @ConfigProperty(name = "jwt.audience")
  public String audience;
  @ConfigProperty(name = "jwt.clockSkewInSeconds")
  public int clockSkewInSeconds;

  /**
   * Verifies a JSON web token. If no error is thrown, the token is valid
   *
   *
   * @param token - Stringified JWT.
   */
  public void verifyToken(final String token) {

    final ObjectMapper mapper = new ObjectMapper();
    final SimpleModule module = new SimpleModule();
    //module.addDeserializer(List.class, new JwtRoleDeserializer());
    mapper.registerModule(module);

    final Deserializer<Map<String, ?>> deserializer = new JacksonDeserializer<>(mapper);

    try {

      Jwts.parserBuilder().deserializeJsonWith(deserializer)
          .setSigningKey(this.secret).requireAudience(this.audience)
          .setAllowedClockSkewSeconds(this.clockSkewInSeconds).build().parseClaimsJws(token)
          .getBody();

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


}
