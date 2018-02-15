package net.explorviz.api;

import java.util.List;
import java.util.Map;

import net.explorviz.model.landscape.Landscape;
import net.explorviz.model.store.Timestamp;

/**
 * Interface for providing necessary information for the extension API
 *
 * @author Christian Zirkelbach (czi@informatik.uni-kiel.de)
 *
 */
interface IExtensionAPI {
	// Generic
	String getAPIVersion();

	// Landscape related
	Landscape getLatestLandscape();

	Landscape getLandscape(long timestamp);

	// Timestamp related
	List<Timestamp> getNewestTimestamps(int intervalSize);

	List<Timestamp> getOldestTimestamps(int intervalSize);

	List<Timestamp> getPreviousTimestamps(long timestamp, int intervalSize);

	List<Timestamp> getSubsequentTimestamps(long timestamp, int intervalSize);

	// Extension related
	void registerAllCoreModels();

	void registerSpecificCoreModels(Map<String, Class<?>> typeMap);

	void registerSpecificModel(String typeName, Class<?> type);

}
