package net.explorviz.api;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import net.explorviz.model.landscape.Landscape;
import net.explorviz.model.store.Timestamp;

/**
 * Interface for providing necessary information for the extension API.
 */
interface IExtensionApi {
  // Generic
  String getApiVersion();

  // Landscape related
  Landscape getLatestLandscape();

  Landscape getLandscape(long timestamp, String folderName);

  // Timestamp related
  List<Timestamp> getNewestTimestamps(int intervalSize);

  List<Timestamp> getOldestTimestamps(int intervalSize);

  List<Timestamp> getPreviousTimestamps(long timestamp, int intervalSize);

  List<Timestamp> getSubsequentTimestamps(long timestamp, int intervalSize);

  // Extension related
  void registerAllCoreModels();

  void registerSpecificCoreModels(Map<String, Class<?>> typeMap);

  void registerSpecificModel(String typeName, Class<?> type);

  void setDummyMode(boolean value) throws FileNotFoundException, IOException;

  void saveLandscapeToFile(Landscape landscape, String folderName);

  List<Timestamp> getUploadedTimestamps();

}
