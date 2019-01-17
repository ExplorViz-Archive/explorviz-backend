package net.explorviz.landscape.repository;

import static org.junit.Assert.assertEquals;

import javax.inject.Inject;
import net.explorviz.landscape.model.landscape.Landscape;
import net.explorviz.landscape.repository.persistence.mongo.MongoRepository;
import net.explorviz.landscape.server.main.DependencyInjectionBinder;
import net.explorviz.landscape.server.providers.CoreModelHandler;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class MongoRepositoryTest {

  @Inject
  private MongoRepository repo;

  @BeforeClass
  public static void setUpAll() {
    CoreModelHandler.registerAllCoreModels();
  }


  /**
   * Perform DI.
   */
  @Before
  public void setUp() {
    if (this.repo == null) {
      final DependencyInjectionBinder binder = new DependencyInjectionBinder();
      final ServiceLocator locator = ServiceLocatorUtilities.bind(binder);
      locator.inject(this);
    }
    this.repo.clear();
  }


  @After
  public void tearDown() {
    this.repo.clear();
  }



  @Test
  public void findByTimestamp() {
    final long ts = System.currentTimeMillis();
    final Landscape landscape = LandscapeDummyCreator.createDummyLandscape();
    this.repo.saveLandscape(ts, landscape);

    final Landscape landscapeRetrieved = this.repo.getLandscapeByTimestamp(ts);

    assertEquals("Ids don't match", landscape.getId(), landscapeRetrieved.getId()); // NOPMD

  }

  @Test
  public void testFindById() {
    final long ts = System.currentTimeMillis();
    final Landscape landscape = LandscapeDummyCreator.createDummyLandscape();
    final Landscape landscape2 = LandscapeDummyCreator.createDummyLandscape();
    this.repo.saveLandscape(ts, landscape);
    this.repo.saveLandscape(ts, landscape2);

    final long id = landscape.getId();
    final Landscape landscapeRetrieved = this.repo.getLandscapeById(id);

    assertEquals("Ids don't match", id, (long) landscapeRetrieved.getId());

  }

  @Test
  public void findReplayByTimestamp() {
    final long ts = System.currentTimeMillis();
    final Landscape landscape = LandscapeDummyCreator.createDummyLandscape();
    this.repo.saveReplay(ts, landscape);

    final Landscape landscapeRetrieved = this.repo.getReplayByTimestamp(ts);



    assertEquals("Ids don't match", landscape.getId(), landscapeRetrieved.getId());
  }

  @Test
  public void findReplayById() {
    final long ts = System.currentTimeMillis();
    final Landscape landscape = LandscapeDummyCreator.createDummyLandscape();
    final Landscape landscape2 = LandscapeDummyCreator.createDummyLandscape();
    this.repo.saveReplay(ts, landscape);
    this.repo.saveReplay(ts, landscape2);

    final long id = landscape.getId();
    final Landscape landscapeRetrieved = this.repo.getReplayById(id);

    assertEquals("Ids don't match", id, (long) landscapeRetrieved.getId());

  }

}
