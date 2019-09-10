package net.explorviz.landscape.server.main;

import net.explorviz.landscape.model.helper.TypeProvider;
import net.explorviz.shared.common.provider.GenericTypeFinder;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Main {

  private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
  private static final int DEFAULT_PORT = 8081;

  private Main() {

  }

  /**
   * Starts the landscape service server.
   *
   */
  public static void main(final String[] args) {
    // register Landscape Model classes, since we want to use them
    // this must happen before initializing the ServiceLocator
    TypeProvider.getExplorVizCoreTypesAsMap().forEach((classname, classRef) -> {
      GenericTypeFinder.getTypeMap().put(classname, classRef);
    });
    ServiceLocator locator = ServiceLocatorUtilities.bind(new DependencyInjectionBinder());
    LandscapeApplication app = locator.createAndInitialize(LandscapeApplication.class);
    app.startExplorVizBackend();
  }



}
