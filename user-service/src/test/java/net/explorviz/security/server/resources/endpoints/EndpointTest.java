package net.explorviz.security.server.resources.endpoints;

import com.github.jasminb.jsonapi.ResourceConverter;
import java.util.Arrays;
import javax.inject.Inject;
import javax.ws.rs.core.Application;
import net.explorviz.security.server.main.DependencyInjectionBinder;
import net.explorviz.security.services.TokenService;
import net.explorviz.shared.security.model.User;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.HttpUrlConnectorProvider;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;

/**
 * Abstract base class for all endpoint tests.
 */
public abstract class EndpointTest extends JerseyTest {


  @Inject
  private ResourceConverter jsonApiConverter;

  @Inject
  private TokenService tokenService;


  private String adminToken;
  private String normieToken;

  protected String getAdminToken() {
    return this.adminToken;
  }

  protected String getNormieToken() {
    return this.normieToken;
  }

  protected ResourceConverter getJsonApiConverter() {
    return this.jsonApiConverter;
  }

  @Override
  public void setUp() throws Exception {
    final DependencyInjectionBinder binder = new DependencyInjectionBinder();
    this.overrideTestBindings(binder);
    final ServiceLocator locator = ServiceLocatorUtilities.bind(binder);
    locator.inject(this);

    this.createDefaultData();

    super.setUp();
  }

  private void createDefaultData() {
    final User admin = new User("Admin");
    admin.setRoles(Arrays.asList(Role.ADMIN));
    final User normie = new User("Normie");

    this.adminToken = "Bearer " + this.tokenService.issueNewToken(admin);
    this.normieToken = "Bearer " + this.tokenService.issueNewToken(normie);
  }

  @Override
  protected void configureClient(final ClientConfig config) {
    // Allow PATCH-Requests
    config.property(HttpUrlConnectorProvider.SET_METHOD_WORKAROUND, true);
  }

  @Override
  protected Application configure() {
    final ResourceConfig c =
        new ResourceConfig(new net.explorviz.security.server.main.Application());

    final AbstractBinder b = this.overrideApplicationBindings();
    c.register(b);
    return c;
  }


  protected void overrideTestBindings(final DependencyInjectionBinder binder) {};

  protected AbstractBinder overrideApplicationBindings() {
    return new DependencyInjectionBinder();
  };



}
