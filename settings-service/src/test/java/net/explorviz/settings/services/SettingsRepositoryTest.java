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
import net.explorviz.settings.model.BooleanSetting;
import net.explorviz.settings.model.DoubleSetting;
import net.explorviz.settings.model.Setting;
import net.explorviz.settings.model.StringSetting;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import xyz.morphia.Datastore;
import xyz.morphia.Key;
import xyz.morphia.query.Query;

@ExtendWith(MockitoExtension.class)
public class SettingsRepositoryTest {

  @Mock
  private Datastore ds;

  private SettingsRepository sps;

  private List<Setting> settings;

  @BeforeEach
  public void setUp() {
    assert ds != null;
    sps = new SettingsRepository(ds);
    settings = new ArrayList<>(Arrays.asList(
        new BooleanSetting("bid", "Boolean Setting", "Boolean Setting Description", false, "test"),
        new StringSetting("sid", "Boolean Setting", "Boolean Setting Description", "def", "test"),
        new DoubleSetting("did", "Double Setting", "Boolean Setting Description", 0.5, "test", -1,
            1)));
  }


  @Test
  public void testGetAll() {
    Query<Setting> q = mock(Query.class);
    when(ds.find(Setting.class)).thenReturn(q);
    when(q.asList()).thenReturn(settings);

    List<Setting> retrievedSettings = sps.findAll();

    assertEquals(this.settings, retrievedSettings);
  }


  @Test
  public void testFindById() {
    when(ds.get(Setting.class, "bid")).thenReturn(settings.get(0));

    Setting retrieved = sps.find("bid").get();

    assertEquals(settings.get(0), retrieved);
  }

  @Test
  public void testRemove() {
    when(ds.delete(Setting.class, settings.get(0).getId())).then(new Answer<WriteResult>() {
      @Override
      public WriteResult answer(InvocationOnMock invocation) throws Throwable {
        settings.remove(0);
        return new WriteResult(1, false, null);
      }
    });

    sps.delete(settings.get(0).getId());
    assertNull(sps.find("bid").orElse(null));
  }

  @Test
  public void testCreate() {
    Setting<String> s = new StringSetting("test", "testname", "a test setting", "default", "test");
    when(ds.save(s)).then(new Answer<Key<Setting<String>>>() {

      @Override
      public Key<Setting<String>> answer(InvocationOnMock invocation) throws Throwable {
        settings.add(s);
        return null;
      }

    });
    sps.create(s);

    assertNotNull(sps.find(s.getId()));
  }



}
