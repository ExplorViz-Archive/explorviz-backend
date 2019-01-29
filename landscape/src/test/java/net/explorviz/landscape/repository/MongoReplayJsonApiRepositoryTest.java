package net.explorviz.landscape.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Random;
import javax.inject.Inject;
import net.explorviz.landscape.model.landscape.Landscape;
import net.explorviz.landscape.repository.persistence.mongo.MongoReplayJsonApiRepository;
import net.explorviz.landscape.server.main.DependencyInjectionBinder;
import net.explorviz.landscape.server.providers.CoreModelHandler;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class MongoReplayJsonApiRepositoryTest {



  @Inject
  private MongoReplayJsonApiRepository repo;

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
  public void findReplayByTimestamp() {
    final long ts = System.currentTimeMillis();
    final Landscape landscape = LandscapeDummyCreator.createDummyLandscape();
    this.repo.save(ts, landscape, 0);

    final String rawLandscape = this.repo.getByTimestamp(ts);



    assertTrue("Invalid landscape", rawLandscape.startsWith("{\"data\":{\"type\":\"landscape\""));
  }


  @Test
  public void findReplayById() {
    final long ts = System.currentTimeMillis();
    final Landscape landscape = LandscapeDummyCreator.createDummyLandscape();
    final Landscape landscape2 = LandscapeDummyCreator.createDummyLandscape();
    this.repo.save(ts, landscape, 0);
    this.repo.save(ts, landscape2, 0);

    final long id = landscape.getId();
    final String rawLandscape = this.repo.getById(id);

    assertTrue("Ivalid landscape or wrong id",
        rawLandscape.startsWith("{\"data\":{\"type\":\"landscape\",\"id\":\"" + id + "\""));

  }

  @Test
  public void testTotalRequestsReplay() {
    final Random rand = new Random();
    final long ts = System.currentTimeMillis();
    final int requests = rand.nextInt(Integer.MAX_VALUE) + 1;
    final Landscape landscape = LandscapeDummyCreator.createDummyLandscape();
    this.repo.save(ts, landscape, requests);

    final int retrievedRequests = this.repo.getTotalRequests(ts);
    assertEquals("Requests not matching", requests, retrievedRequests);
  }

  @Test
  public void testAllTimestamps() {
    final Landscape landscape = LandscapeDummyCreator.createDummyLandscape();
    final long ts = System.currentTimeMillis();
    final Landscape landscape2 = LandscapeDummyCreator.createDummyLandscape();
    final long ts2 = ts + 1;
    this.repo.save(ts, landscape, 0);
    this.repo.save(ts2, landscape2, 0);

    final int timestamps = this.repo.getAllTimestamps().size();
    assertEquals("Amount of objects don't match", 2, timestamps);
  }


}
