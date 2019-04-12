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
    when(ds.get(UserSetting.class, userSettings.get(0).getId())).thenReturn(userSettings.get(0));
    
    UserSetting retrieved = uss.findById("1", "bid").get();
    
    assertEquals(settings.get(0), retrieved);
  }
  
  @Test
  public void testRemove() {
    when(ds.delete(UserSetting.class, userSettings.get(0).getId())).then(new Answer<WriteResult>() {
      @Override
      public WriteResult answer(InvocationOnMock invocation) throws Throwable {
         userSettings.remove(0);
         return new WriteResult(1,false, null);
      }});
    
    uss.delete(userSettings.get(0).getId().getUserId(), userSettings.get(0).getId().getSettingId());
    assertNull(uss.findById(userSettings.get(0).getId().getUserId(), userSettings.get(0).getId().getSettingId()).orElse(null));
  }
  
  @Test
  public void testCreate() {
    UserSetting<Boolean> uset = new UserSetting<Boolean>("1", "test", false);
    when(ds.save(uset)).then(new Answer<Key<Setting<String>>>() {

      @Override
      public Key<Setting<String>> answer(InvocationOnMock invocation) throws Throwable {
        userSettings.add(uset);
        return null;
      }

    });
    
    uss.save(uset);
    
    assertNotNull(uss.findById("1", "test"));
  }


}
