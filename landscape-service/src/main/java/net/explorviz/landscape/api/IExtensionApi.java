package net.explorviz.landscape.api;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import net.explorviz.shared.landscape.model.landscape.Landscape;
import net.explorviz.shared.landscape.model.store.Timestamp;

/**
 * Interface for providing necessary information for the extension API.
 */
interface IExtensionApi {
  // Generic
  String getApiVersion();

  // Landscape related
  Landscape getLatestLandscape();

  Landscape getLandscape(long timestamp);

  // Timestamp related
  List<Timestamp> getSubsequentTimestamps(long timestamp, int intervalSize);

  // Extension related
  void registerAllCoreModels();

  void registerSpecificCoreModels(Map<String, Class<?>> typeMap);

  void registerSpecificModel(String typeName, Class<?> type);

  void setDummyMode(boolean value) throws FileNotFoundException, IOException;

  List<Timestamp> getUploadedTimestamps();

}
