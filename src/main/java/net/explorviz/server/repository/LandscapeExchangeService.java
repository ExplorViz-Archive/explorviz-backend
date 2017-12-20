package net.explorviz.server.repository;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.explorviz.kiekeradapter.main.KiekerAdapter;
import net.explorviz.model.Landscape;
import net.explorviz.model.Timestamp;
import net.explorviz.model.TimestampStorage;
import net.explorviz.server.main.Configuration;
import net.explorviz.server.main.FileSystemHelper;

/**
 * Exchange Service for timestamps and landscapes - used by resources (REST)
 *
 * @author Christian Zirkelbach (czi@informatik.uni-kiel.de)
 *
 */
public class LandscapeExchangeService {
	private static final Logger LOGGER = LoggerFactory.getLogger(LandscapeExchangeService.class.getName());

	private static LandscapeExchangeService instance;
	private static LandscapeRepositoryModel model;
	private static KiekerAdapter adapter;

	@SuppressWarnings("unused")
	private static Long timestamp;
	@SuppressWarnings("unused")
	private static Long activity;

	private static final String REPLAY_FOLDER = FileSystemHelper.getExplorVizDirectory() + File.separator + "replay";
	private static final String REPOSITORY_FOLDER = FileSystemHelper.getExplorVizDirectory() + File.separator
			+ "landscapeRepository";

	public static synchronized LandscapeExchangeService getInstance() {
		if (LandscapeExchangeService.instance == null) {
			LandscapeExchangeService.instance = new LandscapeExchangeService();
		}
		return LandscapeExchangeService.instance;
	}

	public Landscape getLandscapeByTimestampAndActivity(final long timestamp, final long activity) {
		LandscapeExchangeService.timestamp = timestamp;
		LandscapeExchangeService.activity = activity;
		return getCurrentLandscape();
	}

	public LandscapeRepositoryModel getModel() {
		return model;
	}

	public Landscape getCurrentLandscape() {
		return model.getLastPeriodLandscape();
	}

	public List<String> getReplayNames() {
		final List<String> names = new ArrayList<String>();
		final File directory = new File(REPLAY_FOLDER);
		final File[] fList = directory.listFiles();

		if (fList != null) {
			for (final File f : fList) {
				final String filename = f.getName();

				if (filename.endsWith(".expl")) {
					// first validation check -> filename
					long timestamp;

					try {
						timestamp = Long.parseLong(filename.split("-")[0]);
					} catch (final NumberFormatException e) {
						LOGGER.warn(e.getMessage());
						continue;
					}

					// second validation check -> deserialization
					try {
						this.getLandscape(timestamp);
					} catch (final FileNotFoundException e) {
						LOGGER.warn(e.getMessage());
						continue;
					}
					names.add(filename);
				}
			}
		}
		return names;
	}

	public TimestampStorage getTimestampObjectsInRepo() {
		final File directory = new File(REPOSITORY_FOLDER);
		final File[] fList = directory.listFiles();
		final TimestampStorage timestampStorage = new TimestampStorage();

		if (fList != null) {
			for (final File f : fList) {
				final String filename = f.getName();

				if (filename.endsWith(".expl")) {
					// first validation check -> filename
					long timestamp;
					long activity;

					try {
						timestamp = Long.parseLong(filename.split("-")[0]);
						activity = Long.parseLong(filename.split("-")[1].split(".expl")[0]);
					} catch (final NumberFormatException e) {
						continue;
					}

					// second validation check -> deserialization
					try {
						// getLandscape(timestamp, activity);
						this.getLandscape(timestamp);
					} catch (final FileNotFoundException e) {
						LOGGER.warn(e.getMessage());
						continue;
					}
					final Timestamp newTimestamp = new Timestamp(activity);
					timestampStorage.addTimestamp(newTimestamp);
				}
			}
		}
		return timestampStorage;
	}

	@Deprecated
	public List<String> getNamesInRepo() {
		final List<String> names = new ArrayList<String>();
		final File directory = new File(REPOSITORY_FOLDER);
		final File[] fList = directory.listFiles();

		if (fList != null) {
			for (final File f : fList) {
				final String filename = f.getName();

				if (filename.endsWith(".expl")) {
					// first validation check -> filename
					long timestamp;

					try {
						timestamp = Long.parseLong(filename.split("-")[0]);
					} catch (final NumberFormatException e) {
						LOGGER.warn(e.getMessage());
						continue;
					}

					// second validation check -> deserialization
					try {
						this.getLandscape(timestamp);
					} catch (final FileNotFoundException e) {
						LOGGER.warn(e.getMessage());
						continue;
					}
					names.add(filename);
				}
			}
		}
		return names;
	}

	public Landscape getLandscape(final long timestamp) throws FileNotFoundException {
		return model.getLandscape(timestamp);
	}

	public static void startRepository() {
		model = LandscapeRepositoryModel.getInstance();
		new Thread(new Runnable() {

			@Override
			public void run() {
				new RepositoryStarter().start(model);
			}
		}).start();

		// Start Kieker monitoring adapter
		if (Configuration.ENABLE_KIEKER_ADAPTER) {
			adapter = KiekerAdapter.getInstance();
			new Thread(new Runnable() {

				@Override
				public void run() {
					adapter.startReader();
				}
			}).start();
		}
	}
}