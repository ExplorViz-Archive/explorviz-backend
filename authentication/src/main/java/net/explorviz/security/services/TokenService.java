package net.explorviz.security.services;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;

import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;

import org.jvnet.hk2.annotations.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import net.explorviz.shared.annotations.Config;
import net.explorviz.shared.security.TokenDetails;
import net.explorviz.shared.security.TokenParserService;
import net.explorviz.shared.security.User;

@Service
public class TokenService {

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

	public String issueNewToken(final User user) {
		final String id = UUID.randomUUID().toString();

		final ZonedDateTime issuedDate = ZonedDateTime.now();
		final ZonedDateTime expirationDate = issuedDate.plusSeconds(validFor);

		final String authenticationToken = Jwts.builder().setId(id).setIssuer(issuer).setAudience(audience)
				.setSubject(user.getUsername()).setIssuedAt(Date.from(issuedDate.toInstant()))
				.setExpiration(Date.from(expirationDate.toInstant())).claim("roles", user.getRoles())
				.claim("refreshCount", 0).claim("refreshLimit", refreshLimit).signWith(SignatureAlgorithm.HS256, secret)
				.compact();

		return authenticationToken;
	}

	private String issueRefreshmentToken(final TokenDetails newTokenDetails) {
		final String id = UUID.randomUUID().toString();

		final String authenticationToken = Jwts.builder().setId(id).setIssuer(issuer).setAudience(audience)
				.setSubject(newTokenDetails.getUsername())
				.setIssuedAt(Date.from(newTokenDetails.getIssuedDate().toInstant()))
				.setExpiration(Date.from(newTokenDetails.getExpirationDate().toInstant()))
				.claim("roles", newTokenDetails.getRoles()).claim("refreshCount", newTokenDetails.getRefreshCount())
				.claim("refreshLimit", refreshLimit).signWith(SignatureAlgorithm.HS256, secret).compact();

		return authenticationToken;
	}

	public TokenDetails parseToken(final String token) {
		return this.tokenParser.parseToken(token);
	}

	public String refreshToken(final TokenDetails currentTokenDetails) {

		if (!currentTokenDetails.isEligibleForRefreshment()) {
			throw new ForbiddenException("This token cannot be refreshed");
		}

		final ZonedDateTime issuedDate = ZonedDateTime.now();
		final ZonedDateTime expirationDate = issuedDate.plusSeconds(validFor);

		final TokenDetails newTokenDetails = new TokenDetails.Builder().withId(currentTokenDetails.getId())
				.withUsername(currentTokenDetails.getUsername()).withAuthorities(currentTokenDetails.getRoles())
				.withIssuedDate(issuedDate).withExpirationDate(expirationDate)
				.withRefreshCount(currentTokenDetails.getRefreshCount() + 1).withRefreshLimit(refreshLimit).build();

		return issueRefreshmentToken(newTokenDetails);
	}

}
