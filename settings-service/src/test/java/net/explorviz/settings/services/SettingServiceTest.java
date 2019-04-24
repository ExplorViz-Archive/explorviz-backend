package net.explorviz.settings.services;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

@ExtendWith(MockitoExtension.class)
public class SettingServiceTest {

  @Mock private UserSettingsRepository uss;
  @Mock private SettingsRepository sps;
  
  private List<UserSetting> userSettings;
  private List<Setting> settings;
  
  private UserSettingsService settingService;
  
  @BeforeEach
  public void setUp() {

    userSettings = new ArrayList<>(Arrays.asList(
          new UserSetting("1", "bid", Boolean.TRUE),
          new UserSetting("1", "sid", "val"),
          new UserSetting("2", "bid", Boolean.TRUE)
        ));

    settings = new ArrayList<>(Arrays.asList(
        new BooleanSetting("bid", "Boolean Setting", "Boolean Setting Description", false),
        new StringSetting("sid", "Boolean Setting", "Boolean Setting Description", "def"),
        new DoubleSetting("did", "Double Setting", "Boolean Setting Description", 0.5, -1, 1)        
        ));
    settingService = new UserSettingsService(sps, uss);
  }

  
  
  @Test
  public void testGetForUser() {
    String id = "1";
    when(sps.findAll()).thenReturn(settings);
    when(uss.findAll()).thenReturn(userSettings);
    when(sps.find(ArgumentMatchers.anyString())).thenAnswer(new Answer<Optional<Setting>>() {

      @Override
      public Optional<Setting> answer(InvocationOnMock invocation) throws Throwable {
        String sid = invocation.getArgument(0);
        return settings.stream().filter(s -> s.getId().equals(sid)).findAny();
      }
    });
    Map<String, Object> s = settingService.getForUser("1");
    assertEquals(s.get("bid"), Boolean.TRUE);
    assertEquals(s.get("did"), 0.5);
  }
  
  @Test
  public void testSetForUser() throws IllegalArgumentException, UnknownSettingException {
    String id = "1";
    when(sps.find(ArgumentMatchers.anyString())).thenAnswer( i -> {
      String sid = i.getArgument(0);
      return settings.stream().filter(s -> s.getId().equals(sid)).findAny();
    });
    Mockito.doAnswer(new Answer<Void>() {
      @Override
      public Void answer(InvocationOnMock invocation) throws Throwable {
        UserSetting u = invocation.getArgument(0);
        userSettings.removeIf(us -> us.getId().equals(u.getId()));
        userSettings.add(u);
        
        return null;
      }}).when(uss).create(ArgumentMatchers.any());
    when(sps.findAll()).thenReturn(settings); 
    when(uss.findAll()).thenReturn(userSettings);
    
    settingService.setForUser(id, "did", 0.2);
    double retr = (double) settingService.getForUser(id).get("did");
    assertEquals(0.2, retr);
  }
  
  @Test
  public void testSetUnkownSetting() {
    String id = "1";
    when(sps.find(ArgumentMatchers.anyString())).thenAnswer( i -> {
      String sid = i.getArgument(0);
      return settings.stream().filter(s -> s.getId().equals(sid)).findAny();
    });

    
    assertThrows(IllegalArgumentException.class, () -> settingService.setForUser(id, "unknown", 0.2));
  }
  
  
  @Test
  public void testSetUnkownUser() {
    String id = "8";
    when(sps.find(ArgumentMatchers.anyString())).thenAnswer( i -> {
      String sid = i.getArgument(0);
      return settings.stream().filter(s -> s.getId().equals(sid)).findAny();
    });

    
    assertThrows(IllegalArgumentException.class, () -> settingService.setForUser(id, "unknown", 0.2));
  }
  
  @Test
  public void testOrphanRemoval() {
    String id = "1";
    when(sps.findAll()).thenReturn(settings);
    when(sps.find(ArgumentMatchers.anyString())).thenAnswer(new Answer<Optional<Setting>>() {

      @Override
      public Optional<Setting> answer(InvocationOnMock invocation) throws Throwable {
        String sid = invocation.getArgument(0);
        return settings.stream().filter(s -> s.getId().equals(sid)).findAny();
      }
    });
    when(uss.findAll()).thenReturn(userSettings);
    
    settings.remove(0);
    
    Map<String, Object> s = settingService.getForUser("1");
    assertFalse(s.containsKey("bid"));
  }
  
}
