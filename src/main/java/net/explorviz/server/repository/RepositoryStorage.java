package net.explorviz.server.repository;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.nustaq.serialization.FSTConfiguration;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;

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

	private static final FSTConfiguration fstConf;

	private static final int HISTORY_INTERVAL_IN_MINUTES = 24 * 60; // one day

	static {
		fstConf = createFSTConfiguration();

		FOLDER = FileSystemHelper.getExplorVizDirectory() + "/" + "landscapeRepository";
		FOLDER_FOR_TARGET_MODEL = FileSystemHelper.getExplorVizDirectory();

		java.lang.System.out.println("writing to " + FOLDER);

		new File(FOLDER).mkdir();
	}

	public static FSTConfiguration createFSTConfiguration() {
		final FSTConfiguration result = FSTConfiguration.createDefaultConfiguration();
		result.registerClass(Landscape.class);
		result.registerClass(System.class);
		result.registerClass(NodeGroup.class);
		result.registerClass(Node.class);
		result.registerClass(Communication.class);
		result.registerClass(CommunicationTileAccumulator.class);
		result.registerClass(CommunicationAccumulator.class);
		result.registerClass(Application.class);
		result.registerClass(ELanguage.class);
		result.registerClass(Component.class);
		result.registerClass(Clazz.class);
		result.registerClass(RuntimeInformation.class);
		result.registerClass(DatabaseQuery.class);
		result.registerClass(CommunicationClazz.class);
		result.registerClass(CommunicationAppAccumulator.class);
		result.registerClass(Point.class);

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
		FSTObjectOutput output = null;
		try {
			output = fstConf.getObjectOutput(new FileOutputStream(destFolder + "/" + destFilename));
			output.writeObject(landscape, Landscape.class);
			output.close();
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
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
		
		final FileInputStream stream = new FileInputStream(sourceFolder + "/" + sourceFilename);
		
		final FSTObjectInput input = fstConf.getObjectInput(stream);
		Landscape landscape = null;
		try {
			landscape = (Landscape) input.readObject(Landscape.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			stream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
