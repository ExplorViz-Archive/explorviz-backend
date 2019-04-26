package net.explorviz.history.server.resources;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import net.explorviz.history.repository.persistence.LandscapeRepository;
import net.explorviz.history.repository.persistence.ReplayRepository;
import net.explorviz.shared.landscape.model.landscape.Landscape;
import net.explorviz.shared.landscape.model.store.Timestamp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link TimestampResourceTest}. All tests are performed by just calling the methods
 * of {@link TimestampResourceTest}. See {@link TimestampResourceEndpointTest} for tests that use
 * web requests.
 */
@ExtendWith(MockitoExtension.class)
public class TimestampResourceTest {

  private TimestampResource timestampResource;

  @Mock
  private LandscapeRepository<Landscape> landscapeRepo;

  @Mock
  private ReplayRepository<Landscape> replayRepo;

  private List<Timestamp> serviceGeneratedTimestamps;
  private List<Timestamp> userUploadedTimestamps;

  @BeforeEach
  public void setUp() {

    // CHECKSTYLE.OFF: MagicNumber
    serviceGeneratedTimestamps = new ArrayList<>();
    serviceGeneratedTimestamps.add(new Timestamp(1_556_302_800, 300));
    serviceGeneratedTimestamps.add(new Timestamp(1_556_302_810, 400));
    serviceGeneratedTimestamps.add(new Timestamp(1_556_302_820, 500));

    userUploadedTimestamps = new ArrayList<>();
    userUploadedTimestamps.add(new Timestamp(1_556_302_860, 600));
    userUploadedTimestamps.add(new Timestamp(1_556_302_870, 700));
    userUploadedTimestamps.add(new Timestamp(1_556_302_880, 800));
    // CHECKSTYLE.ON: MagicNumber

    when(this.landscapeRepo.getAllTimestamps()).thenReturn(this.serviceGeneratedTimestamps);
    when(this.replayRepo.getAllTimestamps()).thenReturn(this.userUploadedTimestamps);

    timestampResource = new TimestampResource(landscapeRepo, replayRepo);

  }

  @Test
  @DisplayName("No params (default param values) should return all service generated timestamps")
  public void noQueryParam() {
    assertEquals(this.serviceGeneratedTimestamps, timestampResource.getTimestamps(0L, 0, false),
        "Wrong return value for timestamp resource.");
  }
}
