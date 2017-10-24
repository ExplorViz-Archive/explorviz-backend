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
	public String getAPIVersion();

	// Landscape related
	public Landscape getLatestLandscape();

	public Landscape getLandscape(long timestamp);

	// Timestamp related
	public List<Timestamp> getNewestTimestamps(int intervalSize);

	public List<Timestamp> getOldestTimestamps(int intervalSize);

	public List<Timestamp> getPreviousTimestamps(long timestamp, int intervalSize);

	public List<Timestamp> getSubsequentTimestamps(long timestamp, int intervalSize);

}
