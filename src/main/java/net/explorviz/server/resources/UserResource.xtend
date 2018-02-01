package net.explorviz.server.resources

import javax.ws.rs.Path
import net.explorviz.server.security.User
import javax.inject.Inject
import org.hibernate.Session
import net.explorviz.server.security.PasswordStorage
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.Random
import java.math.BigInteger
import java.security.SecureRandom
import javax.ws.rs.core.Response
import com.github.jasminb.jsonapi.ResourceConverter
import com.github.jasminb.jsonapi.JSONAPIDocument
import com.github.jasminb.jsonapi.models.errors.Error;
import java.util.Collections
import javax.ws.rs.GET
import javax.ws.rs.Produces
import javax.ws.rs.PATCH
import javax.ws.rs.QueryParam
import net.explorviz.repository.HibernateSessionFactory

@Path("users")
class UserResource {

	val private static Logger LOGGER = LoggerFactory.getLogger(UserResource)

	var HibernateSessionFactory sessionFactory
	var ResourceConverter converter;

	@Inject
	def UserResource(HibernateSessionFactory sessionFactory, ResourceConverter converter) {
		this.sessionFactory = sessionFactory
		this.converter = converter
	}

	@GET
	@Produces(value="application/vnd.api+json")
	def Response getUser(@QueryParam("username") String username) {
		var User currentUser = null

		// retrieve user
		val Session session = sessionFactory.beginTransaction()
		currentUser = session.find(User, username)

		sessionFactory.commitTransactionAndClose(session)
		
		if(currentUser !== null) {
			currentUser.initializeID()
			currentUser.token = null
			currentUser.password = null
			currentUser.isAuthenticated = false
			currentUser.hashedPassword = null
			
			val JSONAPIDocument<User> document = new JSONAPIDocument<User>(currentUser);

			return Response.ok(this.converter.writeDocument(document)).type("application/vnd.api+json").build();
		} else {
			return getAuthenticationErrorResponse()
		}
	}

	@PATCH
	@Path("authenticate")
	def Response authenticateUser(User userToAuthenticate) {

		val possibleUser = authenticate(userToAuthenticate)

		if (possibleUser !== null) {

			val String token = issueToken()
			possibleUser.token = token
			possibleUser.password = null
			possibleUser.id = userToAuthenticate.id
			possibleUser.isAuthenticated = true

			val Session session = sessionFactory.beginTransaction()
			session.update(possibleUser)
			sessionFactory.commitTransactionAndClose(session)

			val JSONAPIDocument<User> document = new JSONAPIDocument<User>(possibleUser);

			return Response.ok(this.converter.writeDocument(document)).type("application/vnd.api+json").build();

		} else {
			return getAuthenticationErrorResponse()
		}

	}

	def private User authenticate(User userToAuthenticate) {
		var User currentUser = null;

		val Session session = sessionFactory.beginTransaction();
		currentUser = session.find(User, userToAuthenticate.username);
		sessionFactory.commitTransactionAndClose(session);

		try {
			if (currentUser !== null &&
				PasswordStorage.verifyPassword(userToAuthenticate.password, currentUser.getHashedPassword())) {
				return currentUser;
			}
		} catch (Exception e) {
			LOGGER.error("Error when authenticating user: ", e)
			return null;
		}

		return null;
	}

	def private String issueToken() {
		val Random random = new SecureRandom();
		return new BigInteger(130, random).toString(32);
	}
	
	def private Response getAuthenticationErrorResponse() {
		val Error error = new Error();
		error.status = Response.Status.UNAUTHORIZED.toString
		error.title = "Invalid credentials"
		error.detail = "You have entered an invalid username or password"

		val JSONAPIDocument<?> errorDocument = JSONAPIDocument.createErrorDocument(Collections.singleton(error))

		return Response.status(Response.Status.UNAUTHORIZED).type("application/vnd.api+json").entity(
			this.converter.writeDocument(errorDocument)).build();
	}

}
