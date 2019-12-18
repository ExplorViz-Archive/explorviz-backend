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
import net.explorviz.settings.model.Setting;
import net.explorviz.settings.model.UserPreference;
import net.explorviz.shared.common.idgen.IdGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import xyz.morphia.Datastore;
import xyz.morphia.Key;
import xyz.morphia.query.Query;

/**
 * Unit test for {@link UserPreferenceRepository}.
 *
 *
 */
@ExtendWith(MockitoExtension.class)
public class UserPreferenceRepositoryTest {

  @Mock
  private Datastore ds;

  @Mock
  private IdGenerator idgen;

  private UserPreferenceRepository uss;

  private List<UserPreference> userSettings;

  /**
   * Setup.
   */
  @BeforeEach
  public void setUp() {
    assert this.ds != null;
    this.uss = new UserPreferenceRepository(this.ds, this.idgen);
    this.userSettings =
        new ArrayList<>(Arrays.asList(new UserPreference("id1", "1", "bid", Boolean.TRUE),
            new UserPreference("id2", "1", "sid", "val"),
            new UserPreference("id3", "2", "did", 0.4)));
  }


  @Test
  public void testGetAll() {
    final Query<UserPreference> s = mock(Query.class);
    when(this.ds.find(UserPreference.class)).thenReturn(s);
    when(s.asList()).thenReturn(this.userSettings);
    final List<UserPreference> retrieved = this.uss.findAll();
    assertEquals(this.userSettings, retrieved);
  }


  @Test
  public void testFindById() {
    when(this.ds.get(UserPreference.class, this.userSettings.get(0).getId()))
        .thenReturn(this.userSettings.get(0));

    final UserPreference retrieved = this.uss.find(this.userSettings.get(0).getId()).get();

    assertEquals(this.userSettings.get(0), retrieved);
  }

  @Test
  public void testRemove() {
    final Query<UserPreference> q = mock(Query.class);
    when(this.ds.find(UserPreference.class)).thenReturn(q);
    when(q.filter(ArgumentMatchers.anyString(), ArgumentMatchers.any())).thenReturn(q);
    when(this.ds.delete(q)).then(new Answer<WriteResult>() {
      @Override
      public WriteResult answer(final InvocationOnMock invocation) throws Throwable {
        UserPreferenceRepositoryTest.this.userSettings.remove(0);
        return new WriteResult(1, false, null);
      }
    });

    this.uss.delete(this.userSettings.get(0).getId());
    assertNull(this.uss.find(this.userSettings.get(0).getId()).orElse(null));
  }

  @Test
  public void testCreate() {
    final UserPreference uset = new UserPreference(null, "1", "test", false);
    when(this.ds.save(uset)).then(new Answer<Key<Setting>>() {

      @Override
      public Key<Setting> answer(final InvocationOnMock invocation) throws Throwable {
        UserPreferenceRepositoryTest.this.userSettings.add(uset);
        return null;
      }

    });

    final Query<UserPreference> upQuery = mock(Query.class);
    when(upQuery.filter(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
        .thenReturn(upQuery);
    when(this.ds.find(UserPreference.class)).thenReturn(upQuery);
    when(upQuery.count()).thenReturn(0L);
    when(this.idgen.generateId()).thenReturn("testid");
    this.uss.createOrUpdate(uset);

    assertNotNull(this.uss.find(uset.getId()));
  }

  @Test
  public void testCrateDuplicate() {
    final UserPreference uset = new UserPreference(null, "1", "test", false);
    final UserPreference uset2 = new UserPreference(null, "1", "test", true);
    when(this.ds.save(uset)).then(new Answer<Key<Setting>>() {

      @Override
      public Key<Setting> answer(final InvocationOnMock invocation) throws Throwable {
        UserPreferenceRepositoryTest.this.userSettings.add(uset);
        return null;
      }

    });
    when(this.idgen.generateId()).thenReturn("testid");

    final Query<UserPreference> upQuery = mock(Query.class);
    when(this.ds.find(UserPreference.class)).thenReturn(upQuery);



    when(upQuery.filter(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
        .thenReturn(upQuery);



    Mockito.doAnswer(new Answer<Long>() {

      @Override
      public Long answer(final InvocationOnMock invocation) throws Throwable {
        final Long f = UserPreferenceRepositoryTest.this.userSettings.stream()
            .filter(u -> u.getUserId().contentEquals("1"))
            .filter(u -> u.getSettingId().contentEquals("test"))
            .count();

        return f;
      }
    }).when(upQuery).count();

    // when(upQuery.count()).thenReturn(2L);

    this.uss.createOrUpdate(uset);

    assertThrows(IllegalStateException.class, () -> this.uss.createOrUpdate(uset2));

  }


}
