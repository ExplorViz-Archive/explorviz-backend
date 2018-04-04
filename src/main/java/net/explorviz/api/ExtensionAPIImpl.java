package net.explorviz.api;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.explorviz.model.helper.TimestampHelper;
import net.explorviz.model.landscape.Landscape;
import net.explorviz.model.store.Timestamp;
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

	static final Logger LOGGER = LoggerFactory.getLogger(ExtensionAPIImpl.class.getName());
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
		Landscape specificLandscape = new Landscape();
		try {
			specificLandscape = service.getLandscape(timestamp);
			return specificLandscape;

		} catch (final FileNotFoundException e) {
			LOGGER.debug("Specific landscape not found!", e.getMessage());
			return specificLandscape;
		}
	}

	/**
	 * Provides the <intervalSize> newest timestamps within the server
	 *
	 * @param intervalSize
	 */
	@Override
	public List<Timestamp> getNewestTimestamps(final int intervalSize) {
		final List<Timestamp> allTimestamps = this.service.getTimestampObjectsInRepo("landscapeRepository");
		return TimestampHelper.filterMostRecentTimestamps(allTimestamps, intervalSize);
	}

	/**
	 * Provides the <intervalSize> oldest timestamps within the server
	 *
	 * @param intervalSize
	 */

	@Override
	public List<Timestamp> getOldestTimestamps(final int intervalSize) {
		final List<Timestamp> allTimestamps = this.service.getTimestampObjectsInRepo("landscapeRepository");
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
		final List<Timestamp> allTimestamps = this.service.getTimestampObjectsInRepo("landscapeRepository");
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
		final List<Timestamp> allTimestamps = this.service.getTimestampObjectsInRepo("landscapeRepository");
		return TimestampHelper.filterTimestampsAfterTimestamp(allTimestamps, afterTimestamp, intervalSize);
	}

	public List<Timestamp> getUploadedTimestamps() {
		final List<Timestamp> allTimestamps = this.service.getTimestampObjectsInRepo("uploadedLandscapeRepository");
		return allTimestamps;
	}

	public Landscape getUploadedLandscape(final long timestamp) {
		Landscape specificLandscape = new Landscape();
		try {
			specificLandscape = service.getLandscape(timestamp);
			return specificLandscape;

		} catch (final FileNotFoundException e) {
			LOGGER.debug("Specific landscape not found!", e.getMessage());
			return specificLandscape;
		}
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
	 * Registers specific model types for the JSONAPI provider (necessary by
	 * extensions, if they want to use specific or custom backend models) - e.g.,
	 * ("Timestamp", Timestamp.class)
	 */
	@Override
	public void registerSpecificModel(final String typeName, final Class<?> type) {
		GenericTypeFinder.typeMap.putIfAbsent(typeName, type);

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
