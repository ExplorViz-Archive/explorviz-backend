package net.explorviz.landscape.repository;

import javax.inject.Inject;
import javax.inject.Singleton;
import net.explorviz.landscape.repository.persistence.LandscapeRepository;
import net.explorviz.landscape.repository.persistence.ReplayRepository;
import net.explorviz.shared.config.annotations.Config;
import net.explorviz.shared.landscape.model.landscape.Landscape;
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
