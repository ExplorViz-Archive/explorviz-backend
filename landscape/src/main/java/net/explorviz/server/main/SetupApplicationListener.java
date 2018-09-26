package net.explorviz.server.main;

import javax.inject.Inject;
import javax.servlet.annotation.WebListener;
import net.explorviz.repository.LandscapeExchangeService;
import net.explorviz.shared.annotations.Config;
import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEvent.Type;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Primary starting class - executed, when the servlet context is started.
 */
@WebListener
public class SetupApplicationListener implements ApplicationEventListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(SetupApplicationListener.class);

  @Inject
  private LandscapeExchangeService exchangeService;

  @Config("repository.useDummyMode")
  private boolean useDummyMode;

  @Override
  public void onEvent(final ApplicationEvent event) {

    // After this type, CDI (e.g. injected LandscapeExchangeService) has been
    // fullfilled
    final Type t = Type.INITIALIZATION_FINISHED;

    if (event.getType().equals(t)) {
      this.startExplorVizBackend();
      this.startDatabase();
    }
  }

  private void startDatabase() {
    // String hashedPassword = "";
    // try {
    // hashedPassword = PasswordStorage.createHash("admin");
    // } catch (final CannotPerformOperationException e) {
    // LOGGER.error("Couldn't create default admin user : ", e);
    // return;
    // }
  }

  @Override
  public RequestEventListener onRequest(final RequestEvent requestEvent) {
    return null;
  }

  private void startExplorVizBackend() {
    // Start ExplorViz Listener
    this.exchangeService.startRepository();

    LOGGER.info("\n");
    LOGGER.info("* * * * * * * * * * * * * * * * * * *\n"); // NOCS
    LOGGER.info("Server (ExplorViz Backend) sucessfully started.\n");

    if (this.useDummyMode) {
      LOGGER.info("Dummy monitoring data is generated now!\n");
    } else {
      LOGGER.info("Traces can now be processed.!\n");
    }
    LOGGER.info("* * * * * * * * * * * * * * * * * * *\n");
  }

}
