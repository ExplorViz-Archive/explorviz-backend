package net.explorviz.landscape.server.main;

import net.explorviz.landscape.model.helper.TypeProvider;
import net.explorviz.shared.common.provider.GenericTypeFinder;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;

/**
 * Starts the Java application.
 */
public final class Main {

  private Main() {
    // no instantiation
  }

  /**
   * Configures / starts {@link TypeProvider}, the dependency injection, and the actual landscape
   * service via the {@link LandscapeApplication}.
   *
   */
  public static void main(final String[] args) {
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
