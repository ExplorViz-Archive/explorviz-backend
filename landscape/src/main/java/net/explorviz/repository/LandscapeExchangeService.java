package net.explorviz.repository;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.explorviz.kiekeradapter.main.KiekerAdapter;
import net.explorviz.model.landscape.Landscape;
import net.explorviz.model.store.Timestamp;
import net.explorviz.server.helper.FileSystemHelper;
import net.explorviz.server.main.Configuration;
import net.explorviz.shared.annotations.Config;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Exchange Service for timestamps and landscapes - used by resources (REST).
 */
@Service
@Singleton
public class LandscapeExchangeService {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(LandscapeExchangeService.class.getName());

  private static final String EXPLORVIZ_FILE_ENDING = ".expl";

  private static Map<String, Timestamp> timestampCache = new HashMap<String, Timestamp>();

  @SuppressWarnings("unused")
  private static Long timestamp;
  @SuppressWarnings("unused")
  private static Long activity;

  private static final String REPLAY_FOLDER =
      FileSystemHelper.getExplorVizDirectory() + File.separator + "replay";
  private static final String REPOSITORY_FOLDER =
      FileSystemHelper.getExplorVizDirectory() + File.separator;

  private final LandscapeRepositoryModel model;
  private final KiekerAdapter adapter;

  @Config("repository.useDummyMode")
  private boolean useDummyMode;

  @Inject
  public LandscapeExchangeService(final LandscapeRepositoryModel model) {
    this.model = model;
    this.adapter = KiekerAdapter.getInstance();
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
    final List<Timestamp> timestamps = new LinkedList<Timestamp>();

    if (fList != null) {
      for (final File f : fList) {
        final String filename = f.getName();

        if (filename.endsWith(EXPLORVIZ_FILE_ENDING)) {
          // first validation check -> filename

          final String timestampAsString = filename.split("-")[0];
          final String activityAsString = filename.split("-")[1].split(EXPLORVIZ_FILE_ENDING)[0];

          Timestamp possibleTimestamp = timestampCache.get(timestampAsString + activityAsString);

          if (possibleTimestamp == null) {

            // new timestamp -> add to cache
            // and initialize ID of entity
            long timestamp;
            long activity;

            try {
              timestamp = Long.parseLong(timestampAsString);
              activity = Long.parseLong(activityAsString);
            } catch (final NumberFormatException e) {
              continue;
            }

            possibleTimestamp = new Timestamp(timestamp, activity);
            possibleTimestamp.initializeID();
            timestampCache.put(timestampAsString + activityAsString, possibleTimestamp);
          }

          timestamps.add(possibleTimestamp);
        }
      }
    }
    return timestamps;
  }

  public Landscape getLandscape(final long timestamp, final String folderName)
      throws FileNotFoundException {
    return model.getLandscape(timestamp, folderName);
  }

  public void startRepository() {
    new Thread(new Runnable() {

      @Override
      public void run() {
        new RepositoryStarter().start(model);
      }
    }).start();


    // Start Kieker monitoring adapter
    if (!useDummyMode && Configuration.ENABLE_KIEKER_ADAPTER) {
      new Thread(new Runnable() {

        @Override
        public void run() {
          adapter.startReader();
        }
      }).start();
    }
  }
}
