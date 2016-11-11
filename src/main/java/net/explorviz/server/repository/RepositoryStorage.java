package net.explorviz.server.repository;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import net.explorviz.server.main.Configuration;
import net.explorviz.model.*;
import net.explorviz.model.System;
import net.explorviz.model.helper.CommunicationAccumulator;
import net.explorviz.model.helper.CommunicationAppAccumulator;
import net.explorviz.model.helper.CommunicationTileAccumulator;
import net.explorviz.model.helper.Point;
import net.explorviz.model.helper.ELanguage;
import net.explorviz.server.main.FileSystemHelper;

public class RepositoryStorage {
	private static String FOLDER;
	private static String FOLDER_FOR_TARGET_MODEL;
	private static String FILENAME_FOR_TARGET_MODEL = "targetModel" + Configuration.MODEL_EXTENSION;

	private static final Kryo kryoWriter;

	private static final int HISTORY_INTERVAL_IN_MINUTES = 24 * 60; // one day

	static {
		kryoWriter = createKryoInstance();

		FOLDER = FileSystemHelper.getExplorVizDirectory() + "/" + "landscapeRepository";
		FOLDER_FOR_TARGET_MODEL = FileSystemHelper.getExplorVizDirectory();

		java.lang.System.out.println("writing to " + FOLDER);

		new File(FOLDER).mkdir();
	}

	public static Kryo createKryoInstance() {
		final Kryo result = new Kryo();
		result.register(Landscape.class);
		result.register(System.class);
		result.register(NodeGroup.class);
		result.register(Node.class);
		result.register(Communication.class);
		result.register(CommunicationTileAccumulator.class);
		result.register(CommunicationAccumulator.class);
		result.register(Application.class);
		result.register(ELanguage.class);
		result.register(Component.class);
		result.register(Clazz.class);
		result.register(RuntimeInformation.class);
		result.register(DatabaseQuery.class);
		result.register(CommunicationClazz.class);
		result.register(CommunicationAppAccumulator.class);
		result.register(Point.class);

		return result;
	}

	public static Landscape readTargetArchitecture() {
		try {
			return readFromFileGeneric(FOLDER_FOR_TARGET_MODEL, FILENAME_FOR_TARGET_MODEL);
		} catch (final FileNotFoundException e) {
		}

		return new Landscape();
	}

	public static void saveTargetArchitecture(final Landscape landscape) {
		writeToFileGeneric(landscape, FOLDER_FOR_TARGET_MODEL, FILENAME_FOR_TARGET_MODEL);
	}

	public static void writeToFile(final Landscape landscape, final long timestamp) {
		writeToFileGeneric(landscape, FOLDER, timestamp + "-" + landscape.getActivities()
				+ Configuration.MODEL_EXTENSION);
	}

	private static void writeToFileGeneric(final Landscape landscape, final String destFolder,
			final String destFilename) {
		Output output = null;
		try {
			output = new Output(new FileOutputStream(destFolder + "/" + destFilename));
			kryoWriter.writeObject(output, landscape);
			output.close();
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (output != null) {
				output.close();
			}
		}
	}

	public static Landscape readFromFile(final long timestamp) throws FileNotFoundException {
		final Map<Long, Long> availableModels = getAvailableModels(HISTORY_INTERVAL_IN_MINUTES);
		String readInModel = null;

		for (final Entry<Long, Long> availableModel : availableModels.entrySet()) {
			if (availableModel.getKey() <= timestamp) {
				readInModel = availableModel.getKey() + "-" + availableModel.getValue()
						+ Configuration.MODEL_EXTENSION;
			}
		}

		if (readInModel == null) {
			throw new FileNotFoundException("Model not found for timestamp " + timestamp);
		}

		return readFromFileGeneric(FOLDER, readInModel);
	}

	public static Landscape readFromFileGeneric(final String sourceFolder,
			final String sourceFilename) throws FileNotFoundException {
		final Input input = new Input(new FileInputStream(sourceFolder + "/" + sourceFilename));
		final Kryo kryoReader = createKryoInstance();
		final Landscape landscape = kryoReader.readObject(input, Landscape.class);
		input.close();
		return landscape;
	}

	public static Map<Long, Long> getAvailableModelsForTimeshift() {
		return getAvailableModels(Configuration.TIMESHIFT_INTERVAL_IN_MINUTES);
	}

	private static Map<Long, Long> getAvailableModels(final int minutesBackwards) {
		final Map<Long, Long> result = new TreeMap<Long, Long>();

		final File[] files = new File(FOLDER).listFiles();
		for (final File file : files) {
			if (isExplorVizFile(file)) {
				final String[] split = file.getName().split("-");
				final long timestamp = Long.parseLong(split[0]);

				if ((java.lang.System.currentTimeMillis() - TimeUnit.MINUTES
						.toMillis(minutesBackwards)) < timestamp) {
					final long activities = Long.parseLong(split[1].split("\\.")[0]);
					result.put(timestamp, activities);
				}
			}
		}

		return result;
	}

	public static void cleanUpTooOldFiles(final long currentTimestamp) {
		final long enddate = currentTimestamp
				- TimeUnit.MINUTES.toMillis(HISTORY_INTERVAL_IN_MINUTES);
		final File[] files = new File(FOLDER).listFiles();
		for (final File file : files) {
			if (isExplorVizFile(file)) {
				if (Long.parseLong(file.getName().substring(0, file.getName().indexOf("-"))) <= enddate) {
					file.delete();
				}
			}
		}
	}

	public static void clearRepository() {
		final File[] files = new File(FOLDER).listFiles();
		for (final File file : files) {
			if (isExplorVizFile(file)) {
				file.delete();
			}
		}
	}

	public static boolean isExplorVizFile(final File file) {
		return !file.getName().equals(".") && !file.getName().equals("..")
				&& file.getName().endsWith(Configuration.MODEL_EXTENSION);
	}
}
