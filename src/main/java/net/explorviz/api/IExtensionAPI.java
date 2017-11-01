package net.explorviz.api;

import java.util.List;

import net.explorviz.model.Landscape;
import net.explorviz.model.Timestamp;

/**
 * Interface for providing necessary information for the extension API
 *
 * @author Christian Zirkelbach (czi@informatik.uni-kiel.de)
 *
 */
interface IExtensionAPI {
	String getAPIVersion();

	// Landscape related
	Landscape getLatestLandscape();

	Landscape getLandscape(long timestamp);

	// Timestamp related
	List<Timestamp> getNewestTimestamps(int intervalSize);

	List<Timestamp> getOldestTimestamps(int intervalSize);

	List<Timestamp> getPreviousTimestamps(long timestamp, int intervalSize);

	List<Timestamp> getSubsequentTimestamps(long timestamp, int intervalSize);

}
