package net.explorviz.repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import org.nustaq.serialization.FSTConfiguration;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;

import net.explorviz.model.application.Application;
import net.explorviz.model.application.ApplicationCommunication;
import net.explorviz.model.application.Clazz;
import net.explorviz.model.application.ClazzCommunication;
import net.explorviz.model.application.Component;
import net.explorviz.model.application.DatabaseQuery;
import net.explorviz.model.application.RuntimeInformation;
import net.explorviz.model.helper.EProgrammingLanguage;
import net.explorviz.model.landscape.Landscape;
import net.explorviz.model.landscape.Node;
import net.explorviz.model.landscape.NodeGroup;
import net.explorviz.model.landscape.System;
import net.explorviz.server.main.Configuration;
import net.explorviz.server.main.FileSystemHelper;

public class RepositoryStorage {
	private static String folder;
	private static String folderForTargetModel;
	private static String filenameForTargetModel = "targetModel" + Configuration.MODEL_EXTENSION;

	private static final FSTConfiguration FST_CONF;

	private static final int HISTORY_INTERVAL_IN_MINUTES = Configuration.HISTORY_INTERVAL_IN_MINUTES;

	static {
		FST_CONF = createFSTConfiguration();

		folder = FileSystemHelper.getExplorVizDirectory() + "/" + "landscapeRepository";
		folderForTargetModel = FileSystemHelper.getExplorVizDirectory();

		java.lang.System.out.println("writing to " + folder);

		new File(folder).mkdir();
	}

	public static FSTConfiguration createFSTConfiguration() {
		final FSTConfiguration result = FSTConfiguration.createDefaultConfiguration();
		result.registerClass(Landscape.class);
		result.registerClass(System.class);
		result.registerClass(NodeGroup.class);
		result.registerClass(Node.class);
		result.registerClass(ApplicationCommunication.class);
		result.registerClass(Application.class);
		result.registerClass(EProgrammingLanguage.class);
		result.registerClass(Component.class);
		result.registerClass(Clazz.class);
		result.registerClass(RuntimeInformation.class);
		result.registerClass(DatabaseQuery.class);
		result.registerClass(ClazzCommunication.class);

		return result;
	}

	public static Landscape readTargetArchitecture() {
		try {
			return readFromFileGeneric(folderForTargetModel, filenameForTargetModel);
		} catch (final FileNotFoundException e) {
			java.lang.System.err.println(e);
		}

		return new Landscape();
	}

	public static void saveTargetArchitecture(final Landscape landscape) {
		writeToFileGeneric(landscape, folderForTargetModel, filenameForTargetModel);
	}

	public static void writeToFile(final Landscape landscape, final long timestamp) {
		writeToFileGeneric(landscape, folder,
				timestamp + "-" + landscape.getOverallCalls() + Configuration.MODEL_EXTENSION);
	}

	private static void writeToFileGeneric(final Landscape landscape, final String destFolder,
			final String destFilename) {
		FSTObjectOutput output = null;
		try {
			output = FST_CONF.getObjectOutput(new FileOutputStream(destFolder + "/" + destFilename));
			output.writeObject(landscape, Landscape.class);
			output.close();
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (final IOException e) {
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
				readInModel = availableModel.getKey() + "-" + availableModel.getValue() + Configuration.MODEL_EXTENSION;
			}
		}

		if (readInModel == null) {
			throw new FileNotFoundException("Model not found for timestamp " + timestamp);
		}

		return readFromFileGeneric(folder, readInModel);
	}

	public static Landscape readFromFileGeneric(final String sourceFolder, final String sourceFilename)
			throws FileNotFoundException {

		final FileInputStream stream = new FileInputStream(sourceFolder + "/" + sourceFilename);

		final FSTObjectInput input = FST_CONF.getObjectInput(stream);
		Landscape landscape = null;
		try {
			landscape = (Landscape) input.readObject(Landscape.class);
		} catch (final Exception e) {
			e.printStackTrace();
		}
		try {
			stream.close();
		} catch (final IOException e) {
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

		final File[] files = new File(folder).listFiles();
		if (files != null) {
			for (final File file : files) {
				if (isExplorVizFile(file)) {
					final String[] split = file.getName().split("-");
					final long timestamp = Long.parseLong(split[0]);

					if ((java.lang.System.currentTimeMillis()
							- TimeUnit.MINUTES.toMillis(minutesBackwards)) < timestamp) {
						final long activities = Long.parseLong(split[1].split("\\.")[0]);
						result.put(timestamp, activities);
					}
				}
			}
		}
		return result;
	}

	public static void cleanUpTooOldFiles(final long currentTimestamp) {
		final long enddate = currentTimestamp - TimeUnit.MINUTES.toMillis(HISTORY_INTERVAL_IN_MINUTES);
		final File[] files = new File(folder).listFiles();
		if (files != null) {
			for (final File file : files) {
				if (isExplorVizFile(file)
						&& Long.parseLong(file.getName().substring(0, file.getName().indexOf("-"))) <= enddate) {

					file.delete();
				}
			}
		}
	}

	public static void clearRepository() {
		final File[] files = new File(folder).listFiles();
		if (files != null) {
			for (final File file : files) {
				if (isExplorVizFile(file)) {
					file.delete();
				}
			}
		}
	}

	public static boolean isExplorVizFile(final File file) {
		return !file.getName().equals(".") && !file.getName().equals("..")
				&& file.getName().endsWith(Configuration.MODEL_EXTENSION);
	}
}
