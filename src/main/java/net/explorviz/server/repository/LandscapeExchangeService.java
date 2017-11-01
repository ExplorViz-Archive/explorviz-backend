package net.explorviz.server.repository;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import net.explorviz.model.Landscape;
import net.explorviz.model.Timestamp;
import net.explorviz.model.TimestampStorage;
import net.explorviz.server.main.FileSystemHelper;

public class LandscapeExchangeService {
	private static LandscapeExchangeService instance;
	private static LandscapeRepositoryModel model;

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

		for (final File f : fList) {

			final String filename = f.getName();

			if (filename.endsWith(".expl")) {

				// first validation check -> filename
				long timestamp;
				// long activity;

				try {
					timestamp = Long.parseLong(filename.split("-")[0]);
					// activity = Long.parseLong(filename.split("-")[1].split(".expl")[0]);
				} catch (final NumberFormatException e) {
					continue;
				}

				// second validation check -> deserialization
				try {
					// getLandscape(timestamp, activity);
					getLandscape(timestamp);
				} catch (final Exception e) {
					continue;
				}

				names.add(filename);
			}

		}

		return names;
	}

	public TimestampStorage getTimestampObjectsInRepo() {
		final File directory = new File(REPOSITORY_FOLDER);
		final File[] fList = directory.listFiles();

		final TimestampStorage timestampStorage = new TimestampStorage("0");

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
				// try {
				// //getLandscape(timestamp, activity);
				// getLandscape(timestamp);
				// } catch (final Exception e) {
				// continue;
				// }

				final Timestamp newTimestamp = new Timestamp(String.valueOf(timestamp), activity);
				timestampStorage.addTimestamp(newTimestamp);
			}
		}
		return timestampStorage;
	}

	@Deprecated
	public List<String> getNamesInRepo() {
		final List<String> names = new ArrayList<String>();

		final File directory = new File(REPOSITORY_FOLDER);

		final File[] fList = directory.listFiles();

		for (final File f : fList) {

			final String filename = f.getName();

			if (filename.endsWith(".expl")) {

				// first validation check -> filename
				long timestamp;
				// long activity;

				try {
					timestamp = Long.parseLong(filename.split("-")[0]);
					// activity = Long.parseLong(filename.split("-")[1].split(".expl")[0]);
				} catch (final NumberFormatException e) {
					continue;
				}

				// second validation check -> deserialization
				try {
					// getLandscape(timestamp, activity);
					getLandscape(timestamp);
				} catch (final Exception e) {
					continue;
				}

				names.add(filename);
			}

		}

		return names;
	}

	public Landscape getLandscape(final long timestamp) throws FileNotFoundException {
		return model.getLandscape(timestamp);
	}

	public void resetLandscape() {
		timestamp = null;
		activity = null;

		model.reset();
	}

	public static void startRepository() {
		model = LandscapeRepositoryModel.getInstance();
		new Thread(new Runnable() {

			@Override
			public void run() {
				new RepositoryStarter().start(model);
			}
		}).start();
	}
}