package net.explorviz.settings.services;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import com.mongodb.WriteResult;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.ws.rs.core.MultivaluedHashMap;
import net.explorviz.settings.model.FlagSetting;
import net.explorviz.settings.model.RangeSetting;
import net.explorviz.settings.model.Setting;
import net.explorviz.shared.common.idgen.IdGenerator;
import net.explorviz.shared.querying.QueryResult;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.mockito.stubbing.Answer;
import xyz.morphia.Datastore;
import xyz.morphia.Key;
import xyz.morphia.query.Query;

/**
 * Unit tests for {@link SettingsRepository}.
 *
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class SettingsRepositoryTest {

  @Mock
  private Datastore ds;

  @Mock
  private IdGenerator idgen;


  private SettingsRepository sps;

  private List<RangeSetting> rangeSettings;
  private List<FlagSetting> flagSettings;

  private List<Setting> union;

  /**
   * Setup.
   */
  @BeforeEach
  public void setUp() {
    assert this.ds != null;
    this.sps = new SettingsRepository(this.ds, this.idgen);

    // Add some default values
    this.rangeSettings = new ArrayList<>(Arrays.asList(new RangeSetting("r", "Double Setting",
        "Range Setting Description", "testoriging", 0.5, -1, 1)));
    this.flagSettings = new ArrayList<>(Arrays.asList(new FlagSetting("b",
        "Boolean Setting", "Boolean Setting Description", "flag_origin", false)));

    this.union = new ArrayList<>();
    this.union.addAll(this.rangeSettings);
    this.union.addAll(this.flagSettings);
  }


  @Test
  public void testGetAll() {

    final Query<RangeSetting> qrange = mock(Query.class);
    final Query<FlagSetting> qflag = mock(Query.class);
    when(this.ds.find(RangeSetting.class)).thenReturn(qrange);
    when(this.ds.find(FlagSetting.class)).thenReturn(qflag);
    when(qrange.asList()).thenReturn(this.rangeSettings);
    when(qflag.asList()).thenReturn(this.flagSettings);

    final List<Setting> retrievedSettings = this.sps.findAll();

    assertEquals(this.union, retrievedSettings);
  }


  @Test
  public void testFindById() {
    when(this.ds.get(FlagSetting.class, "b")).thenReturn(this.flagSettings.get(0));
    when(this.ds.get(FlagSetting.class, CoreMatchers.not(CoreMatchers.equalTo("b"))))
        .thenReturn(null);
    when(this.ds.get(RangeSetting.class, "r")).thenReturn(this.rangeSettings.get(0));
    when(this.ds.get(RangeSetting.class, CoreMatchers.not(CoreMatchers.equalTo("r"))))
        .thenReturn(null);

    final Setting retrieved = this.sps.find("b").get();

    assertEquals(this.flagSettings.get(0), retrieved);
  }

  @Test
  public void testRemove() {
    when(this.ds.delete(RangeSetting.class, this.flagSettings.get(0).getId())).then(i -> {
      return new WriteResult(0, false, null);
    });
    when(this.ds.delete(FlagSetting.class, this.flagSettings.get(0).getId())).then(i -> {
      SettingsRepositoryTest.this.flagSettings.remove(0);
      return new WriteResult(1, false, null);
    });

    this.sps.delete(this.flagSettings.get(0).getId());
    assertNull(this.sps.find("bid").orElse(null));
  }

  @Test
  public void testCreate() {
    final FlagSetting s = new FlagSetting("testname", "a test setting", "default", false);

    when(this.idgen.generateId()).thenReturn("generatedId");

    when(this.ds.save(s)).then(new Answer<Key<FlagSetting>>() {
      @Override
      public Key<FlagSetting> answer(final InvocationOnMock invocation) throws Throwable {
        SettingsRepositoryTest.this.flagSettings.add(s);
        return null;
      }

    });
    this.sps.createOrUpdate(s);

    assertNotNull(this.sps.find(s.getId()));
  }


  @Test
  public void testExisting() {
    final FlagSetting s = new FlagSetting("id", "testname", "a test setting", "default", false);

    assertThrows(IllegalArgumentException.class, () -> this.sps.createOrUpdate(s));

  }

  @Test
  public void filterByType() {
    final MultivaluedHashMap<String, String> params = new MultivaluedHashMap<>();
    params.putSingle("filter[type]", "flagsetting");

    final Query<FlagSetting> q = mock(Query.class);

    when(this.ds.createQuery(FlagSetting.class)).thenReturn(q);
    when(q.asList()).thenReturn(this.flagSettings);
    final QueryResult<Setting> res =
        this.sps.query(net.explorviz.shared.querying.Query.fromParameterMap(params));

    assertEquals(this.flagSettings, res.getData());
  }

  @Test
  public void testPagination() {
    this.flagSettings.add(new FlagSetting("Name", "Desc", "Some", false));
    final MultivaluedHashMap<String, String> params = new MultivaluedHashMap<>();
    params.putSingle("page[number]", "1");
    params.putSingle("page[size]", "1");

    final net.explorviz.shared.querying.Query<Setting> q =
        net.explorviz.shared.querying.Query.fromParameterMap(params);

    final Query<FlagSetting> qFlag = mock(Query.class);
    final Query<RangeSetting> qRange = mock(Query.class);
    when(this.ds.createQuery(FlagSetting.class)).thenReturn(qFlag);
    when(this.ds.createQuery(RangeSetting.class)).thenReturn(qRange);
    when(qFlag.asList()).thenReturn(this.flagSettings);
    when(qRange.asList()).thenReturn(this.rangeSettings);

    final QueryResult<Setting> res = this.sps.query(q);

    assertEquals(1, res.getData().size());
    assertEquals(0, res.getPreviousPage());
    assertEquals(2, res.getNextPage());

  }


}
