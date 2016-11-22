package net.explorviz.server.main;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.annotation.Priority;
import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.glassfish.jersey.internal.util.Base64;

public class AuthorizationFilter implements ContainerRequestFilter {

	@Context
	private ResourceInfo resourceInfo;

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		
		requestContext.getHeaders().add("Access-Control-Allow-Origin", "*");
		requestContext.getHeaders().add("Access-Control-Allow-Headers",
                "origin, content-type, accept, authorization");
		requestContext.getHeaders().add("Access-Control-Allow-Credentials", "true");
		requestContext.getHeaders().add("Access-Control-Allow-Methods",
                "GET, POST, PUT, DELETE, OPTIONS, HEAD");
		
		System.out.println("in filter");
		
		Method method = resourceInfo.getResourceMethod();
		
		System.out.println(resourceInfo.getResourceClass().getCanonicalName());
		System.out.println(method.getAnnotations().length);
		System.out.println(method.getDeclaredAnnotations().length);
		
		if(method.getAnnotation(PermitAll.class) != null)
			System.out.println("lol");

		if (!method.isAnnotationPresent(PermitAll.class)) {
			
			System.out.println("in annotation");
			System.out.println(method.isAnnotationPresent(PermitAll.class));

			if (method.isAnnotationPresent(DenyAll.class)) {
				Response ACCESS_FORBIDDEN = Response.status(Response.Status.FORBIDDEN)
						.entity("Access blocked for all users !!").build();
				requestContext.abortWith(ACCESS_FORBIDDEN);
				return;
			}


			final MultivaluedMap<String, String> headers = requestContext.getHeaders();


			final List<String> authorization = headers.get(HttpHeaders.AUTHORIZATION);
			
			System.out.println(authorization);
			
			Response ACCESS_DENIED = Response.status(Response.Status.UNAUTHORIZED)
					.entity("You cannot access this resource").build();


			if (authorization == null || authorization.isEmpty()) {
				System.out.println("abort1");
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
					System.out.println("abort2");
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
