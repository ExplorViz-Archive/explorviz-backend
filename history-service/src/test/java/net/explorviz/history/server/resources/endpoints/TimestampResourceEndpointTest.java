package net.explorviz.history.server.resources.endpoints;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import net.explorviz.history.repository.persistence.mongo.TimestampRepository;
import net.explorviz.history.server.resources.TimestampResource;
import net.explorviz.history.server.resources.TimestampResourceTest;
import net.explorviz.landscape.model.store.Timestamp;
import net.explorviz.shared.querying.QueryException;
import net.explorviz.shared.querying.QueryResult;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

/**
 * Unit tests for {@link TimestampResource}. All tests are performed by calling the HTTP endpoints
 * of {@link TimestampResource} via HTTP client requests. See {@link TimestampResourceTest} for
 * tests that use method level calls instead of HTTP requests.
 */
public class TimestampResourceEndpointTest extends JerseyTest {

  private static final String MEDIA_TYPE = "application/vnd.api+json";
  private static final String BASE_URL = "v1/timestamps";

  private static final String GENERIC_STATUS_ERROR_MESSAGE = "Wrong HTTP Status code.";
  private static final String GENERIC_MEDIA_TYPE_ERROR_MESSAGE = "Wrong media type.";

  private static final String FILTER_FROM = "filter[from]";
  private static final String FILTER_TYPE = "filter[type]";
  private static final String PAGE_SIZE = "page[size]";
  private static final String PAGE_NUMBER = "page[number]";


  private TimestampRepository timestampRepo;

  private List<Timestamp> serviceGeneratedTimestamps;
  private List<Timestamp> userUploadedTimestamps;

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

    this.timestampRepo = Mockito.mock(TimestampRepository.class);

    when(this.timestampRepo.getLandscapeTimestamps()).thenReturn(this.serviceGeneratedTimestamps);
    when(this.timestampRepo.getReplayTimestamps()).thenReturn(this.userUploadedTimestamps);

    return new ResourceConfig().register(new TimestampResource(this.timestampRepo));
  }

  @Test
  public void checkOkStatusCodes() throws QueryException { // NOPMD
    when(this.timestampRepo.query(ArgumentMatchers.any())).thenCallRealMethod();
    Response response = this.target().path(BASE_URL).request().get();
    assertEquals("Wrong HTTP Status code for all service-generated timestamps: ",
        Status.OK.getStatusCode(),
        response.getStatus());

    response = this.target()
        .path(BASE_URL)
        .queryParam(FILTER_FROM, 1_556_302_800L) // NOCS
        .request()
        .get();
    assertEquals("Wrong HTTP Status code for service-generated timestamps with starting timestamp",
        Status.OK.getStatusCode(),
        response.getStatus());

    response = this.target()
        .path(BASE_URL)
        .queryParam(FILTER_FROM, 1_556_302_800L) // NOCS
        .queryParam(PAGE_SIZE, 2) // NOCS
        .queryParam(PAGE_NUMBER, 0)
        .request()
        .get();
    assertEquals(
        "Wrong HTTP Status code for service-generated timestamps"
            + "with starting timestamp and intervalsize",
        Status.OK.getStatusCode(),
        response.getStatus());

    response = this.target().path(BASE_URL).queryParam(FILTER_TYPE, "replay").request().get();
    assertEquals("Wrong HTTP Status code for all user-uploaded timestamps: ",
        Status.OK.getStatusCode(),
        response.getStatus());
  }

  @Test
  public void checkBadRequestStatusCodes() throws QueryException { // NOPMD
    when(this.timestampRepo.query(ArgumentMatchers.any())).thenCallRealMethod();
    Response response =
        this.target().path(BASE_URL).queryParam(FILTER_FROM, "nonumber").request().get();
    assertEquals(GENERIC_STATUS_ERROR_MESSAGE,
        Status.BAD_REQUEST.getStatusCode(),
        response.getStatus());

    response = this.target().path(BASE_URL).queryParam(FILTER_FROM, -1).request().get();
    assertEquals(GENERIC_STATUS_ERROR_MESSAGE,
        Status.BAD_REQUEST.getStatusCode(),
        response.getStatus());
  }



  @Test
  public void checkNotAcceptableMediaTypeStatusCode() {
    final Response response =
        this.target().path(BASE_URL).request().accept(MediaType.TEXT_PLAIN).get();
    assertEquals(GENERIC_MEDIA_TYPE_ERROR_MESSAGE,
        Status.NOT_ACCEPTABLE.getStatusCode(),
        response.getStatus());
  }

  @Test
  public void checkMediaTypeOfValidRequestAndResponse() throws QueryException {

    when(this.timestampRepo.query(ArgumentMatchers.any()))
        .thenReturn(new QueryResult<Timestamp>(null, null, -1));

    final Response response = this.target().path(BASE_URL).request().get();
    assertEquals(GENERIC_MEDIA_TYPE_ERROR_MESSAGE, MEDIA_TYPE, response.getMediaType().toString());
  }

  @Test
  public void checkMediaTypeOfValidRequestAndResponseWithAcceptHeader() throws QueryException {
    when(this.timestampRepo.query(ArgumentMatchers.any())).thenCallRealMethod();
    final Response response = this.target().path(BASE_URL).request().accept(MEDIA_TYPE).get();
    assertEquals(GENERIC_MEDIA_TYPE_ERROR_MESSAGE, MEDIA_TYPE, response.getMediaType().toString());
  }

  // TODO test for valid response and JSON-API conformity

}
