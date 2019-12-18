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
 * Tests the {@link MongoReplayJsonApiRepositoryTest}. Expects a running mongodb server.
 *
 */
public class MongoReplayJsonApiRepositoryTest {

  @Inject
  private MongoReplayJsonApiRepository repo;

  @Inject
  private TimestampRepository timestampRepo;

  @Inject
  private IdGenerator idGenerator;

  @BeforeClass
  public static void setUpAll() {
    HistoryApplication.registerLandscapeModels();
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
    final Landscape landscape = LandscapeDummyCreator.createDummyLandscape(this.idGenerator);
    this.repo.save(ts, landscape, 0);

    final Optional<String> rawLandscape = this.repo.getByTimestamp(ts);

    assertTrue("Invalid landscape",
        rawLandscape.get().startsWith("{\"data\":{\"type\":\"landscape\""));
  }

  @Test
  public void findReplayById() {
    final long ts = System.currentTimeMillis();
    final Landscape landscape = LandscapeDummyCreator.createDummyLandscape(this.idGenerator);
    final Landscape landscape2 = LandscapeDummyCreator.createDummyLandscape(this.idGenerator);
    this.repo.save(ts, landscape, 0);
    this.repo.save(ts, landscape2, 0);

    final String id = landscape.getId();
    final Optional<String> rawLandscape = this.repo.getById(id);

    assertTrue("Ivalid landscape or wrong id",
        rawLandscape.get().startsWith("{\"data\":{\"type\":\"landscape\",\"id\":\"" + id + "\""));
  }

  @Test
  public void testTotalRequestsReplay() {
    final Random rand = new Random();
    final long ts = System.currentTimeMillis();
    final int requests = rand.nextInt(Integer.MAX_VALUE) + 1;
    final Landscape landscape = LandscapeDummyCreator.createDummyLandscape(this.idGenerator);
    this.repo.save(ts, landscape, requests);

    final int retrievedRequests = this.repo.getTotalRequestsByTimestamp(ts);
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

    final int timestamps = this.timestampRepo.getReplayTimestamps().size();
    assertEquals("Amount of objects don't match", 1, timestamps);
  }

}
