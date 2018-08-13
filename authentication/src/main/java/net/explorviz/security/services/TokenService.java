package net.explorviz.security.services;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;

import org.jvnet.hk2.annotations.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import net.explorviz.security.model.User;

@Service
public class TokenService {

	public String issueToken(final User user) {
		final String id = UUID.randomUUID().toString();

		final ZonedDateTime issuedDate = ZonedDateTime.now();
		final ZonedDateTime expirationDate = issuedDate.plusSeconds(60);

		final String authenticationToken = Jwts.builder().setId(id).setIssuer("ExplorViz").setAudience("ExplorViz")
				.setSubject(user.getUsername()).setIssuedAt(Date.from(issuedDate.toInstant()))
				.setExpiration(Date.from(expirationDate.toInstant())).claim("roles", user.getRoles())
				.claim("refreshCount", 0).claim("refreshLimit", 1).signWith(SignatureAlgorithm.HS256, "secret")
				.compact();

		return authenticationToken;
	}

}
