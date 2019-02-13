package net.explorviz.landscape.repository;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.explorviz.landscape.server.helper.FileSystemHelper;
import net.explorviz.landscape.server.main.Configuration;
import net.explorviz.shared.config.annotations.Config;
import net.explorviz.shared.landscape.model.landscape.Landscape;
import net.explorviz.shared.landscape.model.store.Timestamp;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Exchange Service for timestamps and landscapes - used by resources (REST).
 */
@Service
@Singleton
public class LandscapeExchangeService {

  private static final Logger LOGGER = LoggerFactory.getLogger(LandscapeExchangeService.class);

  private static final String EXPLORVIZ_FILE_ENDING = ".expl";

  private static Map<String, Timestamp> timestampCache = new HashMap<>();

  @SuppressWarnings("unused")
  private static Long timestamp;
  @SuppressWarnings("unused")
  private static Long activity;

  private static final String REPLAY_FOLDER =
      FileSystemHelper.getExplorVizDirectory() + File.separator + "replay";
  private static final String REPOSITORY_FOLDER =
      FileSystemHelper.getExplorVizDirectory() + File.separator;

  private final LandscapeRepositoryModel model;

  @Config("repository.useDummyMode")
  private boolean useDummyMode;

  @Inject
  public LandscapeExchangeService(final LandscapeRepositoryModel model) {
    this.model = model;
  }

  public LandscapeRepositoryModel getModel() {
    return this.model;
  }

  public Landscape getCurrentLandscape() {
    return this.model.getLastPeriodLandscape();
  }

  public List<String> getReplayNames() {
    final List<String> names = new ArrayList<>();
    final File directory = new File(REPLAY_FOLDER);
    final File[] fList = directory.listFiles();

    if (fList != null) {
      for (final File f : fList) {
        final String filename = f.getName();

        if (filename.endsWith(EXPLORVIZ_FILE_ENDING)) {
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
            this.getLandscape(timestamp, Configuration.REPLAY_REPOSITORY);
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

  public List<Timestamp> getTimestampObjectsInRepo(final String folderName) {
    final File directory = new File(REPOSITORY_FOLDER + folderName);
    final File[] fList = directory.listFiles();
    final List<Timestamp> timestamps = new LinkedList<>();

    if (fList != null) {
      for (final File f : fList) {
        final String filename = f.getName();

        if (filename.endsWith(EXPLORVIZ_FILE_ENDING)) {
          // first validation check -> filename

          final String timestampAsString = filename.split("-")[0];
          final String callsAsString = filename.split("-")[1].split(EXPLORVIZ_FILE_ENDING)[0];

          Timestamp possibleTimestamp = timestampCache.get(timestampAsString + callsAsString);

          if (possibleTimestamp == null) {

            // new timestamp -> add to cache
            // and initialize ID of entity
            long timestamp;
            int calls;

            try {
              timestamp = Long.parseLong(timestampAsString);
              calls = Integer.parseInt(callsAsString);
            } catch (final NumberFormatException e) {
              continue;
            }

            possibleTimestamp = new Timestamp(timestamp, calls); // NOPMD
            possibleTimestamp.initializeId();
            timestampCache.put(timestampAsString + callsAsString, possibleTimestamp);
          }

          timestamps.add(possibleTimestamp);
        }
      }
    }
    return timestamps;
  }

  public Landscape getLandscape(final long timestamp, final String folderName)
      throws FileNotFoundException {
    return this.model.getLandscape(timestamp, folderName);
  }

  public void startRepository() {
    new Thread(new Runnable() {

      @Override
      public void run() {
        new RepositoryStarter().start(LandscapeExchangeService.this.model);
      }
    }).start();
  }
}
