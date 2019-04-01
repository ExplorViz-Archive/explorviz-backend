package net.explorviz.settings.server.main;

import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.annotation.WebListener;
import net.explorviz.shared.security.model.User;
import net.explorviz.shared.security.model.roles.Role;
import net.explorviz.shared.security.model.settings.UserSettings;
import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;
import org.glassfish.jersey.server.monitoring.ApplicationEvent.Type;
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
      
    }

  }

  @Override
  public RequestEventListener onRequest(final RequestEvent requestEvent) {
    return null;
  }




}