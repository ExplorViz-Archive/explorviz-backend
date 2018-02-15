package net.explorviz.repository;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.nustaq.serialization.FSTConfiguration;

import explorviz.live_trace_processing.reader.IPeriodicTimeSignalReceiver;
import explorviz.live_trace_processing.reader.TimeSignalReader;
import explorviz.live_trace_processing.record.IRecord;
import net.explorviz.model.application.Application;
import net.explorviz.model.application.ApplicationCommunication;
import net.explorviz.model.application.Clazz;
import net.explorviz.model.application.ClazzCommunication;
import net.explorviz.model.application.Component;
import net.explorviz.model.landscape.Landscape;
import net.explorviz.model.landscape.Node;
import net.explorviz.model.landscape.NodeGroup;
import net.explorviz.model.landscape.System;
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

	public final Landscape getLastPeriodLandscape() {
		synchronized (lastPeriodLandscape) {
			return lastPeriodLandscape;
		}
	}

	public final Landscape getLandscape(final long timestamp) throws FileNotFoundException {
		return LandscapePreparer.prepareLandscape(RepositoryStorage.readFromFile(timestamp));
	}

	public final Map<Long, Long> getAvailableLandscapes() {
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
			internalLandscape.clearCommunication();
			internalLandscape.getSystems().clear();
			internalLandscape.getEvents().clear();
			internalLandscape.getExceptions().clear();
			internalLandscape.setOverallCalls(0L);
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
		internalLandscape.getExceptions().clear();
		internalLandscape.setOverallCalls(0L);

		for (final System system : internalLandscape.getSystems()) {
			for (final NodeGroup nodeGroup : system.getNodeGroups()) {
				for (final Node node : nodeGroup.getNodes()) {
					for (final Application app : node.getApplications()) {
						app.getDatabaseQueries().clear();

						for (final ClazzCommunication commu : app.getOutgoingClazzCommunication()) {
							commu.reset();
						}

						resetClazzInstances(app.getComponents());
					}
				}
			}
		}

		for (final ApplicationCommunication commu : internalLandscape.getOutgoingApplicationCommunication()) {
			commu.setRequests(0);
			commu.setAverageResponseTime(0);
		}

	}

	private void resetClazzInstances(final List<Component> components) {
		for (final Component compo : components) {
			for (final Clazz clazz : compo.getClazzes()) {
				clazz.getObjectIds().clear();
				clazz.setInstanceCount(0);
			}

			resetClazzInstances(compo.getChildren());
		}
	}

	public void insertIntoModel(final IRecord inputIRecord) {
		// called every second
		insertionRepositoryPart.insertIntoModel(inputIRecord, internalLandscape, remoteCallRepositoryPart);
	}
}
