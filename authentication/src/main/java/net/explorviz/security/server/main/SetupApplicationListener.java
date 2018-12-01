package net.explorviz.security.server.main;

import java.util.Arrays;
import javax.inject.Inject;
import javax.servlet.annotation.WebListener;
import net.explorviz.security.util.PasswordStorage;
import net.explorviz.security.util.PasswordStorage.CannotPerformOperationException;
import net.explorviz.shared.security.model.User;
import net.explorviz.shared.security.model.roles.Role;
import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEvent.Type;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.morphia.Datastore;

/**
 * Primary starting class - executed, when the servlet context is started.
 */
@WebListener
public class SetupApplicationListener implements ApplicationEventListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(SetupApplicationListener.class);

  private static final String ADMIN_NAME = "admin";

  @Inject
  private Datastore datastore;

  @Override
  public void onEvent(final ApplicationEvent event) {

    // After this type, CDI (e.g. injected LandscapeExchangeService) has been
    // fullfilled
    final Type t = Type.INITIALIZATION_FINISHED;


    if (event.getType().equals(t)) {
      try {
        this.initDefaultUser();
      } catch (final CannotPerformOperationException e) {
        if (LOGGER.isWarnEnabled()) {
          LOGGER.warn("Unable to create default user: " + e.getMessage());
        }
      }
    }

  }

  @Override
  public RequestEventListener onRequest(final RequestEvent requestEvent) {
    return null;
  }


  private void initDefaultUser() throws CannotPerformOperationException {

    this.datastore.save(new Role(3L, "admin"));
    final String pw = PasswordStorage.createHash("password");
    this.datastore.save(new User(1L, ADMIN_NAME, pw, Arrays.asList(new Role(3L, "admin"))));
  }

}
