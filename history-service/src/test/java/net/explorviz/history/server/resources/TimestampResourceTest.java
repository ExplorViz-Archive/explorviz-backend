package net.explorviz.history.server.resources;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import net.explorviz.history.repository.persistence.LandscapeRepository;
import net.explorviz.history.repository.persistence.ReplayRepository;
import net.explorviz.shared.common.idgen.IdGenerator;
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

  @Mock(lenient = true)
  private LandscapeRepository<Landscape> landscapeRepo;

  @Mock(lenient = true)
  private ReplayRepository<Landscape> replayRepo;

  @Mock(lenient = true)
  private IdGenerator idGen;

  private List<Timestamp> serviceGeneratedTimestamps;
  private List<Timestamp> userUploadedTimestamps;

  @BeforeEach
  public void setUp() {

    // CHECKSTYLE.OFF: MagicNumber
    this.serviceGeneratedTimestamps = new ArrayList<>();
    this.serviceGeneratedTimestamps.add(new Timestamp("1", 1_556_302_800, 300));
    this.serviceGeneratedTimestamps.add(new Timestamp("2", 1_556_302_810, 400));
    this.serviceGeneratedTimestamps.add(new Timestamp("3", 1_556_302_820, 500));

    this.userUploadedTimestamps = new ArrayList<>();
    this.userUploadedTimestamps.add(new Timestamp("4", 1_556_302_860, 600));
    this.userUploadedTimestamps.add(new Timestamp("5", 1_556_302_870, 700));
    this.userUploadedTimestamps.add(new Timestamp("6", 1_556_302_880, 800));
    // CHECKSTYLE.ON: MagicNumber

    when(this.landscapeRepo.getAllTimestamps()).thenReturn(this.serviceGeneratedTimestamps);
    when(this.replayRepo.getAllTimestamps()).thenReturn(this.userUploadedTimestamps);

    this.timestampResource = new TimestampResource(this.landscapeRepo, this.replayRepo);

  }

  @Test
  @DisplayName("No params (default param values) should return all service generated timestamps")
  public void giveAllServiceGenerated() {
    assertEquals(this.serviceGeneratedTimestamps,
        this.timestampResource.getTimestamps(0L, 0, false),
        "No params returned wrong value for timestamp resource.");
  }

  @Test
  @DisplayName("ReturnUploadedTimestamps = true should return all user uploaded timestamps.")
  public void giveAllUserUploadedOnlyFlag() {
    assertEquals(this.userUploadedTimestamps, this.timestampResource.getTimestamps(0L, 0, true),
        "ReturnUploadedTimestamps flag = true returned wrong value for timestamp resource.");
  }

  @Test
  @DisplayName("ReturnUploadedTimestamps = true has highest priority for return value.")
  public void giveAllUserUploadedAllParams() {
    assertEquals(this.userUploadedTimestamps, this.timestampResource.getTimestamps(5L, 42, true), // NOCS
        "ReturnUploadedTimestamps flag = true does not have highest priority.");
  }

  // TODO afterTimestamp and interval
}
