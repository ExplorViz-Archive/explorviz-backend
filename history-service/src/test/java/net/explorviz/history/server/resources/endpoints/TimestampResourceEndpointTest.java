package net.explorviz.history.server.resources.endpoints;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import net.explorviz.history.repository.persistence.LandscapeRepository;
import net.explorviz.history.repository.persistence.ReplayRepository;
import net.explorviz.history.server.resources.TimestampResource;
import net.explorviz.history.server.resources.TimestampResourceTest;
import net.explorviz.shared.landscape.model.landscape.Landscape;
import net.explorviz.shared.landscape.model.store.Timestamp;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link TimestampResource}. All tests are performed by calling the HTTP endpoints
 * of {@link TimestampResource} via HTTP client requests. See {@link TimestampResourceTest} for
 * tests that use method level calls instead of HTTP requests.
 */
public class TimestampResourceEndpointTest extends JerseyTest {

  private static final String MEDIA_TYPE = "application/vnd.api+json";
  private static final String BASE_URL = "v1/timestamps";

  private static final String QUERY_PARAM_USER_UPLOADED = "returnUploadedTimestamps";
  private static final String QUERY_PARAM_START_TIMESTAMP = "startTimestamp";
  private static final String QUERY_PARAM_INTERVAL_SIZE = "intervalSize";

  private LandscapeRepository<Landscape> landscapeRepo;
  private ReplayRepository<Landscape> replayRepo;

  private List<Timestamp> serviceGeneratedTimestamps;
  private List<Timestamp> userUploadedTimestamps;

  @SuppressWarnings("unchecked")
  @Override
  protected Application configure() {

    // Called for each test

    // CHECKSTYLE.OFF: MagicNumber
    this.serviceGeneratedTimestamps = new ArrayList<>();
    this.serviceGeneratedTimestamps.add(new Timestamp("1", 1_556_302_800L, 300));
    this.serviceGeneratedTimestamps.add(new Timestamp("2", 1_556_302_810L, 400));
    this.serviceGeneratedTimestamps.add(new Timestamp("3", 1_556_302_820L, 500));
    this.serviceGeneratedTimestamps.add(new Timestamp("4", 1_556_302_830L, 600));
    this.serviceGeneratedTimestamps.add(new Timestamp("5", 1_556_302_840L, 700));
    this.serviceGeneratedTimestamps.add(new Timestamp("6", 1_556_302_850L, 800));

    this.userUploadedTimestamps = new ArrayList<>();
    this.userUploadedTimestamps.add(new Timestamp("7", 1_556_302_860L, 600));
    this.userUploadedTimestamps.add(new Timestamp("8", 1_556_302_870L, 700));
    this.userUploadedTimestamps.add(new Timestamp("9", 1_556_302_880L, 800));
    this.userUploadedTimestamps.add(new Timestamp("10", 1_556_302_890L, 900));
    this.userUploadedTimestamps.add(new Timestamp("11", 1_556_302_900L, 1000));
    this.userUploadedTimestamps.add(new Timestamp("12", 1_556_302_910L, 1100));
    // CHECKSTYLE.ON: MagicNumber

    landscapeRepo = Mockito.mock(LandscapeRepository.class);
    replayRepo = Mockito.mock(ReplayRepository.class);

    when(this.landscapeRepo.getAllTimestamps()).thenReturn(this.serviceGeneratedTimestamps);
    when(this.replayRepo.getAllTimestamps()).thenReturn(this.userUploadedTimestamps);

    return new ResourceConfig()
        .register(new TimestampResource(this.landscapeRepo, this.replayRepo));
  }

  @Test
  public void checkStatusCodes() {
    Response response = target().path(BASE_URL).request().get();
    assertEquals("Wrong HTTP Status code for all service-generated timestamps: ",
        Status.OK.getStatusCode(), response.getStatus());

    response = target().path(BASE_URL).queryParam(QUERY_PARAM_START_TIMESTAMP, 1_556_302_800L) // NOCS
        .request().get();
    assertEquals("Wrong HTTP Status code for service-generated timestamps with starting timestamp",
        Status.OK.getStatusCode(), response.getStatus());

    response = target().path(BASE_URL).queryParam(QUERY_PARAM_START_TIMESTAMP, 1_556_302_800L) // NOCS
        .queryParam(QUERY_PARAM_INTERVAL_SIZE, 2) // NOCS
        .request().get();
    assertEquals(
        "Wrong HTTP Status code for service-generated timestamps with starting timestamp and intervalsize",
        Status.OK.getStatusCode(), response.getStatus());

    response = target().path(BASE_URL).queryParam(QUERY_PARAM_USER_UPLOADED, true).request().get();
    assertEquals("Wrong HTTP Status code for all user-uploaded timestamps: ",
        Status.OK.getStatusCode(), response.getStatus());
  }

}
