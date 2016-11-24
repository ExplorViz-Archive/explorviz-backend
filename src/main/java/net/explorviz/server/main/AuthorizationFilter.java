package net.explorviz.server.main;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.internal.util.Base64;

public class AuthorizationFilter implements ContainerRequestFilter {

	@Context
	private ResourceInfo resourceInfo;

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		
		Method method = resourceInfo.getResourceMethod();

		if (!method.isAnnotationPresent(PermitAll.class)) {

			if (method.isAnnotationPresent(DenyAll.class)) {
				Response ACCESS_FORBIDDEN = Response.status(Response.Status.FORBIDDEN)
						.entity("Access blocked for all users !!").build();
				requestContext.abortWith(ACCESS_FORBIDDEN);
				return;
			}


			final MultivaluedMap<String, String> headers = requestContext.getHeaders();


			final List<String> authorization = headers.get(HttpHeaders.AUTHORIZATION);
			
			Response ACCESS_DENIED = Response.status(Response.Status.UNAUTHORIZED)
					.entity("You cannot access this resource").build();


			if (authorization == null || authorization.isEmpty()) {
				requestContext.abortWith(ACCESS_DENIED);
				return;
			}

			final String encodedUserPassword = authorization.get(0).replaceFirst("Basic" + " ", "");

			String usernameAndPassword = new String(Base64.decode(encodedUserPassword.getBytes()));

			final StringTokenizer tokenizer = new StringTokenizer(usernameAndPassword, ":");
			final String username = tokenizer.nextToken();
			final String password = tokenizer.nextToken();

			if (method.isAnnotationPresent(RolesAllowed.class)) {
				RolesAllowed rolesAnnotation = method.getAnnotation(RolesAllowed.class);
				Set<String> rolesSet = new HashSet<String>(Arrays.asList(rolesAnnotation.value()));

				if (!isUserAllowed(username, password, rolesSet)) {
					requestContext.abortWith(ACCESS_DENIED);
					return;
				}
			}
		}

	}

	private boolean isUserAllowed(final String username, final String password, final Set<String> rolesSet) {

		boolean isAllowed = false;

		if (username.equals("admin") && password.equals("explorVizPass")) {			
			
			String userRole = "admin";

			if (rolesSet.contains(userRole)) {
				isAllowed = true;
			}
		}
		return isAllowed;
	}

}
