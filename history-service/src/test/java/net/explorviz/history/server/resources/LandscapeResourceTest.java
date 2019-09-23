package net.explorviz.history.server.resources;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import com.github.jasminb.jsonapi.ResourceConverter;
import com.github.jasminb.jsonapi.SerializationFeature;
import com.github.jasminb.jsonapi.exceptions.DocumentSerializationException;
import java.util.Optional;
import javax.ws.rs.NotFoundException;
import net.explorviz.history.helper.LandscapeDummyCreator;
import net.explorviz.history.repository.persistence.LandscapeRepository;
import net.explorviz.history.repository.persistence.ReplayRepository;
import net.explorviz.history.repository.persistence.mongo.LandscapeSerializationHelper;
import net.explorviz.history.server.resources.endpoints.LandscapeResourceEndpointTest;
import net.explorviz.landscape.model.helper.TypeProvider;
import net.explorviz.landscape.model.landscape.Landscape;
import net.explorviz.shared.common.idgen.AtomicEntityIdGenerator;
import net.explorviz.shared.common.idgen.IdGenerator;
import net.explorviz.shared.common.idgen.UuidServiceIdGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link LandscapeResource}. All tests are performed by just calling the methods of
 * {@link LandscapeResource}. See {@link LandscapeResourceEndpointTest} for tests that use web
 * requests.
 */
@ExtendWith(MockitoExtension.class)
public class LandscapeResourceTest {

  private LandscapeResource landscapeResouce;

  private String currentLandscape;
  private String currentLandscapeId;

  @Mock(lenient = true)
  private LandscapeRepository<String> landscapeStringRepo;

  @Mock(lenient = true)
  private ReplayRepository<String> replayStringRepo;

  @BeforeEach
  public void setUp() throws DocumentSerializationException {

    final IdGenerator idGen = new IdGenerator(new UuidServiceIdGenerator(),
        new AtomicEntityIdGenerator(), "history-test");

    final ResourceConverter rC = new ResourceConverter(TypeProvider.getExplorVizCoreTypesAsArray());
    rC.enableSerializationOption(SerializationFeature.INCLUDE_RELATIONSHIP_ATTRIBUTES);

    final LandscapeSerializationHelper serializationHelper = new LandscapeSerializationHelper(rC);

    final Landscape l = LandscapeDummyCreator.createDummyLandscape(idGen);
    this.currentLandscape = serializationHelper.serialize(l);
    this.currentLandscapeId = l.getId();

    when(this.landscapeStringRepo.getById(this.currentLandscapeId))
        .thenReturn(Optional.of(this.currentLandscape));
    // when(this.replayRepo.getAllTimestamps()).thenReturn(this.userUploadedTimestamps);

    this.landscapeResouce =
        new LandscapeResource(this.landscapeStringRepo, this.replayStringRepo, serializationHelper);
  }

  @Test
  @DisplayName("Return landscape by id.")
  public void giveAllServiceGenerated() {
    assertEquals(this.currentLandscape,
        this.landscapeResouce.getLandscapeById(this.currentLandscapeId),
        "Wrong landscape was returned.");
  }

  @Test
  @DisplayName("Unknown landscape id throws NotFoundException.")
  public void checkUnknownLandscapeException() {
    assertThrows(NotFoundException.class, () -> {
      this.landscapeResouce.getLandscapeById("not-a-valid-id");
    });
  }


}
