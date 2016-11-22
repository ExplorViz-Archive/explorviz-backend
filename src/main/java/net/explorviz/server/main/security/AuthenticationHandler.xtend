package net.explorviz.server.main.security

import javax.ws.rs.Path
import javax.ws.rs.Consumes
import javax.ws.rs.core.Response
import javax.servlet.http.HttpServletResponse
import javax.annotation.security.PermitAll
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.core.HttpHeaders
import org.glassfish.jersey.internal.util.Base64
import java.util.StringTokenizer
import javax.ws.rs.GET

@Path("authenticate")
class AuthenticationHandler {

	@GET
	@Consumes("application/x-www-form-urlencoded")
	@PermitAll
	def Response authenticate(ContainerRequestContext requestContext) {
		
		println("in authenticate")

		var authoHeader = requestContext.headers.get(HttpHeaders.AUTHORIZATION)

		var encodedUserPassword = authoHeader.get(0).replaceFirst("Basic" + " ", "");

		var usernameAndPassword = new String(Base64.decode(encodedUserPassword.getBytes()));

		var tokenizer = new StringTokenizer(usernameAndPassword, ":");
		var username = tokenizer.nextToken();
		var password = tokenizer.nextToken();

		println(username)
		println(password)

//		var responseBuilder = Response.status(HttpServletResponse.SC_OK);
		var responseBuilder = Response.ok().header("Access-Control-Allow-Origin", "*").header(
			"Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS").header("Access-Control-Allow-Headers",
			"Content-Type, Accept, X-Requested-With");

		responseBuilder.entity("Authentication successful").build
	}

}
