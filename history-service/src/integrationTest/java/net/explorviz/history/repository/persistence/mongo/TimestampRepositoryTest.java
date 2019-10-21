package net.explorviz.history.repository.persistence.mongo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.Collection;
import javax.inject.Inject;
import javax.ws.rs.core.MultivaluedHashMap;
import net.explorviz.history.server.main.DependencyInjectionBinder;
import net.explorviz.history.server.main.HistoryApplication;
import net.explorviz.landscape.model.landscape.Landscape;
import net.explorviz.landscape.model.store.Timestamp;
import net.explorviz.shared.common.idgen.IdGenerator;
import net.explorviz.shared.querying.Query;
import net.explorviz.shared.querying.QueryException;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TimestampRepositoryTest {


  @Inject
  private MongoLandscapeRepository landscapeRepo;
  @Inject
  private MongoReplayRepository replayRepository;
  @Inject
  private TimestampRepository timestampRepo;
  @Inject
  private IdGenerator idGenerator;

  @BeforeAll
  public static void setUpAll() {
    HistoryApplication.registerLandscapeModels();
  }

  @BeforeEach
  public void setUp() {
    if (this.timestampRepo == null) {
      final DependencyInjectionBinder binder = new DependencyInjectionBinder();
      final ServiceLocator locator = ServiceLocatorUtilities.bind(binder);
      locator.inject(this);
    }
    this.landscapeRepo.clear();
    this.replayRepository.clear();
  }

  @Test
  public void testLandscapeTimestamps() {
    this.addLandscapes();

    final int timestamps = this.timestampRepo.getLandscapeTimestamps().size();
    assertEquals(2, timestamps); // NOCS
  }

  @Test
  public void testReplayTimestamps() {
    this.addReplays();

    final int timestamps = this.timestampRepo.getReplayTimestamps().size();
    assertEquals(2, timestamps); // NOCS
  }

  private void addLandscapes() {
    final Landscape landscape = LandscapeDummyCreator.createDummyLandscape(this.idGenerator);
    final long ts = System.currentTimeMillis();
    final Landscape landscape2 = LandscapeDummyCreator.createDummyLandscape(this.idGenerator);
    final long ts2 = ts + 1;
    this.landscapeRepo.save(ts, landscape, 0);
    this.landscapeRepo.save(ts2, landscape2, 0);
  }

  private void addReplays() {
    final Landscape replay = LandscapeDummyCreator.createDummyLandscape(this.idGenerator);
    final long replayTs = System.currentTimeMillis();
    final Landscape replay2 = LandscapeDummyCreator.createDummyLandscape(this.idGenerator);
    // Calling currentTimeMillis() again will result in the same timestamp
    final long replayTs2 = replayTs + 1;
    this.replayRepository.save(replayTs, replay, 0);
    this.replayRepository.save(replayTs2, replay2, 0);
  }

  @Test
  public void testQueryForLandscapes() throws QueryException {
    this.addLandscapes();
    this.addReplays();

    final MultivaluedHashMap<String, String> paramters = new MultivaluedHashMap<>();
    paramters.add("filter[type]", "landscape");
    final Query<Timestamp> q = Query.fromParameterMap(paramters);
    final Collection<Timestamp> result = this.timestampRepo.query(q).getData();
    assertEquals(2, result.size());
  }

  @Test
  public void testQueryForReplays() throws QueryException {
    this.addLandscapes();
    this.addReplays();

    final MultivaluedHashMap<String, String> paramters = new MultivaluedHashMap<>();
    paramters.add("filter[type]", "replay");
    final Query<Timestamp> q = Query.fromParameterMap(paramters);
    final Collection<Timestamp> result = this.timestampRepo.query(q).getData();
    assertEquals(2, result.size());
  }

  @Test
  public void testQueryForAll() throws QueryException {
    this.addLandscapes();
    this.addReplays();

    final MultivaluedHashMap<String, String> paramters = new MultivaluedHashMap<>();
    final Query<Timestamp> q = Query.fromParameterMap(paramters);
    final Collection<Timestamp> result = this.timestampRepo.query(q).getData();
    assertEquals(4, result.size());
  }

  @Test
  public void testQueryFrom() throws QueryException {
    final Landscape landscape = LandscapeDummyCreator.createDummyLandscape(this.idGenerator);
    final long ts = 1l;
    final Landscape landscape2 = LandscapeDummyCreator.createDummyLandscape(this.idGenerator);
    final long ts2 = 2l;
    final Landscape landscape3 = LandscapeDummyCreator.createDummyLandscape(this.idGenerator);
    final long ts3 = 3l;

    this.landscapeRepo.save(ts, landscape, 0);
    this.landscapeRepo.save(ts2, landscape2, 0);
    this.landscapeRepo.save(ts3, landscape3, 0);

    final MultivaluedHashMap<String, String> paramters = new MultivaluedHashMap<>();
    paramters.add("filter[from]", "2");
    final Query<Timestamp> q = Query.fromParameterMap(paramters);
    final Collection<Timestamp> result = this.timestampRepo.query(q).getData();
    assertEquals(2l, result.size());

  }

  @Test
  public void testPagination() throws QueryException {
    this.addLandscapes();
    this.addReplays();

    final MultivaluedHashMap<String, String> paramters = new MultivaluedHashMap<>();
    paramters.add("page[number]", "1");
    paramters.add("page[size]", "2");
    final Query<Timestamp> q = Query.fromParameterMap(paramters);
    final Collection<Timestamp> result = this.timestampRepo.query(q).getData();

    assertEquals(2, result.size());
  }


  @Test
  public void testInvalidFromFilterValue() {
    this.addLandscapes();
    this.addReplays();

    final MultivaluedHashMap<String, String> paramters = new MultivaluedHashMap<>();
    paramters.add("filter[from]", "notanint");
    final Query<Timestamp> q = Query.fromParameterMap(paramters);

    assertThrows(QueryException.class, () -> this.timestampRepo.query(q));
  }


}
