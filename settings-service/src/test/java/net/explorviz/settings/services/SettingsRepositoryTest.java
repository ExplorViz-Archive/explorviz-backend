package net.explorviz.settings.services;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.mongodb.WriteResult;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.explorviz.settings.model.FlagSetting;
import net.explorviz.settings.model.RangeSetting;
import net.explorviz.settings.model.Setting;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.query.Query;


@ExtendWith(MockitoExtension.class)
public class SettingsRepositoryTest {

  @Mock
  private Datastore ds;

  private SettingsRepository sps;

  private List<Setting> settings;

  @BeforeEach
  public void setUp() {
    assert this.ds != null;
    this.sps = new SettingsRepository(this.ds);
    this.settings = new ArrayList<>(Arrays.asList(
        new FlagSetting("bid", "Boolean Setting", "Boolean Setting Description", "test", false),
        new RangeSetting("did", "Double Setting", "Boolean Setting Description", "test", 0.5, -1,
            1)));
  }


  @Test
  public void testGetAll() {
    final Query<Setting> q = mock(Query.class);
    when(this.ds.find(Setting.class)).thenReturn(q);
    when(q.asList()).thenReturn(this.settings);

    final List<Setting> retrievedSettings = this.sps.findAll();

    assertEquals(this.settings, retrievedSettings);
  }


  @Test
  public void testFindById() {
    when(this.ds.get(Setting.class, "bid")).thenReturn(this.settings.get(0));

    final Setting retrieved = this.sps.find("bid").get();

    assertEquals(this.settings.get(0), retrieved);
  }

  @Test
  public void testRemove() {
    when(this.ds.delete(Setting.class, this.settings.get(0).getId()))
        .then(new Answer<WriteResult>() {
          @Override
          public WriteResult answer(final InvocationOnMock invocation) throws Throwable {
            SettingsRepositoryTest.this.settings.remove(0);
            return new WriteResult(1, false, null);
          }
        });

    this.sps.delete(this.settings.get(0).getId());
    assertNull(this.sps.find("bid").orElse(null));
  }

  @Test
  public void testCreate() {
    final Setting s = new FlagSetting("test", "testname", "a test setting", "default", false);
    when(this.ds.save(s)).then(new Answer<Key<FlagSetting>>() {



      @Override
      public Key<FlagSetting> answer(final InvocationOnMock invocation) throws Throwable {
        SettingsRepositoryTest.this.settings.add(s);
        return null;
      }

    });
    this.sps.create(s);

    assertNotNull(this.sps.find(s.getId()));
  }



}
