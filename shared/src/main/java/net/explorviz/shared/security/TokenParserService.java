package net.explorviz.shared.security;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.ws.rs.ForbiddenException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.InvalidClaimException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import net.explorviz.shared.annotations.Config;

/**
 * If used, i.e., injected, do not forget to add the injectable properties to
 * the explorviz.properties file.
 */
public class TokenParserService {

	// Credit: cassiomolin - https://github.com/cassiomolin/jersey-jwt

	@Config("jwt.secret")
	private String secret;

	@Config("jwt.audience")
	private String audience;

	@Config("jwt.clockSkewInSeconds")
	private int clockSkewInSeconds;

	public TokenDetails parseToken(final String token) {

		try {

			final Claims claims = Jwts.parser().setSigningKey(secret).requireAudience(audience)
					.setAllowedClockSkewSeconds(clockSkewInSeconds).parseClaimsJws(token).getBody();

			return new TokenDetails.Builder().withId(extractTokenIdFromClaims(claims))
					.withUsername(extractUsernameFromClaims(claims))
					.withAuthorities(extractAuthoritiesFromClaims(claims))
					.withIssuedDate(extractIssuedDateFromClaims(claims))
					.withExpirationDate(extractExpirationDateFromClaims(claims))
					.withRefreshCount(extractRefreshCountFromClaims(claims))
					.withRefreshLimit(extractRefreshLimitFromClaims(claims)).build();

		} catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException | SignatureException e) {
			throw new ForbiddenException("Invalid token", e);
		} catch (final ExpiredJwtException e) {
			throw new ForbiddenException("Expired token", e);
		} catch (final InvalidClaimException e) {
			throw new ForbiddenException("Invalid value for claim \"" + e.getClaimName() + "\"", e);
		} catch (final Exception e) {
			throw new ForbiddenException("Invalid token", e);
		}
	}

	/**
	 * Extract the token identifier from the token claims.
	 *
	 * @param claims
	 * @return Identifier of the JWT token
	 */
	private String extractTokenIdFromClaims(@NotNull final Claims claims) {
		return (String) claims.get(Claims.ID);
	}

	/**
	 * Extract the username from the token claims.
	 *
	 * @param claims
	 * @return Username from the JWT token
	 */
	private String extractUsernameFromClaims(@NotNull final Claims claims) {
		return claims.getSubject();
	}

	/**
	 * Extract the user authorities from the token claims.
	 *
	 * @param claims
	 * @return User authorities from the JWT token
	 */
	@SuppressWarnings("unchecked")
	private List<String> extractAuthoritiesFromClaims(@NotNull final Claims claims) {
		return (List<String>) claims.getOrDefault("roles", new ArrayList<String>());
	}

	/**
	 * Extract the issued date from the token claims.
	 *
	 * @param claims
	 * @return Issued date of the JWT token
	 */
	private ZonedDateTime extractIssuedDateFromClaims(@NotNull final Claims claims) {
		return ZonedDateTime.ofInstant(claims.getIssuedAt().toInstant(), ZoneId.systemDefault());
	}

	/**
	 * Extract the expiration date from the token claims.
	 *
	 * @param claims
	 * @return Expiration date of the JWT token
	 */
	private ZonedDateTime extractExpirationDateFromClaims(@NotNull final Claims claims) {
		return ZonedDateTime.ofInstant(claims.getExpiration().toInstant(), ZoneId.systemDefault());
	}

	/**
	 * Extract the refresh count from the token claims.
	 *
	 * @param claims
	 * @return Refresh count from the JWT token
	 */
	private int extractRefreshCountFromClaims(@NotNull final Claims claims) {
		return (int) claims.get("refreshCount");
	}

	/**
	 * Extract the refresh limit from the token claims.
	 *
	 * @param claims
	 * @return Refresh limit from the JWT token
	 */
	private int extractRefreshLimitFromClaims(@NotNull final Claims claims) {
		return (int) claims.get("refreshLimit");
	}
}