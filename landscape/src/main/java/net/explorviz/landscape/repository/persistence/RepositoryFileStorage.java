package net.explorviz.landscape.repository.persistence; // NOPMD

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.ClientErrorException;
import net.explorviz.landscape.model.application.AggregatedClazzCommunication;
import net.explorviz.landscape.model.application.Application;
import net.explorviz.landscape.model.application.ApplicationCommunication;
import net.explorviz.landscape.model.application.Clazz;
import net.explorviz.landscape.model.application.ClazzCommunication;
import net.explorviz.landscape.model.application.Component;
import net.explorviz.landscape.model.application.DatabaseQuery;
import net.explorviz.landscape.model.application.TraceStep;
import net.explorviz.landscape.model.helper.EProgrammingLanguage;
import net.explorviz.landscape.model.landscape.Landscape;
import net.explorviz.landscape.model.landscape.Node;
import net.explorviz.landscape.model.landscape.NodeGroup;
import net.explorviz.landscape.model.landscape.System;
import net.explorviz.landscape.server.helper.FileSystemHelper;
import net.explorviz.landscape.server.main.Configuration;
import org.nustaq.serialization.FSTConfiguration;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public final class RepositoryFileStorage {
  private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryFileStorage.class);

  private static final FSTConfiguration FST_CONF;

  // What?
  private static final int HISTORY_INTERVAL_IN_MINUTES = Configuration.HISTORY_INTERVAL_IN_MINUTES;
  private static final String REPLAY_REPOSITORY = Configuration.REPLAY_REPOSITORY;
  private static final String LANDSCAPE_REPOSITORY = Configuration.LANDSCAPE_REPOSITORY;

  // Unused
  private static String folder;
  private static String landscapeFolder;
  private static String replayFolder;

  private RepositoryFileStorage() {
    // Utility Class
  }

  static {
    FST_CONF = createFstConfiguration();

    folder = FileSystemHelper.getExplorVizDirectory() + File.separator;
    replayFolder = folder + REPLAY_REPOSITORY;
    landscapeFolder = folder + LANDSCAPE_REPOSITORY;

    LOGGER.debug("writing to base folder: {}", folder);
    LOGGER.debug("writing to replay folder: {}", replayFolder);
    LOGGER.debug("writing to landscape folder: {}", landscapeFolder);

    new File(folder).mkdir();
    new File(replayFolder).mkdir();
    new File(landscapeFolder).mkdir();
  }

  public static FSTConfiguration createFstConfiguration() {
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
    result.registerClass(TraceStep.class);

    return result;
  }


  /**
   * Saves a landscape object on disk.
   *
   * @param landscape the landscape object.
   * @param timestamp the timestamp associated to the landscape
   * @param folderName the output folder
   */
  public static void writeToFile(final Landscape landscape, final long timestamp,
      final String folderName) {
    final String specificFolder = folder + folderName;
    writeToFileGeneric(landscape, specificFolder, timestamp + "-"
        + landscape.getTimestamp().getTotalRequests() + Configuration.MODEL_EXTENSION);
  }


  /**
   * Converts a landscape object to bytes.
   *
   * @param landscape the landscape object
   *
   * @return the given landscape as byte array
   */
  public static byte[] convertLandscapeToBytes(final Landscape landscape) {
    // TODO: Move to "ExportHelper"
    return FST_CONF.asByteArray(landscape);
  }

  /**
   * Writes a landscape object to a file on disk.
   *
   * @param landscape the object
   * @param destFolder the destination folder
   * @param destFilename the destination file name
   */
  private static void writeToFileGeneric(final Landscape landscape, final String destFolder,
      final String destFilename) {

    try (OutputStream stream = Files.newOutputStream(Paths.get(destFolder + "/" + destFilename))) {
      try (FSTObjectOutput output = FST_CONF.getObjectOutput(stream)) {
        output.writeObject(landscape, Landscape.class);
        output.close();
      }

    } catch (final IOException e) {
      LOGGER.error("Error when writing to file", e);
    }
  }

  /**
   * Retrieves a landscape object with a given timestamp.
   *
   * @param timestamp the timestamp associated to the landscape to look for.
   * @param folderName the folder name to use.
   *
   * @throws ClientErrorException if the is no landscape object with the given timestamp.
   *
   * @return the landscape.
   */
  public static Landscape readFromFile(final long timestamp, final String folderName) {
    final String specificFolder = folder + folderName;
    final Map<Long, Long> availableModels =
        getAvailableModels(HISTORY_INTERVAL_IN_MINUTES, specificFolder);
    String readInModel = null;

    for (final Entry<Long, Long> availableModel : availableModels.entrySet()) {
      if (availableModel.getKey() == timestamp) {
        readInModel = availableModel.getKey() + "-" + availableModel.getValue()
            + Configuration.MODEL_EXTENSION;
        break;
      }
    }

    if (readInModel == null) {
      throw new ClientErrorException("Model not found for provided timestamp " + timestamp, 404);
    }

    return readFromFileGeneric(specificFolder, readInModel);
  }

  /**
   * Retrieves a specific landscape object.
   *
   * @param sourceFolder the folder
   * @param sourceFilename the filename
   *
   * @throws ClientErrorException if the is no landscape object with the given timestamp.
   *
   * @return the landscape object.
   */
  public static Landscape readFromFileGeneric(final String sourceFolder,
      final String sourceFilename) {

    FSTObjectInput input;
    final Path path = Paths.get(sourceFolder + "/" + sourceFilename);

    try (InputStream stream = Files.newInputStream(path)) {
      input = FST_CONF.getObjectInput(stream);
    } catch (final IOException e) {
      throw new ClientErrorException("Model not found", 404, e);
    }

    Landscape landscape = null;
    try {
      landscape = (Landscape) input.readObject(Landscape.class);
    } catch (final Exception e) { // NOPMD
      LOGGER.error("Error when reading Landscape from file.", e);
    }

    return landscape;
  }

  /**
   * Returns all landscape model snapshots for timeshift use.
   *
   * @param folderName the folder
   * @return a map containing the timestamps as keys and actvity(?) as values.
   */
  public static Map<Long, Long> getAvailableModelsForTimeshift(final String folderName) {
    final String specificFolder = folder + folderName;
    return getAvailableModels(Configuration.TIMESHIFT_INTERVAL_IN_MINUTES, specificFolder);
  }


  /**
   * Returns all available landscape models
   *
   * @param minutesBackwards
   * @param specificFolder
   * @return Map with timestamps as keys and activity(?) as value
   */
  private static Map<Long, Long> getAvailableModels(final int minutesBackwards,
      final String specificFolder) {
    final Map<Long, Long> result = new TreeMap<>();

    if (specificFolder == null) {
      return result;
    }

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
            if (java.lang.System.currentTimeMillis()
                - TimeUnit.MINUTES.toMillis(minutesBackwards) < timestamp) {
              final long activities = Long.parseLong(split[1].split("\\.")[0]);
              result.put(timestamp, activities);
            }
          } else {
            // every other folder
            final long activities = Long.parseLong(split[1].split("\\.")[0]);
            result.put(timestamp, activities);
          }
        }
      }
    }
    return result;
  }

  /**
   * Removes files that have exceeded a specific life time.
   *
   * @param currentTimestamp The reference timestamp to use.
   * @param specificFolder the folder to tidy up.
   */
  public static void cleanUpTooOldFiles(final long currentTimestamp, final String specificFolder) {
    final long enddate = currentTimestamp - TimeUnit.MINUTES.toMillis(HISTORY_INTERVAL_IN_MINUTES);
    final File[] files = new File(specificFolder).listFiles();
    if (files != null) {
      for (final File file : files) {
        if (isExplorVizFile(file) && Long
            .parseLong(file.getName().substring(0, file.getName().indexOf("-"))) <= enddate) {

          file.delete();
        }
      }
    }
  }


  /**
   * Deletes all available landscape files in a specific folder.
   *
   * @category unused
   *
   * @param folderName the folder to use within the explorviz directory
   */
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

  /**
   * Checks whether a given file is an ExplorViz file by looking at its name.
   *
   * @param file the file
   * @return {@code true} if and only if the file is an ExplorViz file.
   */
  private static boolean isExplorVizFile(final File file) {
    return !file.getName().equals(".") && !file.getName().equals("..")
        && file.getName().endsWith(Configuration.MODEL_EXTENSION);
  }
}
