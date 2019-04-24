package net.explorviz.landscape.server.main;

import javax.inject.Inject;
import javax.servlet.annotation.WebListener;
import net.explorviz.landscape.repository.LandscapeRepositoryModel;
import net.explorviz.landscape.repository.RepositoryStarter;
import net.explorviz.shared.common.idgen.IdGenerator;
import net.explorviz.shared.config.annotations.Config;
import net.explorviz.shared.landscape.model.helper.BaseEntity;
import org.glassfish.hk2.api.ServiceLocator;
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

  private static ServiceLocator LOCATOR;

  public static ServiceLocator getServiceLocator() {
    return LOCATOR;
  }

  @Inject
  private ServiceLocator serviceLocator;


  @Inject
  private IdGenerator idGenerator;

  LandscapeRepositoryModel model;

  @Config("repository.useDummyMode")
  private boolean useDummyMode;

  @Override
  public void onEvent(final ApplicationEvent event) {

    // After this type, CDI (e.g. injected LandscapeExchangeService) has been
    // fullfilled
    final Type t = Type.INITIALIZATION_FINISHED;

    if (event.getType().equals(t)) {
      BaseEntity.initialize(this.idGenerator);
      SetupApplicationListener.LOCATOR = this.serviceLocator;

      // Workaround: Can't be injected directly since BaseEntity.initialize(..) must be called
      // first.
      this.model = this.serviceLocator.createAndInitialize(LandscapeRepositoryModel.class);

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
    new Thread(new Runnable() {

      @Override
      public void run() {
        new RepositoryStarter().start(SetupApplicationListener.this.model);
      }
    }).start();

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
