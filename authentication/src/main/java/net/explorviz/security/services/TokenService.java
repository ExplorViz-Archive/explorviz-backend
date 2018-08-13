package net.explorviz.security.services;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;

import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;

import org.jvnet.hk2.annotations.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import net.explorviz.security.model.TokenDetails;
import net.explorviz.security.model.User;

@Service
public class TokenService {

	private final String secret = "secret";
	private final String audience = "ExplorViz";

	private final TokenParserService tokenParser;

	@Inject
	public TokenService(final TokenParserService tokenParser) {
		this.tokenParser = tokenParser;
	}

	public String issueNewToken(final User user) {
		final String id = UUID.randomUUID().toString();

		final ZonedDateTime issuedDate = ZonedDateTime.now();
		final ZonedDateTime expirationDate = issuedDate.plusSeconds(60);

		final String authenticationToken = Jwts.builder().setId(id).setIssuer("ExplorViz").setAudience(audience)
				.setSubject(user.getUsername()).setIssuedAt(Date.from(issuedDate.toInstant()))
				.setExpiration(Date.from(expirationDate.toInstant())).claim("roles", user.getRoles())
				.claim("refreshCount", 0).claim("refreshLimit", 1).signWith(SignatureAlgorithm.HS256, secret).compact();

		return authenticationToken;
	}

	public String issueRefreshmentToken(final TokenDetails newTokenDetails) {
		final String id = UUID.randomUUID().toString();

		final String authenticationToken = Jwts.builder().setId(id).setIssuer("ExplorViz").setAudience(audience)
				.setSubject(newTokenDetails.getUsername())
				.setIssuedAt(Date.from(newTokenDetails.getIssuedDate().toInstant()))
				.setExpiration(Date.from(newTokenDetails.getExpirationDate().toInstant()))
				.claim("roles", newTokenDetails.getRoles()).claim("refreshCount", 0).claim("refreshLimit", 1)
				.signWith(SignatureAlgorithm.HS256, secret).compact();

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
		final ZonedDateTime expirationDate = issuedDate.plusSeconds(60);

		final TokenDetails newTokenDetails = new TokenDetails.Builder().withId(currentTokenDetails.getId()) // Reuse the
																											// same id
				.withUsername(currentTokenDetails.getUsername()).withAuthorities(currentTokenDetails.getRoles())
				.withIssuedDate(issuedDate).withExpirationDate(expirationDate)
				.withRefreshCount(currentTokenDetails.getRefreshCount() + 1).withRefreshLimit(1).build();

		return issueRefreshmentToken(newTokenDetails);
	}

}
