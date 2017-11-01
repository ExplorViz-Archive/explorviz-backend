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
public final class ExtensionAPIImpl implements IExtensionAPI {

	private static ExtensionAPIImpl instance;

	String versionNumber = "0.1a";
	LandscapeExchangeService service;
	TimestampStorage timestampStorage;

	private ExtensionAPIImpl() {
		this.service = LandscapeExchangeService.getInstance();
	}

	public static synchronized ExtensionAPIImpl getInstance() {
		if (ExtensionAPIImpl.instance == null) {
			ExtensionAPIImpl.instance = new ExtensionAPIImpl();
		}
		return ExtensionAPIImpl.instance;
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
	public Landscape getLandscape(final long timestamp) {
		try {
			return service.getLandscape(timestamp);
		} catch (final FileNotFoundException e) {
			System.err.println(e);
			return null;
		}
	}

	/**
	 * Provides the <intervalSize> newest timestamps within the server
	 *
	 * @param intervalSize
	 */
	@Override
	public List<Timestamp> getNewestTimestamps(final int intervalSize) {
		this.updateTimestampStorage();
		return timestampStorage.filterMostRecentTimestamps(intervalSize);
	}

	/**
	 * Provides the <intervalSize> oldest timestamps within the server
	 *
	 * @param intervalSize
	 */

	@Override
	public List<Timestamp> getOldestTimestamps(final int intervalSize) {
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
	public List<Timestamp> getPreviousTimestamps(final long fromTimestamp, final int intervalSize) {
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
	public List<Timestamp> getSubsequentTimestamps(final long afterTimestamp, final int intervalSize) {
		this.updateTimestampStorage();
		return timestampStorage.filterTimestampsAfterTimestamp(afterTimestamp, intervalSize);
	}
}
