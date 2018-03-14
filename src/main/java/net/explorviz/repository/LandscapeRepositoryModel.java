package net.explorviz.repository;

import java.io.FileNotFoundException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.nustaq.serialization.FSTConfiguration;

import explorviz.live_trace_processing.reader.IPeriodicTimeSignalReceiver;
import explorviz.live_trace_processing.reader.TimeSignalReader;
import explorviz.live_trace_processing.record.IRecord;
import net.explorviz.model.landscape.Landscape;
import net.explorviz.server.main.Configuration;

public final class LandscapeRepositoryModel implements IPeriodicTimeSignalReceiver {
	private static final boolean LOAD_LAST_LANDSCAPE_ON_LOAD = false;
	private static LandscapeRepositoryModel instance = null;

	private volatile Landscape lastPeriodLandscape;
	private final Landscape internalLandscape;
	private final FSTConfiguration fstConf;
	private final InsertionRepositoryPart insertionRepositoryPart;
	private final RemoteCallRepositoryPart remoteCallRepositoryPart;

	private LandscapeRepositoryModel() {
		fstConf = initFSTConf();

		if (LOAD_LAST_LANDSCAPE_ON_LOAD) {
			Landscape readLandscape = null;
			try {
				readLandscape = RepositoryStorage.readFromFile(java.lang.System.currentTimeMillis());
			} catch (final FileNotFoundException e) {
				readLandscape = new Landscape();
			}

			internalLandscape = readLandscape;
		} else {
			internalLandscape = new Landscape();
			internalLandscape.initializeID();
		}

		insertionRepositoryPart = new InsertionRepositoryPart();
		remoteCallRepositoryPart = new RemoteCallRepositoryPart();

		final Landscape l = fstConf.deepCopy(internalLandscape);

		lastPeriodLandscape = LandscapePreparer.prepareLandscape(l);

		new TimeSignalReader(TimeUnit.SECONDS.toMillis(Configuration.outputIntervalSeconds), this).start();
	}

	public static synchronized LandscapeRepositoryModel getInstance() {
		if (LandscapeRepositoryModel.instance == null) {
			LandscapeRepositoryModel.instance = new LandscapeRepositoryModel();
		}
		return LandscapeRepositoryModel.instance;
	}

	public Landscape getLastPeriodLandscape() {
		synchronized (lastPeriodLandscape) {
			return lastPeriodLandscape;
		}
	}

	public Landscape getLandscape(final long timestamp) throws FileNotFoundException {
		return LandscapePreparer.prepareLandscape(RepositoryStorage.readFromFile(timestamp));
	}

	public Map<Long, Long> getAvailableLandscapes() {
		return RepositoryStorage.getAvailableModelsForTimeshift();
	}

	static {
		Configuration.DATABASE_NAMES.add("hsqldb");
		Configuration.DATABASE_NAMES.add("postgres");
		Configuration.DATABASE_NAMES.add("db2");
		Configuration.DATABASE_NAMES.add("mysql");
		Configuration.DATABASE_NAMES.add("neo4j");
		Configuration.DATABASE_NAMES.add("database");
		Configuration.DATABASE_NAMES.add("hypersql");
	}

	public FSTConfiguration initFSTConf() {
		return RepositoryStorage.createFSTConfiguration();
	}

	public void reset() {
		synchronized (internalLandscape) {
			internalLandscape.reset();
		}
	}

	@Override
	public void periodicTimeSignal(final long timestamp) {
		// called every tenth second
		// passed timestamp is nanosecond
		synchronized (internalLandscape) {
			synchronized (lastPeriodLandscape) {

				final long milliseconds = java.lang.System.currentTimeMillis();

				if (Configuration.dummyMode) {
					final Landscape dummyLandscape = LandscapeDummyCreator.createDummyLandscape();
					dummyLandscape.setTimestamp(milliseconds);
					RepositoryStorage.writeToFile(dummyLandscape, milliseconds);
					lastPeriodLandscape = dummyLandscape;
				} else {
					internalLandscape.updateTimestamp(milliseconds);
					RepositoryStorage.writeToFile(internalLandscape, milliseconds);
					final Landscape l = fstConf.deepCopy(internalLandscape);
					lastPeriodLandscape = LandscapePreparer.prepareLandscape(l);
				}
				remoteCallRepositoryPart.checkForTimedoutRemoteCalls();
				resetCommunication();
			}
		}

		RepositoryStorage.cleanUpTooOldFiles(java.lang.System.currentTimeMillis());
	}

	private void resetCommunication() {
		internalLandscape.reset();
	}

	public void insertIntoModel(final IRecord inputIRecord) {
		// called every second
		insertionRepositoryPart.insertIntoModel(inputIRecord, internalLandscape, remoteCallRepositoryPart);
	}
}
