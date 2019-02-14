package net.explorviz.landscape.repository;

import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.explorviz.landscape.repository.persistence.LandscapeRepository;
import net.explorviz.landscape.repository.persistence.ReplayRepository;
import net.explorviz.shared.config.annotations.Config;
import net.explorviz.shared.landscape.model.landscape.Landscape;
import net.explorviz.shared.landscape.model.store.Timestamp;
import org.jvnet.hk2.annotations.Service;

/**
 * Exchange Service for timestamps and landscapes - used by resources (REST).
 */
@Service
@Singleton
public class LandscapeExchangeService {



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
