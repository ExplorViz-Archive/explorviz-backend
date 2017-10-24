package net.explorviz.api;

import java.io.FileNotFoundException;
import java.util.List;

import net.explorviz.model.Landscape;
import net.explorviz.model.Timestamp;
import net.explorviz.model.TimestampStorage;
import net.explorviz.server.repository.LandscapeExchangeService;

/**
 * Interface implementation of the extension API - Offers version information
 * and provides landscapes and timestamps
 * 
 * @author Christian Zirkelbach (czi@informatik.uni-kiel.de)
 *
 */
public class ExtensionAPIImpl implements IExtensionAPI {

	String versionNumber = "0.1a";
	LandscapeExchangeService service;
	TimestampStorage timestampStorage;

	private static ExtensionAPIImpl INSTANCE = null;

	public static synchronized ExtensionAPIImpl getInstance() {
		if (ExtensionAPIImpl.INSTANCE == null) {
			ExtensionAPIImpl.INSTANCE = new ExtensionAPIImpl();
		}
		return ExtensionAPIImpl.INSTANCE;
	}

	private ExtensionAPIImpl() {
		this.service = LandscapeExchangeService.getInstance();
	}

	/**
	 * Updates the held timestamp storage for queries
	 */
	public synchronized void updateTimestampStorage() {
		this.timestampStorage = this.service.getTimestampObjectsInRepo();
	}

	/**
	 * Reveals the currently provides API version
	 */
	@Override
	public String getAPIVersion() {
		return "ExplorViz Extension API version: " + versionNumber;
	}

	/**
	 * Provides the current landscape
	 */
	@Override
	public Landscape getLatestLandscape() {
		return service.getCurrentLandscape();
	}

	/**
	 * Provides a specific landscape determined by a passed timestamp
	 * 
	 * @param timestamp
	 */
	@Override
	public Landscape getLandscape(long timestamp) {
		try {
			return service.getLandscape(timestamp);
		} catch (FileNotFoundException e) {
			e.getMessage();
			return null;
		}
	}

	/**
	 * Provides the <intervalSize> newest timestamps within the server
	 * 
	 * @param intervalSize
	 */
	@Override
	public List<Timestamp> getNewestTimestamps(int intervalSize) {
		this.updateTimestampStorage();
		return timestampStorage.filterMostRecentTimestamps(intervalSize);
	}

	/**
	 * Provides the <intervalSize> oldest timestamps within the server
	 * 
	 * @param intervalSize
	 */

	@Override
	public List<Timestamp> getOldestTimestamps(int intervalSize) {
		this.updateTimestampStorage();
		return timestampStorage.filterOldestTimestamps(intervalSize);
	}

	/**
	 * Provides the <intervalSize> timestamps before a passed <fromTimestamp> within
	 * the server
	 * 
	 * @param fromTimestamp,
	 *            intervalSize
	 */
	@Override
	public List<Timestamp> getPreviousTimestamps(long fromTimestamp, int intervalSize) {
		this.updateTimestampStorage();
		return timestampStorage.filterTimestampsBeforeTimestamp(fromTimestamp, intervalSize);
	}

	/**
	 * Provides the <intervalSize> timestamps after a passed <afterTimestamp> within
	 * the server
	 * 
	 * @param afterTimestamp,
	 *            intervalSize
	 */
	@Override
	public List<Timestamp> getSubsequentTimestamps(long afterTimestamp, int intervalSize) {
		this.updateTimestampStorage();
		return timestampStorage.filterTimestampsAfterTimestamp(afterTimestamp, intervalSize);
	}

	/**
	 * Add the <classToRegister> Class to the JSON API ResourceConverter.
	 * 
	 * @param classToRegister
	 */
	@Override
	public void addClassToResourceConverter(Class<?> classToRegister) {

	}
}
