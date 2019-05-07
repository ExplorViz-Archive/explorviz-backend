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
import net.explorviz.settings.model.CustomSetting;
import net.explorviz.settings.model.Setting;
import net.explorviz.settings.services.mongo.MongoHelper;
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
public class CustomSettingsRepositoryTest {

  @Mock
  private Datastore ds;

  @Mock
  private MongoHelper mongohelper;

  private CustomSettingsRepository uss;

  private List<CustomSetting> userSettings;

  @BeforeEach
  public void setUp() {
    assert this.ds != null;
    this.uss = new CustomSettingsRepository(this.mongohelper, this.ds);
    this.userSettings = new ArrayList<>(Arrays.asList(new CustomSetting("1", "bid", Boolean.TRUE),
        new CustomSetting("1", "sid", "val"), new CustomSetting("1", "did", 0.4)));
  }


  @Test
  public void testGetAll() {
    final String id = "1";



    final List<CustomSetting> retrieved = this.uss.findAll();
    assertEquals(this.userSettings, retrieved);
  }


  @Test
  public void testFindById() {
    when(this.ds.get(CustomSetting.class, this.userSettings.get(0).getId()))
        .thenReturn(this.userSettings.get(0));

    final CustomSetting retrieved = this.uss.find(this.userSettings.get(0).getId()).get();

    assertEquals(this.userSettings.get(0), retrieved);
  }

  @Test
  public void testRemove() {
    final Query q = mock(Query.class);
    when(this.ds.find(CustomSetting.class)).thenReturn(q);
    when(q.filter(ArgumentMatchers.anyString(), ArgumentMatchers.any())).thenReturn(q);
    when(this.ds.delete(q)).then(new Answer<WriteResult>() {
      @Override
      public WriteResult answer(final InvocationOnMock invocation) throws Throwable {
        CustomSettingsRepositoryTest.this.userSettings.remove(0);
        return new WriteResult(1, false, null);
      }
    });

    this.uss.delete(this.userSettings.get(0).getId());
    assertNull(this.uss.find(this.userSettings.get(0).getId()).orElse(null));
  }

  @Test
  public void testCreate() {
    final CustomSetting uset = new CustomSetting("1", "test", false);
    when(this.ds.save(uset)).then(new Answer<Key<Setting>>() {

      @Override
      public Key<Setting> answer(final InvocationOnMock invocation) throws Throwable {
        CustomSettingsRepositoryTest.this.userSettings.add(uset);
        return null;
      }

    });

    this.uss.create(uset);

    assertNotNull(this.uss.find(uset.getId()));
  }


}
