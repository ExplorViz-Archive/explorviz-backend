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
import net.explorviz.settings.model.UserSetting;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import xyz.morphia.Datastore;
import xyz.morphia.Key;
import xyz.morphia.query.Query;

public class UserSettingsServiceTest {
  
  @Mock private Datastore ds;
  
  private UserSettingsService uss;
  
  private List<Setting> settings;
  private List<UserSetting> userSettings;
  
  @BeforeEach
  public void setUp() {
    assert ds != null;
    uss = new UserSettingsService(ds);
    settings = new ArrayList<>(Arrays.asList(
          new BooleanSetting("bid", "Boolean Setting", "Boolean Setting Description", false),
          new StringSetting("sid", "Boolean Setting", "Boolean Setting Description", "def"),
          new DoubleSetting("did", "Boolean Setting", "Boolean Setting Description", 0.5, -1, 1)        
        ));
    
    userSettings = new ArrayList<>(Arrays.asList(
          new UserSetting<Boolean>("1", "bid", Boolean.TRUE),
          new UserSetting<String>("1", "sid", "val"),
          new UserSetting<Double>("1", "did", 0.4)
        ));
  }
  
  
  @Test
  public void testGetAll() {
    Query<UserSetting> q = mock(Query.class);
    when(ds.find(UserSetting.class)).thenReturn(q);
    when(q.asList()).thenReturn(userSettings);
    
    List<UserSetting> retrieved = uss.findAll("1");
    assertEquals(userSettings, retrieved);
  }
  
  
  @Test
  public void testFindById() {
    when(ds.get(UserSetting.class, "bid")).thenReturn(settings.get(0));
    
    Setting retrieved = sps.findById("bid").get();
    
    assertEquals(settings.get(0), retrieved);
  }
  
  @Test
  public void testRemove() {
    when(ds.delete(Setting.class, settings.get(0).getId())).then(new Answer<WriteResult>() {
      @Override
      public WriteResult answer(InvocationOnMock invocation) throws Throwable {
         settings.remove(0);
         return new WriteResult(1,false, null);
      }});
    
    sps.delete(settings.get(0).getId());
    assertNull(sps.findById("bid").orElse(null));
  }
  
  @Test
  public void testCreate() {
    Setting<String> s = new StringSetting("test", "testname", "a test setting", "default");
    when(ds.save(s)).then(new Answer<Key<Setting<String>>>() {

      @Override
      public Key<Setting<String>> answer(InvocationOnMock invocation) throws Throwable {
        settings.add(s);
        return null;
      }

    });
    sps.save(s);
    
    assertNotNull(sps.findById(s.getId()));
  }


}
