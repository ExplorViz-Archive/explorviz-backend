package net.explorviz.history.server.resources;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import net.explorviz.history.repository.persistence.LandscapeRepository;
import net.explorviz.history.repository.persistence.ReplayRepository;
import net.explorviz.history.repository.persistence.mongo.TimestampRepository;
import net.explorviz.history.server.resources.endpoints.TimestampResourceEndpointTest;
import net.explorviz.shared.landscape.model.landscape.Landscape;
import net.explorviz.shared.landscape.model.store.Timestamp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link TimestampResource}. All tests are performed by just calling the methods of
 * {@link TimestampResource}. See {@link TimestampResourceEndpointTest} for tests that use web
 * requests.
 */
@ExtendWith(MockitoExtension.class)
public class TimestampResourceTest {

  private TimestampResource timestampResource;

  @Mock(lenient = true)
  private LandscapeRepository<Landscape> landscapeRepo;

  @Mock(lenient = true)
  private ReplayRepository<Landscape> replayRepo;

  @Mock(lenient = true)
  private TimestampRepository timestampRepo;

  private List<Timestamp> serviceGeneratedTimestamps;
  private List<Timestamp> userUploadedTimestamps;

  @BeforeEach
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

    when(this.timestampRepo.getLandscapeTimestamps()).thenReturn(this.serviceGeneratedTimestamps);
    when(this.timestampRepo.getReplayTimestamps()).thenReturn(this.userUploadedTimestamps);

    this.timestampResource = new TimestampResource(this.landscapeRepo, this.replayRepo);

  }

  @Test
  @DisplayName("Return all service-generated timestamps.")
  public void giveAllServiceGenerated() {
    assertEquals(this.serviceGeneratedTimestamps,
        this.timestampResource.getTimestampsOld(0L, 0, false, 0),
        "No params returned wrong value for timestamp resource.");
  }

  @Test
  @DisplayName("Return all user-uploaded timestamps.")
  public void giveAllUserUploadedOnlyFlag() {
    assertEquals(this.userUploadedTimestamps,
        this.timestampResource.getTimestampsOld(0L, 0, true, 0),
        "User-uploaded flag returned wrong value for timestamp resource.");
  }

  @Test
  @DisplayName("Return first two service-generated timestamps.")
  public void giveServiceGeneratedBasedOnMaxLength() {

    final List<Timestamp> resultList = this.timestampResource.getTimestampsOld(0L, 0, false, 2);

    final List<Timestamp> expectedList = new ArrayList<>();
    expectedList.add(new Timestamp("1", 1_556_302_800L, 300)); // NOCS
    expectedList.add(new Timestamp("2", 1_556_302_810L, 400)); // NOCS

    assertEquals(expectedList, resultList, "MaxLength returned wrong value.");
  }

  @Test
  @DisplayName("Return first two user-uploaded timestamps.")
  public void giveUserUploadedOnlyFlagBasedOnMaxLength() {

    final List<Timestamp> resultList = this.timestampResource.getTimestampsOld(0L, 0, true, 2);

    final List<Timestamp> expectedList = new ArrayList<>();
    expectedList.add(new Timestamp("7", 1_556_302_860L, 600)); // NOCS
    expectedList.add(new Timestamp("8", 1_556_302_870L, 700)); // NOCS

    assertEquals(expectedList, resultList,
        "User-uploaded flag and maxLength returned wrong value.");
  }

  @Test
  @DisplayName("Return all service-generated timestamps, which come after the passed one.")
  public void giveAllServiceTimestampsAfterPassedTimestamp() {

    final List<Timestamp> resultList =
        this.timestampResource.getTimestampsOld(1_556_302_820L, 0, false, 0);

    final List<Timestamp> expectedList = new ArrayList<>();
    expectedList.add(new Timestamp("3", 1_556_302_820L, 500)); // NOCS
    expectedList.add(new Timestamp("4", 1_556_302_830L, 600)); // NOCS
    expectedList.add(new Timestamp("5", 1_556_302_840L, 700)); // NOCS
    expectedList.add(new Timestamp("6", 1_556_302_850L, 800)); // NOCS

    assertEquals(expectedList, resultList,
        "Invalid return value for filtered service-generated timestamps.");
  }

  @Test
  @DisplayName("Return all user-uploaded timestamps, which come after the passed one.")
  public void giveAllUserUploadedAllParams() {

    final List<Timestamp> resultList =
        this.timestampResource.getTimestampsOld(1_556_302_880L, 0, true, 0);

    final List<Timestamp> expectedList = new ArrayList<>();
    expectedList.add(new Timestamp("9", 1_556_302_880L, 800)); // NOCS
    expectedList.add(new Timestamp("10", 1_556_302_890L, 900)); // NOCS
    expectedList.add(new Timestamp("11", 1_556_302_900L, 1000)); // NOCS
    expectedList.add(new Timestamp("12", 1_556_302_910L, 1100)); // NOCS

    assertEquals(expectedList, resultList,
        "Invalid return value for filtered user-uploaded timestamps.");
  }

  @Test
  @DisplayName("Return concrete interval of user-uploaded timestamps.")
  public void giveConcreteIntervalOfUserUploadedTimestamps() {

    final List<Timestamp> resultList =
        this.timestampResource.getTimestampsOld(1_556_302_880L, 3, true, 0);

    final List<Timestamp> expectedList = new ArrayList<>();
    expectedList.add(new Timestamp("9", 1_556_302_880L, 800)); // NOCS
    expectedList.add(new Timestamp("10", 1_556_302_890L, 900)); // NOCS
    expectedList.add(new Timestamp("11", 1_556_302_900L, 1000)); // NOCS

    assertEquals(expectedList, resultList,
        "Invalid return value for concrete interval of user-uploaded timestamps.");
  }

  @Test
  @DisplayName("Return remaining interval of user-uploaded timestamps.")
  public void giveRemainingIntervalOfUserUploadedTimestamps() {

    final List<Timestamp> resultList =
        this.timestampResource.getTimestampsOld(1_556_302_880L, 100, true, 0);

    final List<Timestamp> expectedList = new ArrayList<>();
    expectedList.add(new Timestamp("9", 1_556_302_880L, 800)); // NOCS
    expectedList.add(new Timestamp("10", 1_556_302_890L, 900)); // NOCS
    expectedList.add(new Timestamp("11", 1_556_302_900L, 1000)); // NOCS
    expectedList.add(new Timestamp("12", 1_556_302_910L, 1100)); // NOCS


    assertEquals(expectedList, resultList,
        "Invalid return value for remaining interval of user-uploaded timestamps.");
  }

  @Test
  @DisplayName("Return concrete interval of service-generated timestamps.")
  public void giveConcreteIntervalOfServiceGeneratedTimestamps() {

    final List<Timestamp> resultList =
        this.timestampResource.getTimestampsOld(1_556_302_810L, 100, false, 0);

    final List<Timestamp> expectedList = new ArrayList<>();
    expectedList.add(new Timestamp("2", 1_556_302_810L, 400)); // NOCS
    expectedList.add(new Timestamp("3", 1_556_302_820L, 500)); // NOCS
    expectedList.add(new Timestamp("4", 1_556_302_830L, 600)); // NOCS
    expectedList.add(new Timestamp("5", 1_556_302_840L, 700)); // NOCS
    expectedList.add(new Timestamp("6", 1_556_302_850L, 800)); // NOCS


    assertEquals(expectedList, resultList,
        "Invalid return value for concrete interval of service-generated timestamps.");
  }

  @Test
  @DisplayName("Return remaining interval of service-generated timestamps.")
  public void giveRemainingIntervalOfServiceGeneratedTimestamps() {

    final List<Timestamp> resultList =
        this.timestampResource.getTimestampsOld(1_556_302_810L, 4, false, 0);

    final List<Timestamp> expectedList = new ArrayList<>();
    expectedList.add(new Timestamp("2", 1_556_302_810L, 400)); // NOCS
    expectedList.add(new Timestamp("3", 1_556_302_820L, 500)); // NOCS
    expectedList.add(new Timestamp("4", 1_556_302_830L, 600)); // NOCS
    expectedList.add(new Timestamp("5", 1_556_302_840L, 700)); // NOCS

    assertEquals(expectedList, resultList,
        "Invalid return value for remaining interval of service-generated timestamps.");
  }

  @Test
  @DisplayName("Return interval of user-uploaded timestamps starting at oldest, when no timestamp was passed.") // NOCS
  public void giveMaxLengthIntervalOfUserPloadedTimestamps() {

    final List<Timestamp> resultList = this.timestampResource.getTimestampsOld(0L, 0, true, 2);

    final List<Timestamp> expectedList = new ArrayList<>();
    expectedList.add(new Timestamp("7", 1_556_302_860L, 600)); // NOCS
    expectedList.add(new Timestamp("8", 1_556_302_870L, 700)); // NOCS

    assertEquals(expectedList, resultList,
        "Invalid return value for interval of user-uploaded timestamps.");
  }

  @Test
  @DisplayName("Return interval of service-generated timestamps starting at oldest, when no timestamp was passed.") // NOCS
  public void giveMaxLengthIntervalOfServiceGeneratedTimestamps() {

    final List<Timestamp> resultList = this.timestampResource.getTimestampsOld(0L, 0, false, 2);

    final List<Timestamp> expectedList = new ArrayList<>();
    expectedList.add(new Timestamp("1", 1_556_302_800L, 300)); // NOCS
    expectedList.add(new Timestamp("2", 1_556_302_810L, 400)); // NOCS

    assertEquals(expectedList, resultList,
        "Invalid return value for interval of service-generated timestamps.");
  }


  @Test
  @DisplayName("Unknown passed user-uploaded timestamp throws exception.")
  public void throwExceptionOnUnknownUserTimestamp() {
    assertThrows(NotFoundException.class, () -> {
      this.timestampResource.getTimestampsOld(5L, 42, true, 0); // NOCS
    });
  }

  @Test
  @DisplayName("Unknown passed service-generated timestamp throws exception.")
  public void throwExceptionOnUnknownServiceTimestamp() {
    assertThrows(NotFoundException.class, () -> {
      this.timestampResource.getTimestampsOld(5L, 42, false, 0); // NOCS
    });
  }

  @Test
  @DisplayName("Negative interval throws exception.")
  public void throwExceptionOnNegativeInterval() {
    assertThrows(BadRequestException.class, () -> {
      this.timestampResource.getTimestampsOld(1_556_302_810L, -1, false, 0); // NOCS
    });
  }

  @Test
  @DisplayName("Negative maxLength throws exception.")
  public void throwExceptionOnNegativeMaxLength() {
    assertThrows(BadRequestException.class, () -> {
      this.timestampResource.getTimestampsOld(1_556_302_810L, 0, false, -1); // NOCS
    });
  }


}
