package net.explorviz.repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import org.nustaq.serialization.FSTConfiguration;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;

import net.explorviz.model.application.AggregatedClazzCommunication;
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
import net.explorviz.server.helper.FileSystemHelper;
import net.explorviz.server.main.Configuration;

public class RepositoryStorage {
	private static String folder;
	private static String landscapeFolder;
	private static String replayFolder;
	private static String folderForTargetModel;
	private static String filenameForTargetModel = "targetModel" + Configuration.MODEL_EXTENSION;

	private static final FSTConfiguration FST_CONF;

	private static final int HISTORY_INTERVAL_IN_MINUTES = Configuration.HISTORY_INTERVAL_IN_MINUTES;
	private static final String REPLAY_REPOSITORY = Configuration.REPLAY_REPOSITORY;
	private static final String LANDSCAPE_REPOSITORY = Configuration.LANDSCAPE_REPOSITORY;

	static {
		FST_CONF = createFSTConfiguration();

		folder = FileSystemHelper.getExplorVizDirectory() + File.separator;
		folderForTargetModel = FileSystemHelper.getExplorVizDirectory();
		replayFolder = folder + REPLAY_REPOSITORY;
		landscapeFolder = folder + LANDSCAPE_REPOSITORY;

		java.lang.System.out.println("writing to base folder " + folder);
		java.lang.System.out.println("writing to replay folder " + replayFolder);
		java.lang.System.out.println("writing to landscape folder " + landscapeFolder);

		new File(folder).mkdir();
		new File(replayFolder).mkdir();
		new File(landscapeFolder).mkdir();
	}

	public static FSTConfiguration createFSTConfiguration() {
		final FSTConfiguration result = FSTConfiguration.createDefaultConfiguration();
		result.registerClass(Landscape.class);
		result.registerClass(System.class);
		result.registerClass(NodeGroup.class);
		result.registerClass(Node.class);
		result.registerClass(Application.class);
		result.registerClass(ApplicationCommunication.class);
		result.registerClass(AggregatedClazzCommunication.class);
		result.registerClass(DatabaseQuery.class);
		result.registerClass(EProgrammingLanguage.class);
		result.registerClass(Component.class);
		result.registerClass(Clazz.class);
		result.registerClass(ClazzCommunication.class);
		result.registerClass(RuntimeInformation.class);

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

	public static void writeToFile(final Landscape landscape, final long timestamp, final String folderName) {
		final String specificFolder = folder + folderName;
		try {
			Files.createDirectories(Paths.get(specificFolder));
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		writeToFileGeneric(landscape, specificFolder,
				timestamp + "-" + landscape.getOverallCalls() + Configuration.MODEL_EXTENSION);
	}

	public static void writeToFile(final Landscape landscape, final String fileName, final String folderName) {
		final String specificFolder = folder + folderName;
		try {
			Files.createDirectories(Paths.get(specificFolder));
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		writeToFileGeneric(landscape, specificFolder, fileName + Configuration.MODEL_EXTENSION);
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

	public static Landscape readFromFile(final long timestamp, final String folderName) throws FileNotFoundException {
		final String specificFolder = folder + folderName;
		final Map<Long, Long> availableModels = getAvailableModels(HISTORY_INTERVAL_IN_MINUTES, specificFolder);
		String readInModel = null;

		for (final Entry<Long, Long> availableModel : availableModels.entrySet()) {
			if (availableModel.getKey() <= timestamp) {
				readInModel = availableModel.getKey() + "-" + availableModel.getValue() + Configuration.MODEL_EXTENSION;
			}
		}

		if (readInModel == null) {
			throw new FileNotFoundException("Model not found for timestamp " + timestamp);
		}

		return readFromFileGeneric(specificFolder, readInModel);
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

	public static Map<Long, Long> getAvailableModelsForTimeshift(final String folderName) {
		final String specificFolder = folder + folderName;
		return getAvailableModels(Configuration.TIMESHIFT_INTERVAL_IN_MINUTES, specificFolder);
	}

	private static Map<Long, Long> getAvailableModels(final int minutesBackwards, final String specificFolder) {
		final Map<Long, Long> result = new TreeMap<Long, Long>();

		final File[] files = new File(specificFolder).listFiles();
		if (files != null) {
			for (final File file : files) {
				if (isExplorVizFile(file)) {
					final String[] split = file.getName().split("-");
					final long timestamp = Long.parseLong(split[0]);

					if (specificFolder.endsWith(REPLAY_REPOSITORY)) {
						// don't check age of files in the replay repository
						final long activities = Long.parseLong(split[1].split("\\.")[0]);
						result.put(timestamp, activities);
					} else if (specificFolder.endsWith(LANDSCAPE_REPOSITORY)) {
						if ((java.lang.System.currentTimeMillis()
								- TimeUnit.MINUTES.toMillis(minutesBackwards)) < timestamp) {
							final long activities = Long.parseLong(split[1].split("\\.")[0]);
							result.put(timestamp, activities);
						}
					}
				}
			}
		}
		return result;
	}

	public static void cleanUpTooOldFiles(final long currentTimestamp, final String specificFolder) {
		final long enddate = currentTimestamp - TimeUnit.MINUTES.toMillis(HISTORY_INTERVAL_IN_MINUTES);
		final File[] files = new File(specificFolder).listFiles();
		if (files != null) {
			for (final File file : files) {
				if (isExplorVizFile(file)
						&& Long.parseLong(file.getName().substring(0, file.getName().indexOf("-"))) <= enddate) {

					file.delete();
				}
			}
		}
	}

	public static void clearRepository(final String folderName) {
		final String specificFolder = folder + folderName;
		final File[] files = new File(specificFolder).listFiles();
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
