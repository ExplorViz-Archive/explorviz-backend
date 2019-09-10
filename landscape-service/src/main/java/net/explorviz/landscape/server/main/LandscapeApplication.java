package net.explorviz.landscape.server.main;

import javax.inject.Inject;
import net.explorviz.landscape.repository.LandscapeRepositoryModel;
import net.explorviz.landscape.repository.RepositoryStarter;
import net.explorviz.shared.config.annotations.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Entry point to the core logic of the ExplorViz landscape service with DI.
 */
public class LandscapeApplication {

  private static final Logger LOGGER = LoggerFactory.getLogger(LandscapeApplication.class);

  private final boolean useDummyMode;

  private final LandscapeRepositoryModel model;

  @Inject
  public LandscapeApplication(final LandscapeRepositoryModel model,
      @Config("repository.useDummyMode") final boolean useDummyMode) {
    this.useDummyMode = useDummyMode;
    this.model = model;
  }

  /**
   * Starts the core logic of this application.
   */
  public void startApplication() {
    // Start ExplorViz Listener
    new Thread(new Runnable() {

      @Override
      public void run() {
        new RepositoryStarter().start(LandscapeApplication.this.model);
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
