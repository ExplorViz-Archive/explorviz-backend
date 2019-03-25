package net.explorviz.security.server.resources.endpoints;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.github.jasminb.jsonapi.JSONAPIDocument;
import com.github.jasminb.jsonapi.exceptions.DocumentSerializationException;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import net.explorviz.security.server.main.DependencyInjectionBinder;
import net.explorviz.security.server.resources.UserResource;
import net.explorviz.security.services.RoleService;
import net.explorviz.security.services.UserMongoCrudService;
import net.explorviz.security.testutils.TestDatasourceFactory;
import net.explorviz.shared.security.model.User;
import net.explorviz.shared.security.model.roles.Role;
import net.explorviz.shared.security.model.settings.UserSettings;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpStatus;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.junit.Test;
import xyz.morphia.Datastore;

/**
 * This class contains tests for {@link UserResource} using actual http-requests and -responses. So
 * far only the most basic cases are covered.
 *
 */
@SuppressWarnings("PMD")
public class UserResourceEndpointTest extends EndpointTest {

  private static final String MEDIA_TYPE = "application/vnd.api+json";
  private static final String BASE_URL = "v1/users/";

  @Inject
  private UserMongoCrudService userCrudService;

  @Inject
  private RoleService roleService;

  @Inject
  private Datastore datastore;



  @Override
  public void setUp() throws Exception {
    super.setUp();
    this.datastore.getCollection(User.class).drop();
    this.datastore.getCollection(Role.class).drop();

    for (final Role r : this.roleService.getAllRoles()) {
      this.datastore.save(r);
    }
  }



  @Override
  protected void overrideTestBindings(final DependencyInjectionBinder binder) {
    binder.bindFactory(TestDatasourceFactory.class).to(Datastore.class).in(Singleton.class)
        .ranked(2);
  }



  @Override
  protected AbstractBinder overrideApplicationBindings() {
    return new DependencyInjectionBinder() {
      @Override
      public void configure() {
        this.bind(UserMongoCrudService.class).to(UserMongoCrudService.class).in(Singleton.class)
            .ranked(10);
        this.bindFactory(TestDatasourceFactory.class).to(Datastore.class).in(Singleton.class)
            .ranked(2);
      }
    };

  }



  @Override
  public void tearDown() throws Exception {
    this.datastore.getCollection(User.class).drop();
    this.datastore.getCollection(Role.class).drop();
    super.tearDown();
  }



  @Test
  @org.junit.Ignore // Nees User class without restricted access rights to password, otherwise the
                    // password
  // won't be parsed
  public void createUserAsAdminTest() throws InterruptedException, DocumentSerializationException {
    final User u = new User(null, "newuser", "pw", null);


    // Marshall to json api object
    final JSONAPIDocument<User> userDoc = new JSONAPIDocument<>(u);
    final byte[] converted = this.getJsonApiConverter().writeDocument(userDoc);

    // Send request
    final Entity<byte[]> userEntity = Entity.entity(converted, MEDIA_TYPE);
    final Response response = this.target(BASE_URL).request()
        .header(HttpHeader.AUTHORIZATION.asString(), this.getAdminToken()).post(userEntity);


    assertEquals(HttpStatus.OK_200, response.getStatus());

    final User respuser = this.getJsonApiConverter()
        .readDocument(response.readEntity(byte[].class), User.class).get();
    assertEquals(u.getUsername(), respuser.getUsername());
    // No passwords should be sent back
    assertEquals(null, respuser.getPassword());
    assertEquals(u.getRoles(), respuser.getRoles());
    // Id must be set
    assertFalse(respuser.getId().equals("0"));
  }


  @Test
  public void createUserWithUnknownSettings() throws DocumentSerializationException {
    final User user = new User("someuser");
    user.setPassword("abc");
    user.getSettings().getBooleanAttributes().put("unknownkey", false);
    // Marshall to json api object
    final JSONAPIDocument<User> userDoc = new JSONAPIDocument<>(user);
    final byte[] converted = this.getJsonApiConverter().writeDocument(userDoc);

    // Send request
    final Entity<byte[]> userEntity = Entity.entity(converted, MEDIA_TYPE);
    final Response response = this.target(BASE_URL).request()
        .header(HttpHeader.AUTHORIZATION.asString(), this.getAdminToken()).post(userEntity);

    assertEquals(HttpStatus.BAD_REQUEST_400, response.getStatus());

  }

  @Test
  public void createUserWithInvalidettingsRange() throws DocumentSerializationException {
    final User user = new User("someuser");
    user.setPassword("abc");
    user.getSettings().getNumericAttributes().put("appVizTransparencyIntensity", 1.0);
    // Marshall to json api object
    final JSONAPIDocument<User> userDoc = new JSONAPIDocument<>(user);
    final byte[] converted = this.getJsonApiConverter().writeDocument(userDoc);

    // Send request
    final Entity<byte[]> userEntity = Entity.entity(converted, MEDIA_TYPE);
    final Response response = this.target(BASE_URL).request()
        .header(HttpHeader.AUTHORIZATION.asString(), this.getAdminToken()).post(userEntity);

    assertEquals(HttpStatus.BAD_REQUEST_400, response.getStatus());

  }


  @Test
  public void createUserAsNormie() throws DocumentSerializationException {
    final User u = new User(null, "newuser", "pw", null);

    // Marshall to json api object
    final JSONAPIDocument<User> userDoc = new JSONAPIDocument<>(u);
    final byte[] converted = this.getJsonApiConverter().writeDocument(userDoc);

    // Send request
    final Entity<byte[]> userEntity = Entity.entity(converted, MEDIA_TYPE);
    final Response response = this.target(BASE_URL).request()
        .header(HttpHeader.AUTHORIZATION.asString(), this.getNormieToken()).post(userEntity);

    assertEquals(HttpStatus.FORBIDDEN_403, response.getStatus());
  }

  @Test
  public void createUserAsAnon() throws DocumentSerializationException {
    final User u = new User(null, "newuser", "pw", null);

    // Marshall to json api object
    final JSONAPIDocument<User> userDoc = new JSONAPIDocument<>(u);
    final byte[] converted = this.getJsonApiConverter().writeDocument(userDoc);

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

    final byte[] document = this.getJsonApiConverter()
        .writeDocumentCollection(new JSONAPIDocument<>(Arrays.asList(u1, u2)));

    final Entity<byte[]> body = Entity.entity(document, MEDIA_TYPE);

    final Response response = this.target("v1/users/batch").request()
        .header(HttpHeader.AUTHORIZATION.asString(), this.getAdminToken()).post(body);

    assertEquals(HttpStatus.OK_200, response.getStatus());

    final byte[] rawResponseBody = response.readEntity(byte[].class);

    final List<User> responseBody =
        this.getJsonApiConverter().readDocumentCollection(rawResponseBody, User.class).get();

    assertEquals(2, responseBody.size());
    assertTrue(Long.parseLong(responseBody.get(0).getId()) > 0
        && Long.parseLong(responseBody.get(1).getId()) > 0);
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

    final byte[] body =
        this.getJsonApiConverter().writeDocument(new JSONAPIDocument<>(createdUser));


    final Response rawResponseBody = this.target("v1/users/" + createdUser.getId()).request()
        .header(HttpHeader.AUTHORIZATION.asString(), this.getAdminToken())
        .method("PATCH", Entity.entity(body, MEDIA_TYPE));


    final User responseBody = this.getJsonApiConverter()
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

    final String id = u.getId();

    final byte[] rawResponseBody = this.target("v1/users/" + id).request()
        .header(HttpHeader.AUTHORIZATION.asString(), this.getAdminToken()).get(byte[].class);

    final User foundUser =
        this.getJsonApiConverter().readDocument(rawResponseBody, User.class).get();

    assertEquals(id, foundUser.getId());
    assertEquals("name", foundUser.getUsername());
    assertTrue(foundUser.getRoles().stream().anyMatch(
        r -> r.getDescriptor().equals(this.roleService.getAllRoles().get(0).getDescriptor())));

  }

  // ByRole
  @Test
  public void findByRole() throws DocumentSerializationException {

    // Somehow the admin user is created after the setup method dropped the table
    this.datastore.getCollection(User.class).drop();

    final User u1 =
        new User("1", "user1", "pw", Arrays.asList(this.roleService.getAllRoles().get(0)));
    final User u2 =
        new User("2", "user2", "pw", Arrays.asList(this.roleService.getAllRoles().get(0)));
    final User u3 = new User("3", "user3", "pw", null);

    this.userCrudService.saveNewEntity(u1);
    this.userCrudService.saveNewEntity(u2);
    this.userCrudService.saveNewEntity(u3);

    final byte[] rawResponseBody = this.target(BASE_URL).queryParam("role", "admin").request()
        .header(HttpHeader.AUTHORIZATION.asString(), this.getAdminToken()).get(byte[].class);

    final List<User> adminUsers =
        this.getJsonApiConverter().readDocumentCollection(rawResponseBody, User.class).get();

    assertEquals("Did not found all admin users", 2L, adminUsers.size());
  }


  @Test
  public void removeUser() throws DocumentSerializationException {

    final User u = new User("1", "user1", "pw", Arrays.asList(new Role("admin")));

    this.userCrudService.saveNewEntity(u);

    final Response deleteResponse = this.target("v1/users/" + u.getId()).request()
        .header(HttpHeader.AUTHORIZATION.asString(), this.getAdminToken()).delete();
    final Response getResponse = this.target("v1/users/" + u.getId()).request()
        .header(HttpHeader.AUTHORIZATION.asString(), this.getAdminToken()).get();

    assertEquals(HttpStatus.NO_CONTENT_204, deleteResponse.getStatus());
    assertEquals(HttpStatus.NOT_FOUND_404, getResponse.getStatus());
  }

  @Test
  public void testRetrieveSettings() throws DocumentSerializationException {
    final UserSettings settings = new UserSettings();
    final User u = new User("1", "user1", "pw", null, settings);

    this.userCrudService.saveNewEntity(u);

    final byte[] retrievedUserRaw = this.target("v1/users/" + u.getId()).request()
        .header(HttpHeader.AUTHORIZATION.asString(), this.getAdminToken()).get(byte[].class);

    final User retrievedUser =
        this.getJsonApiConverter().readDocument(retrievedUserRaw, User.class).get();

    assertEquals("Settings do not match", settings, retrievedUser.getSettings());

  }

}
