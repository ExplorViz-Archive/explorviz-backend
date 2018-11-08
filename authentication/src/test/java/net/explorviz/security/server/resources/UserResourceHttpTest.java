package net.explorviz.security.server.resources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

/**
 * This class contains tests for {@link UserResource} using actual http-requests and -responses.
 *
 */
public class UserResourceHttpTest extends JerseyTest {

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


    super.setUp();
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
        this.bind(InMemoryUserCrudService.class).to(UserCrudService.class).in(Singleton.class);

      }
    });
    return c;
  }

  @Test
  public void newUserAsAdminTest() throws InterruptedException, DocumentSerializationException {
    final UserInput u = new UserInput("newuser");
    u.setPassword("pw");
    u.setRoles(Arrays.asList("admin"));
    u.setId(-1L);

    // Marshall to json api object
    final JSONAPIDocument<UserInput> userDoc = new JSONAPIDocument<>(u);
    final byte[] converted = this.jsonApiConverter.writeDocument(userDoc);

    // Send request
    final Entity<byte[]> userEntity = Entity.entity(converted, MEDIA_TYPE);
    final Response response = this.target("v1/users").request()
        .header(HttpHeader.AUTHORIZATION.asString(), this.adminToken).post(userEntity);


    assertEquals(200, response.getStatus());

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
  public void newUserAsNormie() throws DocumentSerializationException {
    final UserInput u = new UserInput("newuser");
    u.setPassword("pw");
    u.setRoles(Arrays.asList("admin"));
    u.setId(-1L);

    // Marshall to json api object
    final JSONAPIDocument<UserInput> userDoc = new JSONAPIDocument<>(u);
    final byte[] converted = this.jsonApiConverter.writeDocument(userDoc);

    // Send request
    final Entity<byte[]> userEntity = Entity.entity(converted, MEDIA_TYPE);
    final Response response = this.target("v1/users").request()
        .header(HttpHeader.AUTHORIZATION.asString(), this.normieToken).post(userEntity);

    assertEquals(403, response.getStatus());
  }

  @Test
  public void newUserAsAnon() throws DocumentSerializationException {
    final UserInput u = new UserInput("newuser");
    u.setPassword("pw");
    u.setRoles(Arrays.asList("admin"));
    u.setId(-1L);

    // Marshall to json api object
    final JSONAPIDocument<UserInput> userDoc = new JSONAPIDocument<>(u);
    final byte[] converted = this.jsonApiConverter.writeDocument(userDoc);

    // Send request
    final Entity<byte[]> userEntity = Entity.entity(converted, MEDIA_TYPE);
    final Response response = this.target("v1/users").request().post(userEntity);

    assertEquals(403, response.getStatus());
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

    public UserInput(final String username) {
      this.username = username;
    }

    public UserInput(final Long id, final String username, final String password,
        final List<String> roles) {
      this.username = username;
      this.id = id;
      this.password = password;
      this.roles = roles;
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
