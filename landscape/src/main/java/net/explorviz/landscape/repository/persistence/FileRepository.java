package net.explorviz.landscape.repository.persistence;

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

/**
 * Stores {@link Landscape} objects as files. Objects are then identified by their filename.
 */
public class FileRepository implements LandscapeRepository {
  private static final Logger LOGGER = LoggerFactory.getLogger(FileRepository.class);
  private final FSTConfiguration fstConfig;


  private final String baseFolder;
  private final String landscapeFolder;
  private final String replayFolder;



  public FileRepository() {
    this.fstConfig = this.createFstConfiguration();

    this.baseFolder = FileSystemHelper.getExplorVizDirectory() + File.separator;
    this.replayFolder = this.baseFolder + Configuration.REPLAY_REPOSITORY;
    this.landscapeFolder = this.baseFolder + Configuration.LANDSCAPE_REPOSITORY;

    LOGGER.debug("writing to base folder: {}", this.baseFolder);
    LOGGER.debug("writing to replay folder: {}", this.replayFolder);
    LOGGER.debug("writing to landscape folder: {}", this.landscapeFolder);

    new File(this.baseFolder).mkdir();
    new File(this.replayFolder).mkdir();
    new File(this.landscapeFolder).mkdir();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void saveLandscape(final long timestamp, final Landscape landscape) {

    final String destFilename = timestamp + "-" + landscape.getTimestamp().getTotalRequests()
        + Configuration.MODEL_EXTENSION;
    try (OutputStream stream =
        Files.newOutputStream(Paths.get(this.baseFolder + "/" + destFilename))) {

      try (FSTObjectOutput output = this.fstConfig.getObjectOutput(stream)) {
        output.writeObject(landscape, Landscape.class);
        output.close();
      }

    } catch (final IOException e) {
      LOGGER.error("Error when writing to file", e);
    }
  }


  @Override
  public Landscape getLandscapeByTimestamp(final long timestamp) {

    final Map<Long, Long> availableModels =
        getAvailableModels(Configuration.HISTORY_INTERVAL_IN_MINUTES, this.landscapeFolder);
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

    return this.getLandscapeById(readInModel);
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
   *
   *         TODO: Move to FSTHelper
   */
  @Override
  public Landscape getLandscapeById(final String filename) {

    FSTObjectInput input;
    final Path path = Paths.get(this.landscapeFolder + "/" + filename);

    try (InputStream stream = Files.newInputStream(path)) {
      input = this.fstConfig.getObjectInput(stream);
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


  @Override
  public Map<Long, Long> getAllForTimeshift() {
    return getAvailableModels(Configuration.TIMESHIFT_INTERVAL_IN_MINUTES, this.landscapeFolder);
  }

  @Override
  public void cleanup(final long from) {
    final long enddate =
        from - TimeUnit.MINUTES.toMillis(Configuration.HISTORY_INTERVAL_IN_MINUTES);
    // cleanup landscapes
    final File[] files = new File(this.landscapeFolder).listFiles();
    if (files != null) {
      for (final File file : files) {
        if (isExplorVizFile(file) && Long
            .parseLong(file.getName().substring(0, file.getName().indexOf("-"))) <= enddate) {

          file.delete();
        }
      }
    }

    // TODO: cleanup replays?
  }



  @Override
  public void clear() {
    File[] files = new File(this.landscapeFolder).listFiles();
    if (files != null) {
      for (final File file : files) {
        if (isExplorVizFile(file)) {
          file.delete();
        }
      }
    }
    files = new File(this.replayFolder).listFiles();
    if (files != null) {
      for (final File file : files) {
        if (isExplorVizFile(file)) {
          file.delete();
        }
      }
    }
  }

  // TODO: Move to 'FSTHelper'
  public FSTConfiguration createFstConfiguration() {
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

          if (specificFolder.endsWith(Configuration.REPLAY_REPOSITORY)) {
            // don't check age of files in the replay repository
            final long activities = Long.parseLong(split[1].split("\\.")[0]);
            result.put(timestamp, activities);
          } else if (specificFolder.endsWith(Configuration.LANDSCAPE_REPOSITORY)) {
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
