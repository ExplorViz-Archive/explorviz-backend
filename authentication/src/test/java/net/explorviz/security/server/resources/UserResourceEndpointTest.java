package net.explorviz.security.server.resources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import com.github.jasminb.jsonapi.DeserializationFeature;
import com.github.jasminb.jsonapi.JSONAPIDocument;
import com.github.jasminb.jsonapi.LongIdHandler;
import com.github.jasminb.jsonapi.ResourceConverter;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Type;
import com.github.jasminb.jsonapi.exceptions.DocumentSerializationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import net.explorviz.security.server.main.DependencyInjectionBinder;
import net.explorviz.security.services.InMemoryUserCrudService;
import net.explorviz.security.services.TokenService;
import net.explorviz.security.services.UserCrudService;
import net.explorviz.shared.security.User;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpStatus;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.HttpUrlConnectorProvider;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

/**
 * This class contains tests for {@link UserResource} using actual http-requests and -responses.
 *
 */
public class UserResourceEndpointTest extends JerseyTest {

  private static final String MEDIA_TYPE = "application/vnd.api+json";

  @Inject
  private TokenService tokenService;

  private String adminToken;
  private String normieToken;

  private ResourceConverter jsonApiConverter;



  @Override
  public void setUp() throws Exception {

    // Inject dependencies
    final ServiceLocator locator = ServiceLocatorUtilities.bind(new DependencyInjectionBinder());
    locator.inject(this);



    // Create tokens for random users
    final User admin = new User("Admin");
    admin.setRoles(Arrays.asList("admin"));
    final User normie = new User("Normie");

    this.adminToken = "Bearer " + this.tokenService.issueNewToken(admin);
    this.normieToken = "Bearer " + this.tokenService.issueNewToken(normie);


    // Setup json api converter
    this.jsonApiConverter = new ResourceConverter();
    this.jsonApiConverter.registerType(UserInput.class);
    this.jsonApiConverter.disableDeserializationOption(DeserializationFeature.REQUIRE_RESOURCE_ID);

    super.setUp();
  }


  @Override
  protected void configureClient(final ClientConfig config) {
    // Otherwise we can't send PATCH-Requests
    config.property(HttpUrlConnectorProvider.SET_METHOD_WORKAROUND, true);
  }


  @Override
  protected Application configure() {

    final ResourceConfig c =
        new ResourceConfig(new net.explorviz.security.server.main.Application());

    // Use the in-memory user db instead of mongo to avoid connection to an actual database.
    // Could also use a mock-object instead
    c.register(new AbstractBinder() {
      @Override
      protected void configure() {
        this.bind(InMemoryUserCrudService.class).to(UserCrudService.class).in(Singleton.class)
            .ranked(10);

      }
    });
    return c;
  }

  @Test
  public void createUserAsAdminTest() throws InterruptedException, DocumentSerializationException {
    final UserInput u = new UserInput(null, "newuser", "pw", null);


    // Marshall to json api object
    final JSONAPIDocument<UserInput> userDoc = new JSONAPIDocument<>(u);
    final byte[] converted = this.jsonApiConverter.writeDocument(userDoc);

    // Send request
    final Entity<byte[]> userEntity = Entity.entity(converted, MEDIA_TYPE);
    final Response response = this.target("v1/users").request()
        .header(HttpHeader.AUTHORIZATION.asString(), this.adminToken).post(userEntity);


    assertEquals(HttpStatus.OK_200, response.getStatus());

    final UserInput respuser = this.jsonApiConverter
        .readDocument(response.readEntity(byte[].class), UserInput.class).get();
    assertEquals(u.getUsername(), respuser.getUsername());
    // No passwords should be sent back
    assertEquals(null, respuser.getPassword());
    assertEquals(u.getRoles(), respuser.getRoles());
    // Id must be set
    assertTrue(respuser.getId() > 0);
  }


  @Test
  public void createUserAsNormie() throws DocumentSerializationException {
    final UserInput u = new UserInput(null, "newuser", "pw", null);

    // Marshall to json api object
    final JSONAPIDocument<UserInput> userDoc = new JSONAPIDocument<>(u);
    final byte[] converted = this.jsonApiConverter.writeDocument(userDoc);

    // Send request
    final Entity<byte[]> userEntity = Entity.entity(converted, MEDIA_TYPE);
    final Response response = this.target("v1/users").request()
        .header(HttpHeader.AUTHORIZATION.asString(), this.normieToken).post(userEntity);

    assertEquals(HttpStatus.FORBIDDEN_403, response.getStatus());
  }

  @Test
  public void createUserAsAnon() throws DocumentSerializationException {
    final UserInput u = new UserInput(null, "newuser", "pw", null);

    // Marshall to json api object
    final JSONAPIDocument<UserInput> userDoc = new JSONAPIDocument<>(u);
    final byte[] converted = this.jsonApiConverter.writeDocument(userDoc);

    // Send request
    final Entity<byte[]> body = Entity.entity(converted, MEDIA_TYPE);
    final Response response = this.target("v1/users").request().post(body);

    assertEquals(HttpStatus.FORBIDDEN_403, response.getStatus());
  }


  @Test
  public void createAll() throws DocumentSerializationException {
    final UserInput u1 = new UserInput(null, "u1", "pw", null);
    final UserInput u2 = new UserInput(null, "u2", "pw", null);

    final byte[] document =
        this.jsonApiConverter.writeDocumentCollection(new JSONAPIDocument<>(Arrays.asList(u1, u2)));

    final Entity<byte[]> body = Entity.entity(document, MEDIA_TYPE);

    final Response response = this.target("v1/users/batch").request()
        .header(HttpHeader.AUTHORIZATION.asString(), this.adminToken).post(body);

    assertEquals(HttpStatus.OK_200, response.getStatus());

    final byte[] rawResponseBody = response.readEntity(byte[].class);

    final List<UserInput> responseBody =
        this.jsonApiConverter.readDocumentCollection(rawResponseBody, UserInput.class).get();

    assertEquals(2, responseBody.size());
    assertTrue(responseBody.get(0).id > 0 && responseBody.get(1).id > 0);
  }


  @Test
  public void updateUser() throws DocumentSerializationException {

    // Create user to update afterwards
    final UserInput u = new UserInput(null, "u", "pw", null);
    final JSONAPIDocument<UserInput> userDoc = new JSONAPIDocument<>(u);
    final byte[] converted = this.jsonApiConverter.writeDocument(userDoc);
    final Entity<byte[]> requetsBody = Entity.entity(converted, MEDIA_TYPE);
    final byte[] rawResponse = this.target("v1/users").request()
        .header(HttpHeader.AUTHORIZATION.asString(), this.adminToken)
        .post(requetsBody, byte[].class);

    final UserInput createdUser =
        this.jsonApiConverter.readDocument(rawResponse, UserInput.class).get();

    // Update the users properties
    createdUser.setPassword("newpw");
    createdUser.setUsername("newname");
    createdUser.setRoles(Arrays.asList("admin"));

    final byte[] body = this.jsonApiConverter.writeDocument(new JSONAPIDocument<>(createdUser));


    final Response rawResponseBody = this.target("v1/users/" + createdUser.id).request()
        .header(HttpHeader.AUTHORIZATION.asString(), this.adminToken)
        .method("PATCH", Entity.entity(body, MEDIA_TYPE));


    final UserInput responseBody = this.jsonApiConverter
        .readDocument(rawResponseBody.readEntity(byte[].class), UserInput.class).get();

    assertEquals(createdUser.id, responseBody.id);
    assertEquals(createdUser.username, responseBody.username);
    assertEquals(null, responseBody.password);
    assertEquals(createdUser.roles, responseBody.roles);
  }



  /**
   * This class mimics the actual {@link User} class. The actual user class can't be used for
   * testing purposes since passwords are ignored when serializing.
   */
  @Type("user")
  private static class UserInput {
    private String username;

    private String password;

    @Id(LongIdHandler.class)
    private Long id;

    private List<String> roles = new ArrayList<>();

    private UserInput() {}



    public UserInput(final Long id, final String username, final String password,
        final List<String> roles) {
      super();
      this.username = username;
      this.password = password;
      this.id = id;
      this.roles = roles == null ? new ArrayList<>() : roles;
    }

    public Long getId() {
      return this.id;
    }


    public void setId(final Long id) {
      this.id = id;
    }

    public String getUsername() {
      return this.username;
    }

    public void setUsername(final String username) {
      this.username = username;
    }

    public String getPassword() {
      return this.password;
    }

    public void setPassword(final String password) {
      this.password = password;
    }

    public List<String> getRoles() {
      return this.roles;
    }

    public void setRoles(final List<String> roles) {
      this.roles = roles;
    }
  }


}
