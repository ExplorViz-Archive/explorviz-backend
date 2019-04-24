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
import java.util.stream.Collectors;
import net.explorviz.settings.model.BooleanSetting;
import net.explorviz.settings.model.DoubleSetting;
import net.explorviz.settings.model.Setting;
import net.explorviz.settings.model.StringSetting;
import net.explorviz.settings.model.UserSetting;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import xyz.morphia.Datastore;
import xyz.morphia.Key;
import xyz.morphia.query.Query;

@ExtendWith(MockitoExtension.class)
public class UserSettingsRepositoryTest {
  
  @Mock private Datastore ds;
  
  private UserSettingsRepository uss;
  
  private List<UserSetting> userSettings;
  
  @BeforeEach
  public void setUp() {
    assert ds != null;
    uss = new UserSettingsRepository(ds);  
    userSettings = new ArrayList<>(Arrays.asList(
          new UserSetting("1", "bid", Boolean.TRUE),
          new UserSetting("1", "sid", "val"),
          new UserSetting("1", "did", 0.4)
        ));
  }
  
  
  @Test
  public void testGetAll() {
    String id = "1";
    Query<UserSetting> q = mock(Query.class);
    when(ds.find(UserSetting.class)).thenReturn(q);
    List<UserSetting> returnList = userSettings.stream().filter(u -> u.getId().getUserId().equals(id)).collect(Collectors.toList());
    when(q.asList()).thenReturn(returnList);
    
    List<UserSetting> retrieved = uss.findAll();
    assertEquals(userSettings, retrieved);
  }
  
  
  @Test
  public void testFindById() {
    when(ds.get(UserSetting.class, userSettings.get(0).getId())).thenReturn(userSettings.get(0));
    
    UserSetting retrieved = uss.find(userSettings.get(0).getId()).get();
    
    assertEquals(userSettings.get(0), retrieved);
  }
  
  @Test
  public void testRemove() {
    when(ds.delete(UserSetting.class, userSettings.get(0).getId())).then(new Answer<WriteResult>() {
      @Override
      public WriteResult answer(InvocationOnMock invocation) throws Throwable {
         userSettings.remove(0);
         return new WriteResult(1,false, null);
      }});
    
    uss.delete(userSettings.get(0).getId());
    assertNull(uss.find(userSettings.get(0).getId()).orElse(null));
  }
  
  @Test
  public void testCreate() {
    UserSetting uset = new UserSetting("1", "test", false);
    when(ds.save(uset)).then(new Answer<Key<Setting<String>>>() {

      @Override
      public Key<Setting<String>> answer(InvocationOnMock invocation) throws Throwable {
        userSettings.add(uset);
        return null;
      }

    });
    
    uss.create(uset);
    
    assertNotNull(uss.find(uset.getId()));
  }


}
