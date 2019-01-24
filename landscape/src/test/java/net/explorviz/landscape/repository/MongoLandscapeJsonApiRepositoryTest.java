package net.explorviz.landscape.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Random;
import javax.inject.Inject;
import net.explorviz.landscape.model.landscape.Landscape;
import net.explorviz.landscape.repository.persistence.mongo.MongoLandscapeJsonApiRepository;
import net.explorviz.landscape.server.main.DependencyInjectionBinder;
import net.explorviz.landscape.server.providers.CoreModelHandler;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests the {@link MongoLandscapeJsonApiRepository}. Expects a running mongodb server.
 *
 */
public class MongoLandscapeJsonApiRepositoryTest {


  @Inject
  private MongoLandscapeJsonApiRepository repo;

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
    this.repo.saveLandscape(ts, landscape, 0);

    final String rawLandscape = this.repo.getLandscapeByTimestamp(ts);



    assertTrue("Invalid landscape", rawLandscape.startsWith("{\"data\":{\"type\":\"landscape\""));
  }

  @Test
  public void testFindById() {

    final Landscape landscape = LandscapeDummyCreator.createDummyLandscape();
    final long ts = System.currentTimeMillis();
    final Landscape landscape2 = LandscapeDummyCreator.createDummyLandscape();
    final long ts2 = ts + 1;
    this.repo.saveLandscape(ts, landscape, 0);
    this.repo.saveLandscape(ts2, landscape2, 0);

    final long id = landscape.getId();
    final String rawLandscape = this.repo.getLandscapeById(id);

    assertTrue("Ivalid landscape or wrong id",
        rawLandscape.startsWith("{\"data\":{\"type\":\"landscape\",\"id\":\"" + id + "\""));

  }



  @Test
  public void testTotalRequestsLandscape() {
    final Random rand = new Random();
    final long ts = System.currentTimeMillis();
    final long requests = rand.nextInt(Integer.MAX_VALUE) + 1;
    final Landscape landscape = LandscapeDummyCreator.createDummyLandscape();
    this.repo.saveLandscape(ts, landscape, requests);

    final long retrievedRequests = this.repo.getLandscapeTotalRequests(ts);
    assertEquals("Requests not matching", requests, retrievedRequests);
  }

}
