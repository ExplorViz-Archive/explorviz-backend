package net.explorviz.landscape.server.main;

import io.prometheus.client.exporter.HTTPServer;
import io.prometheus.client.hotspot.DefaultExports;
import net.explorviz.landscape.model.helper.TypeProvider;
import net.explorviz.shared.common.provider.GenericTypeFinder;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Starts the Java application.
 */
public final class Main {

  private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
  private static final int PROM_PORT = 1235;

  private Main() {
    // no instantiation
  }

  /**
   * Configures / starts {@link TypeProvider}, the dependency injection, and the actual landscape
   * service via the {@link LandscapeApplication}.
   *
   */
  public static void main(final String[] args) {

    try {
      // Starts the server
      new HTTPServer(PROM_PORT);
      // JVM Metrics
      DefaultExports.initialize();
      LOGGER.info("Started prometheus server on port " + PROM_PORT);
    } catch (IOException e) {
      LOGGER.warn("Failed to start prometheus HTTP server", e);
    }

    // register landscape model classes
    // this must happen before initializing the ServiceLocator
    TypeProvider.getExplorVizCoreTypesAsMap().forEach((classname, classRef) -> {
      GenericTypeFinder.getTypeMap().put(classname, classRef);
    });

    final ServiceLocator locator = ServiceLocatorUtilities.bind(new DependencyInjectionBinder());
    final LandscapeApplication app = locator.createAndInitialize(LandscapeApplication.class);
    app.startApplication();
  }



}
