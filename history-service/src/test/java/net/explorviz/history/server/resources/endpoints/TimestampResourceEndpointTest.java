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
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Unit tests for {@link TimestampResource}. All tests are performed by calling the HTTP endpoints
 * of {@link TimestampResource} via HTTP client requests. See {@link TimestampResourceTest} for
 * tests that use method level calls instead of HTTP requests.
 */
@RunWith(MockitoJUnitRunner.class)
public class TimestampResourceEndpointTest extends JerseyTest {

  private static final String MEDIA_TYPE = "application/vnd.api+json";
  private static final String BASE_URL = "v1/timestamps";

  private TimestampResource timestampResource;

  @Mock(lenient = true)
  private LandscapeRepository<Landscape> landscapeRepo;

  @Mock(lenient = true)
  private ReplayRepository<Landscape> replayRepo;

  private List<Timestamp> serviceGeneratedTimestamps;
  private List<Timestamp> userUploadedTimestamps;

  @Override
  protected Application configure() {
    return new ResourceConfig(TimestampResource.class);
  }

  @Override
  public void setUp() {

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

    when(this.landscapeRepo.getAllTimestamps()).thenReturn(this.serviceGeneratedTimestamps);
    when(this.replayRepo.getAllTimestamps()).thenReturn(this.userUploadedTimestamps);

    this.timestampResource = new TimestampResource(this.landscapeRepo, this.replayRepo);

  }

  // TODO CommonEndpointTest class with Application + DI/Mock Setup

  @Test
  public void allServiceGeneratedTimestampsStatusCode() {
    final Response response = target().path(BASE_URL).request().get();



    assertEquals("Http Response should be 200: ", Status.OK.getStatusCode(), response.getStatus());

    // assertEquals("Http Content-Type should be: ", MEDIA_TYPE,
    // response.getHeaderString(HttpHeaders.CONTENT_TYPE));

    // final String content = response.readEntity(String.class);
    // assertEquals("Content of ressponse is: ", "hi", content);
  }

}
