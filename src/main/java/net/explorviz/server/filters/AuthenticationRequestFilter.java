package net.explorviz.server.filters;

import java.io.IOException;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.ws.rs.ext.Provider;

import org.hibernate.Session;

import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import net.explorviz.server.repository.HibernateSessionFactory;
import net.explorviz.server.security.Secured;
import net.explorviz.server.security.User;

@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationRequestFilter implements ContainerRequestFilter {
	
	private HibernateSessionFactory sessionFactory;
	
	@Inject
	public AuthenticationRequestFilter(HibernateSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {

		String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

		if (authorizationHeader == null || !authorizationHeader.startsWith("Basic")) {
			requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
		}

		String token = authorizationHeader.substring("Basic".length()).trim();

		if (!validateToken(token)) {
			requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
		}

		// user authenticated => everything ok
	}

	private boolean validateToken(String token) {
		User currentUser = null;
		
		Session session = sessionFactory.beginTransaction();
		
		TypedQuery<User> query = session.createQuery("FROM USERS where token = :token ", User.class);
		query.setParameter("token", token);
		
		try{
			currentUser = query.getSingleResult();
		} catch(NoResultException e) {
			return false;
		}
		
		sessionFactory.commitTransactionAndClose(session);

		if (currentUser == null) {
			return false;
		}

		return token.equals(currentUser.getToken());
	}

}
