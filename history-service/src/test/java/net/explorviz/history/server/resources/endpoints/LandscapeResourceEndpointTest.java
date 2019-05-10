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
import net.explorviz.history.repository.persistence.LandscapeRepository;
import net.explorviz.history.repository.persistence.ReplayRepository;
import net.explorviz.history.repository.persistence.mongo.LandscapeDummyCreator;
import net.explorviz.history.repository.persistence.mongo.LandscapeSerializationHelper;
import net.explorviz.history.server.resources.LandscapeResource;
import net.explorviz.history.server.resources.LandscapeResourceTest;
import net.explorviz.shared.common.idgen.AtomicEntityIdGenerator;
import net.explorviz.shared.common.idgen.IdGenerator;
import net.explorviz.shared.common.idgen.UuidServiceIdGenerator;
import net.explorviz.shared.landscape.model.helper.TypeProvider;
import net.explorviz.shared.landscape.model.landscape.Landscape;
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

  private static final String QUERY_PARAM_USER_UPLOADED = "returnUploadedLandscapes";
  private static final String QUERY_PARAM_START_TIMESTAMP = "startTimestamp";
  private static final String QUERY_PARAM_INTERVAL_SIZE = "intervalSize";
  private static final String QUERY_PARAM_MAX_LENGTH = "maxLength";

  private static final String GENERIC_STATUS_ERROR_MESSAGE = "Wrong HTTP Status code.";
  private static final String GENERIC_MEDIA_TYPE_ERROR_MESSAGE = "Wrong media type.";

  private LandscapeRepository<String> landscapeRepo;
  private ReplayRepository<String> replayRepo;

  private String currentLandscape;
  private String currentLandscapeId;

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

    landscapeRepo = Mockito.mock(LandscapeRepository.class);
    replayRepo = Mockito.mock(ReplayRepository.class);

    when(this.landscapeRepo.getById(this.currentLandscapeId))
        .thenReturn(Optional.of(this.currentLandscape));
    when(this.landscapeRepo.getById("2L"))
        .thenThrow(new NotFoundException("Landscape not found for provided 2L."));
    // when(this.replayRepo.getAllTimestamps()).thenReturn(this.userUploadedTimestamps);

    return new ResourceConfig()
        .register(new LandscapeResource(this.landscapeRepo, this.replayRepo));
  }

  @Test
  public void checkOkStatusCodes() { // NOPMD
    final Response response = target().path(BASE_URL + "/" + currentLandscapeId).request().get();
    assertEquals(GENERIC_STATUS_ERROR_MESSAGE, Status.OK.getStatusCode(), response.getStatus());
  }

  @Test
  public void checkNotFoundStatusCodeForUnknownId() { // NOPMD
    final Response response = target().path(BASE_URL + "/2L").request().get();
    assertEquals(GENERIC_STATUS_ERROR_MESSAGE, Status.NOT_FOUND.getStatusCode(),
        response.getStatus());
  }

  @Test
  public void checkNotFoundStatusCodeForUnknownLandscape() {
    final Response response = target().path(BASE_URL + "/12").request().get();
    assertEquals(GENERIC_STATUS_ERROR_MESSAGE, Status.NOT_FOUND.getStatusCode(),
        response.getStatus());
  }

  @Test
  public void checkNotAcceptableMediaTypeStatusCode() {
    final Response response = target().path(BASE_URL + "/" + currentLandscapeId).request()
        .accept(MediaType.TEXT_PLAIN).get();
    assertEquals(GENERIC_MEDIA_TYPE_ERROR_MESSAGE, Status.NOT_ACCEPTABLE.getStatusCode(),
        response.getStatus());
  }

  @Test
  public void checkMediaTypeOfValidRequestAndResponse() {
    final Response response = target().path(BASE_URL + "/" + currentLandscapeId).request().get();
    assertEquals(GENERIC_MEDIA_TYPE_ERROR_MESSAGE, MEDIA_TYPE, response.getMediaType().toString());
  }

  @Test
  public void checkMediaTypeOfValidRequestAndResponseWithAcceptHeader() {
    final Response response =
        target().path(BASE_URL + "/" + currentLandscapeId).request().accept(MEDIA_TYPE).get();
    assertEquals(GENERIC_MEDIA_TYPE_ERROR_MESSAGE, MEDIA_TYPE, response.getMediaType().toString());
  }



  // TODO test for valid response and JSON-API conformity

}
