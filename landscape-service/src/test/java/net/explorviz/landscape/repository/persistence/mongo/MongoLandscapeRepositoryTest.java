package net.explorviz.landscape.repository.persistence.mongo;

import static org.junit.Assert.assertEquals;

import java.util.Random;
import javax.inject.Inject;
import net.explorviz.landscape.repository.LandscapeDummyCreator;
import net.explorviz.landscape.repository.persistence.mongo.MongoLandscapeRepository;
import net.explorviz.landscape.server.main.DependencyInjectionBinder;
import net.explorviz.landscape.server.providers.CoreModelHandler;
import net.explorviz.shared.landscape.model.landscape.Landscape;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class MongoLandscapeRepositoryTest {

  @Inject
  private MongoLandscapeRepository repo;

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
    this.repo.save(ts, landscape, 0);

    final Landscape landscapeRetrieved = this.repo.getByTimestamp(ts);

    assertEquals("Ids don't match", landscape.getId(), landscapeRetrieved.getId()); // NOPMD

  }


  @Test
  public void testTotalRequets() {
    final long ts = System.currentTimeMillis();
    final Landscape landscape = LandscapeDummyCreator.createDummyLandscape();
    this.repo.save(ts, landscape, 0);

    final Landscape landscapeRetrieved = this.repo.getByTimestamp(ts);

    assertEquals("Requests don't match", landscape.getId(), landscapeRetrieved.getId()); // NOPMD
  }

  @Test
  public void testFindById() {
    final long ts = System.currentTimeMillis();
    final Landscape landscape = LandscapeDummyCreator.createDummyLandscape();
    final Landscape landscape2 = LandscapeDummyCreator.createDummyLandscape();
    this.repo.save(ts, landscape, 0);
    this.repo.save(ts, landscape2, 0);

    final long id = landscape.getId();

    final Landscape landscapeRetrieved = this.repo.getById(id);

    assertEquals("Ids don't match", id, (long) landscapeRetrieved.getId());

  }



  @Test
  public void testTotalRequestsLandscape() {
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
