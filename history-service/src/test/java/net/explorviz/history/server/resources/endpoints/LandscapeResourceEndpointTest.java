package net.explorviz.history.server.resources.endpoints;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;
import com.github.jasminb.jsonapi.ResourceConverter;
import com.github.jasminb.jsonapi.SerializationFeature;
import com.github.jasminb.jsonapi.exceptions.DocumentSerializationException;
import java.util.Optional;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import net.explorviz.history.helper.LandscapeDummyCreator;
import net.explorviz.history.repository.persistence.LandscapeRepository;
import net.explorviz.history.repository.persistence.ReplayRepository;
import net.explorviz.history.repository.persistence.mongo.LandscapeSerializationHelper;
import net.explorviz.history.server.resources.LandscapeResource;
import net.explorviz.history.server.resources.LandscapeResourceTest;
import net.explorviz.landscape.model.helper.TypeProvider;
import net.explorviz.landscape.model.landscape.Landscape;
import net.explorviz.landscape.model.store.Timestamp;
import net.explorviz.shared.common.idgen.AtomicEntityIdGenerator;
import net.explorviz.shared.common.idgen.IdGenerator;
import net.explorviz.shared.common.idgen.UuidServiceIdGenerator;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link LandscapeResource}. All tests are performed by calling the HTTP endpoints
 * of {@link LandscapeResource} via HTTP client requests. See {@link LandscapeResourceTest} for
 * tests that use method level calls instead of HTTP requests.
 */
public class LandscapeResourceEndpointTest extends JerseyTest {

  private static final String MEDIA_TYPE = "application/vnd.api+json";
  private static final String BASE_URL = "v1/landscapes";

  private static final String QUERY_PARAM_TIMESTAMP = "timestamp";

  private static final String GENERIC_STATUS_ERR_MESSAGE = "Wrong HTTP Status code.";
  private static final String GENERIC_MEDIA_TYPE_ERR_MESSAGE = "Wrong media type.";

  private LandscapeRepository<String> landscapeStringRepo; // NOPMD
  private ReplayRepository<String> replayStringRepo; // NOPMD

  private String currentLandscape; // NOPMD
  private String currentLandscapeId;

  private Timestamp currentLandscapeTimestamp;

  @SuppressWarnings("unchecked")
  @Override
  protected Application configure() {

    final IdGenerator idGen = new IdGenerator(new UuidServiceIdGenerator(),
        new AtomicEntityIdGenerator(), "history-test");

    final ResourceConverter rC = new ResourceConverter(TypeProvider.getExplorVizCoreTypesAsArray());
    rC.enableSerializationOption(SerializationFeature.INCLUDE_RELATIONSHIP_ATTRIBUTES);

    final LandscapeSerializationHelper serializationHelper = new LandscapeSerializationHelper(rC);

    final Landscape l = LandscapeDummyCreator.createDummyLandscape(idGen);
    try {
      this.currentLandscape = serializationHelper.serialize(l);
    } catch (final DocumentSerializationException e) {
      fail("Failed test since landscape serialization in configure() method failed.");
    }
    this.currentLandscapeId = l.getId();
    this.currentLandscapeTimestamp = l.getTimestamp();

    this.landscapeStringRepo = Mockito.mock(LandscapeRepository.class);
    this.replayStringRepo = Mockito.mock(ReplayRepository.class);

    when(this.landscapeStringRepo.getById(this.currentLandscapeId))
        .thenReturn(Optional.of(this.currentLandscape));
    when(this.landscapeStringRepo.getByTimestamp(this.currentLandscapeTimestamp.getTimestamp()))
        .thenReturn(Optional.of(this.currentLandscape));
    when(this.landscapeStringRepo.getById("2L"))
        .thenThrow(new NotFoundException("Landscape not found for provided 2L."));

    final ResourceConfig rc = new ResourceConfig();
    rc.register(MultiPartFeature.class);
    rc.register(new LandscapeResource(this.landscapeStringRepo, this.replayStringRepo,
        serializationHelper));

    return rc;
  }

  @Test
  public void checkOkStatusCodes() { // NOPMD
    Response response =
        this.target().path(BASE_URL + "/" + this.currentLandscapeId).request().get();
    assertEquals(GENERIC_STATUS_ERR_MESSAGE, Status.OK.getStatusCode(), response.getStatus());

    response = this.target()
        .path(BASE_URL)
        .queryParam(QUERY_PARAM_TIMESTAMP, this.currentLandscapeTimestamp.getTimestamp())
        .request()
        .accept(MEDIA_TYPE)
        .get();

    // TODO why fail?
    assertEquals(GENERIC_STATUS_ERR_MESSAGE, Status.OK.getStatusCode(), response.getStatus());
  }

  @Test
  public void checkNotFoundStatusCodeForUnknownId() { // NOPMD
    Response response = this.target().path(BASE_URL + "/2L").request().get();
    assertEquals(GENERIC_STATUS_ERR_MESSAGE,
        Status.NOT_FOUND.getStatusCode(),
        response.getStatus());

    response = this.target()
        .path(BASE_URL)
        .queryParam(QUERY_PARAM_TIMESTAMP, "2")
        .request()
        .accept(MEDIA_TYPE)
        .get();

    assertEquals(GENERIC_STATUS_ERR_MESSAGE,
        Status.NOT_FOUND.getStatusCode(),
        response.getStatus());
  }

  @Test
  public void checkNotFoundStatusCodeForUnknownLandscape() {
    final Response response = this.target().path(BASE_URL + "/12").request().get();
    assertEquals(GENERIC_STATUS_ERR_MESSAGE,
        Status.NOT_FOUND.getStatusCode(),
        response.getStatus());
  }

  @Test
  public void checkNotAcceptableMediaTypeStatusCode() {
    final Response response = this.target()
        .path(BASE_URL + "/" + this.currentLandscapeId)
        .request()
        .accept(MediaType.TEXT_PLAIN)
        .get();
    assertEquals(GENERIC_MEDIA_TYPE_ERR_MESSAGE,
        Status.NOT_ACCEPTABLE.getStatusCode(),
        response.getStatus());
  }

  @Test
  public void checkBadRequestErrorCodeOnMissingQueryParam() {
    final Response response = this.target().path(BASE_URL).request().get();
    assertEquals(GENERIC_MEDIA_TYPE_ERR_MESSAGE,
        Status.BAD_REQUEST.getStatusCode(),
        response.getStatus());
  }

  @Test
  public void checkMediaTypeOfValidRequestAndResponse() {
    final Response response =
        this.target().path(BASE_URL + "/" + this.currentLandscapeId).request().get();
    assertEquals(GENERIC_MEDIA_TYPE_ERR_MESSAGE, MEDIA_TYPE, response.getMediaType().toString());
  }

  @Test
  public void checkMediaTypeOfValidRequestAndResponseWithAcceptHeader() {
    final Response response = this.target()
        .path(BASE_URL + "/" + this.currentLandscapeId)
        .request()
        .accept(MEDIA_TYPE)
        .get();
    assertEquals(GENERIC_MEDIA_TYPE_ERR_MESSAGE, MEDIA_TYPE, response.getMediaType().toString());
  }

  @Test
  public void checkQueryEndpointSuccess() {
    final Response response = this.target()
        .path(BASE_URL)
        .queryParam(QUERY_PARAM_TIMESTAMP, this.currentLandscapeTimestamp.getTimestamp())
        .request()
        .accept(MEDIA_TYPE)
        .get();

    assertEquals("Query Parameter endpoint returned wrong value",
        this.currentLandscape,
        response.readEntity(String.class));
  }

  // TODO test for valid response and JSON-API conformity

}
