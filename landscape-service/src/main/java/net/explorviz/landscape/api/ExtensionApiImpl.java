package net.explorviz.landscape.api;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.explorviz.landscape.repository.LandscapeExchangeService;
import net.explorviz.landscape.server.main.Application;
import net.explorviz.shared.common.provider.GenericTypeFinder;
import net.explorviz.shared.config.helper.PropertyHelper;
import net.explorviz.shared.landscape.model.landscape.Landscape;
import org.jvnet.hk2.annotations.Service;

/**
 * Interface implementation of the extension API - Offers version information and provides
 * landscapes and timestamps.
 */
@Service
@Singleton
public final class ExtensionApiImpl implements IExtensionApi {



  private static final String VERSION_NUMBER = "1.3.0";
  private final LandscapeExchangeService service;

  @Inject
  public ExtensionApiImpl(final LandscapeExchangeService service) {
    this.service = service;
  }

  /**
   * Reveals the currently provides API version.
   */
  @Override
  public String getApiVersion() {
    return "ExplorViz Extension API version: " + ExtensionApiImpl.VERSION_NUMBER;
  }

  /**
   * Provides the current landscape.
   *
   * @return Latest landscape model
   */
  @Override
  public Landscape getLatestLandscape() {
    return this.service.getCurrentLandscape();
  }

  /**
   * Provides a specific landscape determined by a passed timestamp.
   *
   * @param timestamp - As configured in Kieker
   */
  @Override
  public Landscape getLandscape(final long timestamp) {
    return this.service.getLandscape(timestamp);
  }

  /**
   * Registers specific core model types for the JSONAPI provider (necessary by extensions, if they
   * want to use specific backend models) - e.g., ("Timestamp", Timestamp.class)
   */
  @Override
  public void registerSpecificCoreModels(final Map<String, Class<?>> typeMap) {
    for (final Map.Entry<String, Class<?>> entry : typeMap.entrySet()) {
      GenericTypeFinder.getTypeMap().putIfAbsent(entry.getKey(), entry.getValue());
    }

  }

  /**
   * Registers specific model types for the JSONAPI provider (necessary by extensions, if they want
   * to use specific or custom backend models) - e.g., ("Timestamp", Timestamp.class)
   */
  @Override
  public void registerSpecificModel(final String typeName, final Class<?> type) {
    GenericTypeFinder.getTypeMap().putIfAbsent(typeName, type);

  }

  /**
   * Registers all core model types for the JSONAPI provider (necessary for extensions, if they want
   * to use backend models).
   */
  @Override
  public void registerAllCoreModels() {
    Application.registerLandscapeModels();
  }

  /**
   * Enable / disable dummy mode true = Use dummy monitoring data instead of real monitoring data.
   *
   * @throws IOException - Thrown when cast to Boolean failed
   * @throws FileNotFoundException - Thrown when
   */
  @Override
  public void setDummyMode(final boolean value) throws FileNotFoundException, IOException {
    PropertyHelper.setBooleanProperty("useDummyMode", value);
  }


}
