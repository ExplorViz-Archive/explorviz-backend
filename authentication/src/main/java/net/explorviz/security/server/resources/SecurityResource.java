package net.explorviz.security.server.resources;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import net.explorviz.security.model.Token;

// @Secured
@Path("v1/tokens")
public class SecurityResource {

	private static final String APPLICATION_JSON_API_TYPE_STRING = "application/vnd.api+json";

	@POST
	@Consumes(APPLICATION_JSON_API_TYPE_STRING)
	@Produces(APPLICATION_JSON_API_TYPE_STRING)
	public Token issueToken(final net.explorviz.security.model.UserCredentials credentials) {

//		 curl -X POST \
//		 'http://localhost:8080/v1/tokens/' \
//		 -H 'Content-Type: application/vnd.api+json' \
//		 -d '{ "data": { "type": "user-credentials", "id": "1", "attributes": { "username": "admin", "password": "password" } } }'

		final String id = UUID.randomUUID().toString();

		final ZonedDateTime issuedDate = ZonedDateTime.now();
		final ZonedDateTime expirationDate = issuedDate.plusSeconds(60);

		final String authenticationToken = Jwts.builder().setId(id).setIssuer("ExplorViz").setAudience("ExplorViz")
				.setSubject(credentials.getUsername()).setIssuedAt(Date.from(issuedDate.toInstant()))
				.setExpiration(Date.from(expirationDate.toInstant()))
				.claim("authorities", new ArrayList<String>().add("admin")).claim("refreshCount", 0)
				.claim("refreshLimit", 1).signWith(SignatureAlgorithm.HS256, "secret").compact();

		final Token t = new Token("1");
		t.setToken(authenticationToken);

		return t;
	}

	@GET
	public Response authenticate(@HeaderParam("Authorization") final String authHeader) {

		System.out.println(authHeader);

		/*
		 * curl -X GET \ 'http://localhost:8080/v1/tokens/' \ -H 'Authorization: Bearer
		 * TOKEN'
		 */

		return Response.ok().build();
	}

}