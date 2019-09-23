package net.explorviz.history.repository.persistence.mongo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.Optional;
import java.util.Random;
import javax.inject.Inject;
import net.explorviz.history.server.main.DependencyInjectionBinder;
import net.explorviz.history.server.main.HistoryApplication;
import net.explorviz.landscape.model.landscape.Landscape;
import net.explorviz.shared.common.idgen.IdGenerator;
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

  @Inject
  private TimestampRepository timestampRepo;

  @Inject
  private IdGenerator idGenerator;

  @BeforeClass
  public static void setUpAll() {
    HistoryApplication.registerLandscapeModels();
  }



  /**
   * Injects dependencies.
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
    final Landscape landscape = LandscapeDummyCreator.createDummyLandscape(this.idGenerator);
    this.repo.save(ts, landscape, 0);

    final Optional<String> rawLandscape = this.repo.getByTimestamp(ts);

    assertTrue("Invalid landscape",
        rawLandscape.get().startsWith("{\"data\":{\"type\":\"landscape\""));
  }

  @Test
  public void testFindById() {
    final Landscape landscape = LandscapeDummyCreator.createDummyLandscape(this.idGenerator);
    final long ts = System.currentTimeMillis();
    final Landscape landscape2 = LandscapeDummyCreator.createDummyLandscape(this.idGenerator);
    final long ts2 = ts + 1;
    this.repo.save(ts, landscape, 0);
    this.repo.save(ts2, landscape2, 0);

    final String id = landscape.getId();
    final Optional<String> rawLandscape = this.repo.getById(id);

    assertTrue("Ivalid landscape or wrong id",
        rawLandscape.get().startsWith("{\"data\":{\"type\":\"landscape\",\"id\":\"" + id + "\""));
  }

  @Test
  public void testTotalRequestsLandscape() {
    final Random rand = new Random();
    final long ts = System.currentTimeMillis();
    final int requests = rand.nextInt(Integer.MAX_VALUE) + 1;
    final Landscape landscape = LandscapeDummyCreator.createDummyLandscape(this.idGenerator);
    this.repo.save(ts, landscape, requests);

    final int retrievedRequests = this.repo.getTotalRequests(ts);
    assertEquals("Requests not matching", requests, retrievedRequests);
  }



  @Test
  public void testCleanup() {

    final Landscape landscapeOld = LandscapeDummyCreator.createDummyLandscape(this.idGenerator);
    final long ts1 = 1546300800L; // NOPMD

    final Landscape landscapeNew = LandscapeDummyCreator.createDummyLandscape(this.idGenerator);
    final long ts2 = System.currentTimeMillis();

    this.repo.save(ts1, landscapeOld, 0);
    this.repo.save(ts2, landscapeNew, 0);

    // cleanup old landscape based on configuration
    this.repo.cleanup();

    final int timestamps = this.timestampRepo.getLandscapeTimestamps().size();
    assertEquals("Amount of objects don't match", 1, timestamps);
  }

}
