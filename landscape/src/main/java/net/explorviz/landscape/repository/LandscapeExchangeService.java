package net.explorviz.landscape.repository;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.explorviz.landscape.model.landscape.Landscape;
import net.explorviz.landscape.model.store.Timestamp;
import net.explorviz.landscape.repository.persistence.LandscapeRepository;
import net.explorviz.landscape.repository.persistence.ReplayRepository;
import net.explorviz.landscape.server.helper.FileSystemHelper;
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


  @Inject
  private LandscapeRepository<String> landscapeRepo;

  @Inject
  private ReplayRepository<String> replayRepo;

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


  public List<Timestamp> getLandscapeTimestamps() {
    final List<Long> rawTimestamps = this.landscapeRepo.getAllTimestamps();
    final List<Timestamp> timestamps =
        rawTimestamps.stream().map(t -> new Timestamp(t, this.landscapeRepo.getTotalRequests(t)))
            .collect(Collectors.toList());

    return timestamps;
  }

  public List<Timestamp> getReplayTimestamps() {
    final List<Long> rawTimestamps = this.replayRepo.getAllTimestamps();
    final List<Timestamp> timestamps =
        rawTimestamps.stream().map(t -> new Timestamp(t, this.replayRepo.getTotalRequests(t)))
            .collect(Collectors.toList());

    return timestamps;
  }



  public Landscape getLandscape(final long timestamp) {
    return this.model.getLandscape(timestamp);
  }

  public Landscape getReplay(final long timestamp) {
    return this.model.getReplay(timestamp);

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
