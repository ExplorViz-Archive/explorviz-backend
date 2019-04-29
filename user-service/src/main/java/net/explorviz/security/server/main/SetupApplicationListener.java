package net.explorviz.security.server.main;

import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.annotation.WebListener;
import net.explorviz.security.services.RoleService;
import net.explorviz.security.services.UserMongoCrudService;
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

  @Inject
  private RoleService roleService;

  @Inject
  private UserMongoCrudService userService;

  @Override
  public void onEvent(final ApplicationEvent event) {

    // After this type, CDI (e.g. injected LandscapeExchangeService) has been
    // fullfilled
    final Type t = Type.INITIALIZATION_FINISHED;


    if (event.getType().equals(t)) {
      try {
        this.createDefaultData();
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


  private void createDefaultData() throws CannotPerformOperationException {

    final List<Role> roleList = this.roleService.getAllRoles();

    for (final Role r : roleList) {
      this.datastore.save(r);
    }


    final String pw = PasswordStorage.createHash("password");

    if (this.datastore.getCount(User.class) == 0) {
      this.userService
          .saveNewEntity(new User(null, ADMIN_NAME, pw, Arrays.asList(roleList.get(0))));
      if (LOGGER.isInfoEnabled()) {
        LOGGER.info("Created default admin");
      }
    }

  }

}
