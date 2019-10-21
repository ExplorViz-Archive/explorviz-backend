package net.explorviz.history.repository.persistence.mongo;

import static org.junit.Assert.assertEquals;
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
 * Tests the {@link MongoLandscapeRepositoryTest}. Expects a running mongodb server.
 *
 */
public class MongoLandscapeRepositoryTest {

  @Inject
  private MongoLandscapeRepository repo;

  @Inject
  private IdGenerator idGenerator;

  @BeforeClass
  public static void setUpAll() {
    HistoryApplication.registerLandscapeModels();
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
    final Landscape landscape = LandscapeDummyCreator.createDummyLandscape(this.idGenerator);
    this.repo.save(ts, landscape, 0);

    final Optional<Landscape> landscapeRetrieved = this.repo.getByTimestamp(ts);

    assertEquals("Ids don't match", landscape.getId(), landscapeRetrieved.get().getId()); // NOPMD
                                                                                          // NOCS

  }


  @Test
  public void testTotalRequets() {
    final long ts = System.currentTimeMillis();
    final Landscape landscape = LandscapeDummyCreator.createDummyLandscape(this.idGenerator);
    this.repo.save(ts, landscape, 0);

    final Optional<Landscape> landscapeRetrieved = this.repo.getByTimestamp(ts);

    assertEquals("Requests don't match", landscape.getId(), landscapeRetrieved.get().getId()); // NOPMD
  }

  @Test
  public void testFindById() {
    final long ts = System.currentTimeMillis();
    final Landscape landscape = LandscapeDummyCreator.createDummyLandscape(this.idGenerator);
    final Landscape landscape2 = LandscapeDummyCreator.createDummyLandscape(this.idGenerator);
    this.repo.save(ts, landscape, 0);
    this.repo.save(ts, landscape2, 0);

    final String id = landscape.getId();

    final Optional<Landscape> landscapeRetrieved = this.repo.getByTimestamp(ts);

    assertEquals("Ids don't match", id, landscapeRetrieved.get().getId());

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


}
