package net.explorviz.history.server.resources;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.UriInfo;
import net.explorviz.history.repository.persistence.mongo.TimestampRepository;
import net.explorviz.history.server.resources.endpoints.TimestampResourceEndpointTest;
import net.explorviz.landscape.model.store.Timestamp;
import net.explorviz.shared.querying.QueryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
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
  private TimestampRepository timestampRepo;

  private List<Timestamp> serviceGeneratedTimestamps;
  private List<Timestamp> userUploadedTimestamps;

  @BeforeEach
  public void setUp() throws QueryException {

    // CHECKSTYLE.OFF: MagicNumber
    this.serviceGeneratedTimestamps = new ArrayList<>();
    this.serviceGeneratedTimestamps.add(new Timestamp("1", 1556302800L, 300));
    this.serviceGeneratedTimestamps.add(new Timestamp("2", 1556302810L, 400));
    this.serviceGeneratedTimestamps.add(new Timestamp("3", 1556302820L, 500));
    this.serviceGeneratedTimestamps.add(new Timestamp("4", 1556302830L, 600));
    this.serviceGeneratedTimestamps.add(new Timestamp("5", 1556302840L, 700));
    this.serviceGeneratedTimestamps.add(new Timestamp("6", 1556302850L, 800));

    this.userUploadedTimestamps = new ArrayList<>();
    this.userUploadedTimestamps.add(new Timestamp("7", 1556302860L, 600));
    this.userUploadedTimestamps.add(new Timestamp("8", 1556302870L, 700));
    this.userUploadedTimestamps.add(new Timestamp("9", 1556302880L, 800));
    this.userUploadedTimestamps.add(new Timestamp("10", 1556302890L, 900));
    this.userUploadedTimestamps.add(new Timestamp("11", 1556302900L, 1000));
    this.userUploadedTimestamps.add(new Timestamp("12", 1556302910L, 1100));
    // CHECKSTYLE.ON: MagicNumber

    when(this.timestampRepo.getLandscapeTimestamps()).thenReturn(this.serviceGeneratedTimestamps);
    when(this.timestampRepo.getReplayTimestamps()).thenReturn(this.userUploadedTimestamps);
    when(this.timestampRepo.query(ArgumentMatchers.any())).thenCallRealMethod();
    this.timestampResource = new TimestampResource(this.timestampRepo);

  }

  @Test
  @DisplayName("Return all service-generated timestamps.")
  public void giveAllServiceGenerated() {
    final MultivaluedHashMap<String, String> params = new MultivaluedHashMap<>();
    params.add("filter[type]", "landscape");
    final UriInfo ui = Mockito.mock(UriInfo.class);
    when(ui.getQueryParameters(true)).thenReturn(params);
    assertEquals(this.reverse(this.serviceGeneratedTimestamps),
        this.timestampResource.getTimestamps(ui).getData(),
        "No params returned wrong value for timestamp resource.");
  }

  @Test
  @DisplayName("Return all user-uploaded timestamps.")
  public void giveAllUserUploadedOnlyFlag() {
    final MultivaluedHashMap<String, String> params = new MultivaluedHashMap<>();
    params.add("filter[type]", "replay");
    final UriInfo ui = Mockito.mock(UriInfo.class);
    when(ui.getQueryParameters(true)).thenReturn(params);
    assertEquals(this.reverse(this.userUploadedTimestamps),
        this.timestampResource.getTimestamps(ui).getData(),
        "User-uploaded flag returned wrong value for timestamp resource.");
  }

  @Test
  @DisplayName("Return first two service-generated timestamps.")
  public void giveServiceGeneratedBasedOnMaxLength() {
    final MultivaluedHashMap<String, String> params = new MultivaluedHashMap<>();
    params.add("page[number]", "2");
    params.add("page[size]", "2");
    params.add("filter[type]", "landscape");
    final UriInfo ui = Mockito.mock(UriInfo.class);
    when(ui.getQueryParameters(true)).thenReturn(params);
    final List<Timestamp> resultList =
        (List<Timestamp>) this.timestampResource.getTimestamps(ui).getData();

    final List<Timestamp> expectedList = new ArrayList<>();
    expectedList.add(new Timestamp("1", 1556302800L, 300)); // NOCS
    expectedList.add(new Timestamp("2", 1556302810L, 400)); // NOCS

    assertEquals(this.reverse(expectedList), resultList, "MaxLength returned wrong value.");
  }

  @Test
  @DisplayName("Return first two user-uploaded timestamps.")
  public void giveUserUploadedOnlyFlagBasedOnMaxLength() {

    final MultivaluedHashMap<String, String> params = new MultivaluedHashMap<>();
    params.add("page[number]", "2");
    params.add("page[size]", "2");
    params.add("filter[type]", "replay");
    final UriInfo ui = Mockito.mock(UriInfo.class);
    when(ui.getQueryParameters(true)).thenReturn(params);
    final List<Timestamp> resultList =
        (List<Timestamp>) this.timestampResource.getTimestamps(ui).getData();

    final List<Timestamp> expectedList = new ArrayList<>();
    expectedList.add(new Timestamp("7", 1556302860L, 600)); // NOCS
    expectedList.add(new Timestamp("8", 1556302870L, 700)); // NOCS

    assertEquals(this.reverse(expectedList),
        resultList,
        "User-uploaded flag and maxLength returned wrong value.");
  }

  @Test
  @DisplayName("Return all service-generated timestamps, which come after the passed one.")
  public void giveAllServiceTimestampsAfterPassedTimestamp() {

    final MultivaluedHashMap<String, String> params = new MultivaluedHashMap<>();
    params.add("filter[from]", "1556302820");
    params.add("filter[type]", "landscape");
    final UriInfo ui = Mockito.mock(UriInfo.class);
    when(ui.getQueryParameters(true)).thenReturn(params);

    final List<Timestamp> resultList =
        (List<Timestamp>) this.timestampResource.getTimestamps(ui).getData();

    final List<Timestamp> expectedList = new ArrayList<>();
    expectedList.add(new Timestamp("3", 1556302820L, 500)); // NOCS
    expectedList.add(new Timestamp("4", 1556302830L, 600)); // NOCS
    expectedList.add(new Timestamp("5", 1556302840L, 700)); // NOCS
    expectedList.add(new Timestamp("6", 1556302850L, 800)); // NOCS

    assertEquals(this.reverse(expectedList),
        resultList,
        "Invalid return value for filtered service-generated timestamps.");
  }

  @Test
  @DisplayName("Return all user-uploaded timestamps, which come after the passed one.")
  public void giveAllUserUploadedAllParams() {


    final MultivaluedHashMap<String, String> params = new MultivaluedHashMap<>();
    params.add("filter[from]", "1556302880");
    params.add("filter[type]", "replay");
    final UriInfo ui = Mockito.mock(UriInfo.class);
    when(ui.getQueryParameters(true)).thenReturn(params);

    final List<Timestamp> resultList =
        (List<Timestamp>) this.timestampResource.getTimestamps(ui).getData();

    final List<Timestamp> expectedList = new ArrayList<>();
    expectedList.add(new Timestamp("9", 1556302880L, 800)); // NOCS
    expectedList.add(new Timestamp("10", 1556302890L, 900)); // NOCS
    expectedList.add(new Timestamp("11", 1556302900L, 1000)); // NOCS
    expectedList.add(new Timestamp("12", 1556302910L, 1100)); // NOCS

    assertEquals(this.reverse(expectedList),
        resultList,
        "Invalid return value for filtered user-uploaded timestamps.");
  }

  @Test
  @DisplayName("Return concrete interval of user-uploaded timestamps.")
  public void giveConcreteIntervalOfUserUploadedTimestamps() {

    final MultivaluedHashMap<String, String> params = new MultivaluedHashMap<>();
    params.add("filter[from]", "1556302880");
    params.add("filter[to]", "1556302900");
    params.add("filter[type]", "replay");
    params.add("page[size]", "3");
    params.add("page[number]", "0");
    final UriInfo ui = Mockito.mock(UriInfo.class);
    when(ui.getQueryParameters(true)).thenReturn(params);

    final List<Timestamp> resultList =
        (List<Timestamp>) this.timestampResource.getTimestamps(ui).getData();

    final List<Timestamp> expectedList = new ArrayList<>();
    expectedList.add(new Timestamp("9", 1556302880L, 800)); // NOCS
    expectedList.add(new Timestamp("10", 1556302890L, 900)); // NOCS
    expectedList.add(new Timestamp("11", 1556302900L, 1000)); // NOCS

    assertEquals(this.reverse(expectedList),
        resultList,
        "Invalid return value for concrete interval of user-uploaded timestamps.");
  }

  @Test
  @DisplayName("Return remaining interval of user-uploaded timestamps.")
  public void giveRemainingIntervalOfUserUploadedTimestamps() {

    final MultivaluedHashMap<String, String> params = new MultivaluedHashMap<>();
    params.add("filter[from]", "1556302880");
    params.add("filter[type]", "replay");

    final UriInfo ui = Mockito.mock(UriInfo.class);
    when(ui.getQueryParameters(true)).thenReturn(params);

    final List<Timestamp> resultList =
        (List<Timestamp>) this.timestampResource.getTimestamps(ui).getData();

    final List<Timestamp> expectedList = new ArrayList<>();
    expectedList.add(new Timestamp("9", 1556302880L, 800)); // NOCS
    expectedList.add(new Timestamp("10", 1556302890L, 900)); // NOCS
    expectedList.add(new Timestamp("11", 1556302900L, 1000)); // NOCS
    expectedList.add(new Timestamp("12", 1556302910L, 1100)); // NOCS


    assertEquals(this.reverse(expectedList),
        resultList,
        "Invalid return value for remaining interval of user-uploaded timestamps.");
  }

  @Test
  @DisplayName("Return concrete interval of service-generated timestamps.")
  public void giveConcreteIntervalOfServiceGeneratedTimestamps() {
    final MultivaluedHashMap<String, String> params = new MultivaluedHashMap<>();
    params.add("filter[from]", "1556302810");
    params.add("filter[type]", "landscape");
    final UriInfo ui = Mockito.mock(UriInfo.class);
    when(ui.getQueryParameters(true)).thenReturn(params);


    final List<Timestamp> resultList =
        (List<Timestamp>) this.timestampResource.getTimestamps(ui).getData();

    final List<Timestamp> expectedList = new ArrayList<>();
    expectedList.add(new Timestamp("2", 1556302810L, 400)); // NOCS
    expectedList.add(new Timestamp("3", 1556302820L, 500)); // NOCS
    expectedList.add(new Timestamp("4", 1556302830L, 600)); // NOCS
    expectedList.add(new Timestamp("5", 1556302840L, 700)); // NOCS
    expectedList.add(new Timestamp("6", 1556302850L, 800)); // NOCS


    assertEquals(this.reverse(expectedList),
        resultList,
        "Invalid return value for concrete interval of service-generated timestamps.");
  }

  @Test
  @DisplayName("Return remaining interval of service-generated timestamps.")
  public void giveRemainingIntervalOfServiceGeneratedTimestamps() {

    final MultivaluedHashMap<String, String> params = new MultivaluedHashMap<>();
    params.add("filter[from]", "1556302809");
    params.add("filter[type]", "landscape");
    params.add("page[number]", "0");
    params.add("page[size]", "5");
    final UriInfo ui = Mockito.mock(UriInfo.class);
    when(ui.getQueryParameters(true)).thenReturn(params);

    final List<Timestamp> resultList =
        (List<Timestamp>) this.timestampResource.getTimestamps(ui).getData();

    final List<Timestamp> expectedList = new ArrayList<>();
    expectedList.add(new Timestamp("2", 1556302810L, 400)); // NOCS
    expectedList.add(new Timestamp("3", 1556302820L, 500)); // NOCS
    expectedList.add(new Timestamp("4", 1556302830L, 600)); // NOCS
    expectedList.add(new Timestamp("5", 1556302840L, 700)); // NOCS
    expectedList.add(new Timestamp("6", 1556302850L, 800)); // NOCS

    assertEquals(this.reverse(expectedList),
        resultList,
        "Invalid return value for remaining interval of service-generated timestamps.");
  }

  @Test
  @DisplayName("Return interval of user-uploaded timestamps starting at newest, when no timestamp was passed.") // NOCS
  public void giveMaxLengthIntervalOfUserPloadedTimestamps() {

    final MultivaluedHashMap<String, String> params = new MultivaluedHashMap<>();
    params.add("filter[type]", "replay");
    params.add("page[number]", "0");
    params.add("page[size]", "2");
    final UriInfo ui = Mockito.mock(UriInfo.class);
    when(ui.getQueryParameters(true)).thenReturn(params);

    final List<Timestamp> resultList =
        (List<Timestamp>) this.timestampResource.getTimestamps(ui).getData();

    final List<Timestamp> expectedList = new ArrayList<>();
    expectedList.add(new Timestamp("11", 1556302900L, 1000)); // NOCS
    expectedList.add(new Timestamp("12", 1556302910L, 1100)); // NOCS



    assertEquals(this.reverse(expectedList),
        resultList,
        "Invalid return value for interval of user-uploaded timestamps.");
  }

  @Test
  @DisplayName("Return interval of service-generated timestamps starting at newest, when no timestamp was passed.") // NOCS
  public void giveMaxLengthIntervalOfServiceGeneratedTimestamps() {

    final MultivaluedHashMap<String, String> params = new MultivaluedHashMap<>();
    params.add("filter[type]", "landscape");
    params.add("page[number]", "0");
    params.add("page[size]", "2");
    final UriInfo ui = Mockito.mock(UriInfo.class);
    when(ui.getQueryParameters(true)).thenReturn(params);

    final List<Timestamp> resultList =
        (List<Timestamp>) this.timestampResource.getTimestamps(ui).getData();

    final List<Timestamp> expectedList = new ArrayList<>();
    expectedList.add(new Timestamp("6", 1556302850L, 800)); // NOCS
    expectedList.add(new Timestamp("5", 1556302840L, 700)); // NOCS

    assertEquals(expectedList,
        resultList,
        "Invalid return value for interval of service-generated timestamps.");
  }

  private List<Timestamp> reverse(final List<Timestamp> original) {
    final int size = original.size();
    final Timestamp[] result = new Timestamp[size];
    for (int i = 0; i < size; i++) {
      final Timestamp elem = original.get(i);
      result[size - 1 - i] = elem;
    }
    return Arrays.asList(result);
  }


}
