package net.explorviz.security.server.resources;

import static java.lang.Math.toIntExact;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.github.jasminb.jsonapi.JSONAPIDocument;
import com.github.jasminb.jsonapi.ResourceConverter;
import com.github.jasminb.jsonapi.exceptions.DocumentSerializationException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import net.explorviz.security.server.main.DependencyInjectionBinder;
import net.explorviz.security.services.RoleService;
import net.explorviz.security.services.TokenService;
import net.explorviz.security.services.UserMongoCrudService;
import net.explorviz.security.testutils.TestDatasourceFactory;
import net.explorviz.shared.security.model.User;
import net.explorviz.shared.security.model.roles.Role;
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
import xyz.morphia.Datastore;

/**
 * This class contains tests for {@link UserResource} using actual http-requests and -responses. So
 * far only the most basic cases are covered.
 *
 */
@SuppressWarnings("PMD")
public class UserResourceEndpointTest extends JerseyTest {

  private static final String MEDIA_TYPE = "application/vnd.api+json";
  private static final String BASE_URL = "v1/users/";

  @Inject
  private TokenService tokenService;

  @Inject
  private ResourceConverter jsonApiConverter;

  @Inject
  private UserMongoCrudService userCrudService;

  @Inject
  private RoleService roleService;

  @Inject
  private Datastore datastore;

  private String adminToken;
  private String normieToken;



  @Override
  public void setUp() throws Exception {
    final DependencyInjectionBinder binder = new DependencyInjectionBinder();
    binder.bindFactory(TestDatasourceFactory.class).to(Datastore.class).in(Singleton.class)
        .ranked(2);
    // Inject dependencies
    final ServiceLocator locator = ServiceLocatorUtilities.bind(binder);

    locator.inject(this);

    this.datastore.getCollection(User.class).drop();
    this.datastore.getCollection(Role.class).drop();

    for (final Role r : this.roleService.getAllRoles()) {
      this.datastore.save(r);
    }



    // Create tokens for random users
    final User admin = new User("Admin");
    admin.setRoles(Arrays.asList(new Role(1L, "admin")));
    final User normie = new User("Normie");

    this.adminToken = "Bearer " + this.tokenService.issueNewToken(admin);
    this.normieToken = "Bearer " + this.tokenService.issueNewToken(normie);

    super.setUp();
  }



  @Override
  public void tearDown() throws Exception {
    this.datastore.getCollection(User.class).drop();
    this.datastore.getCollection(Role.class).drop();
    super.tearDown();
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
        this.bind(UserMongoCrudService.class).to(UserMongoCrudService.class).in(Singleton.class)
            .ranked(10);
        this.bindFactory(TestDatasourceFactory.class).to(Datastore.class).in(Singleton.class)
            .ranked(2);
      }
    });
    return c;
  }

  @Test
  @org.junit.Ignore // Nees User class without restricted access rights to password, otherwise the
                    // password
  // won't be parsed
  public void createUserAsAdminTest() throws InterruptedException, DocumentSerializationException {
    final User u = new User(null, "newuser", "pw", null);


    // Marshall to json api object
    final JSONAPIDocument<User> userDoc = new JSONAPIDocument<>(u);
    final byte[] converted = this.jsonApiConverter.writeDocument(userDoc);

    final String s = new String(converted);

    // Send request
    final Entity<byte[]> userEntity = Entity.entity(converted, MEDIA_TYPE);
    final Response response = this.target(BASE_URL).request()
        .header(HttpHeader.AUTHORIZATION.asString(), this.adminToken).post(userEntity);


    assertEquals(HttpStatus.OK_200, response.getStatus());

    final User respuser =
        this.jsonApiConverter.readDocument(response.readEntity(byte[].class), User.class).get();
    assertEquals(u.getUsername(), respuser.getUsername());
    // No passwords should be sent back
    assertEquals(null, respuser.getPassword());
    assertEquals(u.getRoles(), respuser.getRoles());
    // Id must be set
    assertTrue(respuser.getId() > 0);
  }


  @Test
  public void createUserAsNormie() throws DocumentSerializationException {
    final User u = new User(null, "newuser", "pw", null);

    // Marshall to json api object
    final JSONAPIDocument<User> userDoc = new JSONAPIDocument<>(u);
    final byte[] converted = this.jsonApiConverter.writeDocument(userDoc);

    // Send request
    final Entity<byte[]> userEntity = Entity.entity(converted, MEDIA_TYPE);
    final Response response = this.target(BASE_URL).request()
        .header(HttpHeader.AUTHORIZATION.asString(), this.normieToken).post(userEntity);

    assertEquals(HttpStatus.FORBIDDEN_403, response.getStatus());
  }

  @Test
  public void createUserAsAnon() throws DocumentSerializationException {
    final User u = new User(null, "newuser", "pw", null);

    // Marshall to json api object
    final JSONAPIDocument<User> userDoc = new JSONAPIDocument<>(u);
    final byte[] converted = this.jsonApiConverter.writeDocument(userDoc);

    // Send request
    final Entity<byte[]> body = Entity.entity(converted, MEDIA_TYPE);
    final Response response = this.target(BASE_URL).request().post(body);

    assertEquals(HttpStatus.FORBIDDEN_403, response.getStatus());
  }


  @Test
  @org.junit.Ignore // See above
  public void createAll() throws DocumentSerializationException {
    final User u1 = new User(null, "u1", "pw", null);
    final User u2 = new User(null, "u2", "pw", null);

    final byte[] document =
        this.jsonApiConverter.writeDocumentCollection(new JSONAPIDocument<>(Arrays.asList(u1, u2)));

    final Entity<byte[]> body = Entity.entity(document, MEDIA_TYPE);

    final Response response = this.target("v1/users/batch").request()
        .header(HttpHeader.AUTHORIZATION.asString(), this.adminToken).post(body);

    assertEquals(HttpStatus.OK_200, response.getStatus());

    final byte[] rawResponseBody = response.readEntity(byte[].class);

    final List<User> responseBody =
        this.jsonApiConverter.readDocumentCollection(rawResponseBody, User.class).get();

    assertEquals(2, responseBody.size());
    assertTrue(responseBody.get(0).getId() > 0 && responseBody.get(1).getId() > 0);
  }


  @Test
  public void updateUser() throws DocumentSerializationException {

    // Create user to update afterwards
    final User createdUser = new User(null, "u", "pw", null);

    this.userCrudService.saveNewEntity(createdUser);

    // Update the users properties
    createdUser.setPassword("newpw");
    createdUser.setUsername("newname");
    createdUser.setRoles(Arrays.asList(this.roleService.getAllRoles().get(0)));

    final byte[] body = this.jsonApiConverter.writeDocument(new JSONAPIDocument<>(createdUser));


    final Response rawResponseBody = this.target("v1/users/" + createdUser.getId()).request()
        .header(HttpHeader.AUTHORIZATION.asString(), this.adminToken)
        .method("PATCH", Entity.entity(body, MEDIA_TYPE));


    final User responseBody = this.jsonApiConverter
        .readDocument(rawResponseBody.readEntity(byte[].class), User.class).get();

    assertEquals(createdUser.getId(), responseBody.getId());
    assertEquals(createdUser.getUsername(), responseBody.getUsername());
    assertEquals(null, responseBody.getPassword());
    assertTrue(createdUser.getRoles().stream().anyMatch(
        r -> r.getDescriptor().equals(this.roleService.getAllRoles().get(0).getDescriptor())));
  }



  @Test
  public void findById() throws DocumentSerializationException {

    final User u =
        new User(null, "name", "pw", Arrays.asList(this.roleService.getAllRoles().get(0)));

    this.userCrudService.saveNewEntity(u);

    final long id = u.getId();
    System.out.println(id);

    final byte[] rawResponseBody = this.target("v1/users/" + toIntExact(id)).request()
        .header(HttpHeader.AUTHORIZATION.asString(), this.adminToken).get(byte[].class);

    final User foundUser = this.jsonApiConverter.readDocument(rawResponseBody, User.class).get();

    assertEquals(id, (long) foundUser.getId());
    assertEquals("name", foundUser.getUsername());
    assertTrue(foundUser.getRoles().stream().anyMatch(
        r -> r.getDescriptor().equals(this.roleService.getAllRoles().get(0).getDescriptor())));

  }

  // ByRole
  @Test
  public void findByRole() throws DocumentSerializationException {
    final User u1 =
        new User(1L, "user1", "pw", Arrays.asList(this.roleService.getAllRoles().get(0)));
    final User u2 =
        new User(2L, "user2", "pw", Arrays.asList(this.roleService.getAllRoles().get(0)));
    final User u3 = new User(3L, "user3", "pw", null);

    this.userCrudService.saveNewEntity(u1);
    this.userCrudService.saveNewEntity(u2);
    this.userCrudService.saveNewEntity(u3);

    final byte[] rawResponseBody = this.target(BASE_URL).queryParam("role", "admin").request()
        .header(HttpHeader.AUTHORIZATION.asString(), this.adminToken).get(byte[].class);

    final List<User> adminUsers =
        this.jsonApiConverter.readDocumentCollection(rawResponseBody, User.class).get();

    assertEquals("Did not found all admin users", 2L, adminUsers.size());
  }


  @Test
  public void removeUser() throws DocumentSerializationException {
    final User u = new User(1L, "user1", "pw", Arrays.asList(new Role(5L, "admin")));

    this.userCrudService.saveNewEntity(u);

    final Response deleteResponse = this.target("v1/users/" + u.getId()).request()
        .header(HttpHeader.AUTHORIZATION.asString(), this.adminToken).delete();
    final Response getResponse = this.target("v1/users/" + u.getId()).request()
        .header(HttpHeader.AUTHORIZATION.asString(), this.adminToken).get();

    assertEquals(HttpStatus.NO_CONTENT_204, deleteResponse.getStatus());
    assertEquals(HttpStatus.NOT_FOUND_404, getResponse.getStatus());
  }

  @Test
  public void testRetrieveSettings() throws DocumentSerializationException {
    final HashMap<String, String> settings = new HashMap<>();
    settings.put("somekey", "somevalue");
    final User u = new User(1L, "user1", "pw", null, settings);

    this.userCrudService.saveNewEntity(u);

    final byte[] retrievedUserRaw = this.target("v1/users/" + u.getId()).request()
        .header(HttpHeader.AUTHORIZATION.asString(), this.adminToken).get(byte[].class);

    final User retrievedUser =
        this.jsonApiConverter.readDocument(retrievedUserRaw, User.class).get();

    assertEquals("Settings do not match", settings, retrievedUser.getSettings());

  }

}
