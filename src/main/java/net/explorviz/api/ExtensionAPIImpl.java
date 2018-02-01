package net.explorviz.api;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

import org.glassfish.jersey.server.ParamException.QueryParamException;

import net.explorviz.model.Landscape;
import net.explorviz.model.Timestamp;
import net.explorviz.model.helper.TimestampHelper;
import net.explorviz.repository.LandscapeExchangeService;
import net.explorviz.server.providers.CoreModelHandler;
import net.explorviz.server.providers.GenericTypeFinder;

/**
 * Interface implementation of the extension API - Offers version information
 * and provides landscapes and timestamps
 *
 * @author Christian Zirkelbach (czi@informatik.uni-kiel.de)
 *
 */
public final class ExtensionAPIImpl implements IExtensionAPI {

	private static ExtensionAPIImpl instance;

	String versionNumber = "1.2.0a";
	LandscapeExchangeService service;

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
			throw new QueryParamException(e, "Error in ExtensionAPI.getLandscape", "10");
		}
	}

	/**
	 * Provides the <intervalSize> newest timestamps within the server
	 *
	 * @param intervalSize
	 */
	@Override
	public List<Timestamp> getNewestTimestamps(final int intervalSize) {
		final List<Timestamp> allTimestamps = this.service.getTimestampObjectsInRepo();
		return TimestampHelper.filterMostRecentTimestamps(allTimestamps, intervalSize);
	}

	/**
	 * Provides the <intervalSize> oldest timestamps within the server
	 *
	 * @param intervalSize
	 */

	@Override
	public List<Timestamp> getOldestTimestamps(final int intervalSize) {
		final List<Timestamp> allTimestamps = this.service.getTimestampObjectsInRepo();
		return TimestampHelper.filterOldestTimestamps(allTimestamps, intervalSize);
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
		final List<Timestamp> allTimestamps = this.service.getTimestampObjectsInRepo();
		return TimestampHelper.filterTimestampsBeforeTimestamp(allTimestamps, fromTimestamp, intervalSize);
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
		final List<Timestamp> allTimestamps = this.service.getTimestampObjectsInRepo();
		return TimestampHelper.filterTimestampsAfterTimestamp(allTimestamps, afterTimestamp, intervalSize);
	}

	/**
	 * Registers specific core model types for the JSONAPI provider (necessary by
	 * extensions, if they want to use specific backend models) - e.g.,
	 * ("Timestamp", Timestamp.class)
	 */
	@Override
	public void registerSpecificCoreModels(final Map<String, Class<?>> typeMap) {
		for (final Map.Entry<String, Class<?>> entry : typeMap.entrySet()) {
			GenericTypeFinder.typeMap.putIfAbsent(entry.getKey(), entry.getValue());
		}

	}

	/**
	 * Registers all core model types for the JSONAPI provider (necessary by
	 * extensions, if they want to use all backend models)
	 */
	@Override
	public void registerAllCoreModels() {
		CoreModelHandler.registerAllCoreModels();

	}
}
