package net.explorviz.landscape.repository;

import static org.junit.Assert.assertTrue;

import javax.inject.Inject;
import net.explorviz.landscape.model.landscape.Landscape;
import net.explorviz.landscape.repository.persistence.mongo.MongoJsonApiRepository;
import net.explorviz.landscape.server.main.DependencyInjectionBinder;
import net.explorviz.landscape.server.providers.CoreModelHandler;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests the {@link MongoJsonApiRepository}. Expects a running mongodb server.
 *
 */
public class MongoJsonApiRepositoryTest {


  @Inject
  private MongoJsonApiRepository repo;

  @BeforeClass
  public static void setUpAll() {
    CoreModelHandler.registerAllCoreModels();
  }


  /**
   * Injects depedencies.
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

    final String rawLandscape = this.repo.getLandscapeByTimestamp(ts);



    assertTrue("Invalid landscape", rawLandscape.startsWith("{\"data\":{\"type\":\"landscape\""));
  }

  @Test
  public void testFindById() {
    final long ts = System.currentTimeMillis();
    final Landscape landscape = LandscapeDummyCreator.createDummyLandscape();
    final Landscape landscape2 = LandscapeDummyCreator.createDummyLandscape();
    this.repo.saveLandscape(ts, landscape);
    this.repo.saveLandscape(ts, landscape2);

    final long id = landscape.getId();
    final String rawLandscape = this.repo.getLandscapeById(id);

    assertTrue("Ivalid landscape or wrong id",
        rawLandscape.startsWith("{\"data\":{\"type\":\"landscape\",\"id\":\"" + id + "\""));

  }

  @Test
  public void findReplayByTimestamp() {
    final long ts = System.currentTimeMillis();
    final Landscape landscape = LandscapeDummyCreator.createDummyLandscape();
    this.repo.saveReplay(ts, landscape);

    final String rawLandscape = this.repo.getReplayByTimestamp(ts);



    assertTrue("Invalid landscape", rawLandscape.startsWith("{\"data\":{\"type\":\"landscape\""));
  }

  @Test
  public void findReplayById() {
    final long ts = System.currentTimeMillis();
    final Landscape landscape = LandscapeDummyCreator.createDummyLandscape();
    final Landscape landscape2 = LandscapeDummyCreator.createDummyLandscape();
    this.repo.saveReplay(ts, landscape);
    this.repo.saveReplay(ts, landscape2);

    final long id = landscape.getId();
    final String rawLandscape = this.repo.getReplayById(id);

    assertTrue("Ivalid landscape or wrong id",
        rawLandscape.startsWith("{\"data\":{\"type\":\"landscape\",\"id\":\"" + id + "\""));

  }



}
