package net.explorviz.settings.services;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import com.mongodb.Block;
import com.mongodb.CursorType;
import com.mongodb.Function;
import com.mongodb.ServerAddress;
import com.mongodb.ServerCursor;
import com.mongodb.WriteResult;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Collation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import net.explorviz.settings.model.BooleanSetting;
import net.explorviz.settings.model.DoubleSetting;
import net.explorviz.settings.model.Setting;
import net.explorviz.settings.model.StringSetting;
import net.explorviz.settings.model.UserSetting;
import net.explorviz.settings.services.mongo.MongoHelper;
import org.bson.Document;
import org.bson.conversions.Bson;
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
  
  @Mock private MongoHelper mongohelper;
  
  private UserSettingsRepository uss;
  
  private List<UserSetting> userSettings;
  
  @BeforeEach
  public void setUp() {
    assert ds != null;
    uss = new UserSettingsRepository(mongohelper, ds);  
    userSettings = new ArrayList<>(Arrays.asList(
          new UserSetting("1", "bid", Boolean.TRUE),
          new UserSetting("1", "sid", "val"),
          new UserSetting("1", "did", 0.4)
        ));
  }
  
  
  @Test
  public void testGetAll() {
    String id = "1";
       
    MongoCollection<Document> collection = mock(MongoCollection.class);
    when(mongohelper.getUserSettingsCollection()).thenReturn(collection);
    
    List<UserSetting> returnList = userSettings.stream().filter(u -> u.getId().getUserId().equals(id)).collect(Collectors.toList());
    when(collection.withCodecRegistry(ArgumentMatchers.any())).thenReturn(collection);
    when(collection.find(UserSetting.class)).thenReturn(new FindIterable<UserSetting>() {
      
      @Override
      public <U> MongoIterable<U> map(Function<UserSetting, U> mapper) {
        // TODO Auto-generated method stub
        return null;
      }
      
      @Override
      public MongoCursor<UserSetting> iterator() {
        Iterator<UserSetting> it = returnList.iterator();
        return new MongoCursor<UserSetting>() {

          @Override
          public void close() {
                       
          }

          @Override
          public boolean hasNext() {
            return it.hasNext();
          }

          @Override
          public UserSetting next() {
            return it.next();
          }

          @Override
          public UserSetting tryNext() {
            return it.hasNext()?it.next():null;
          }

          @Override
          public ServerCursor getServerCursor() {
            // TODO Auto-generated method stub
            return null;
          }

          @Override
          public ServerAddress getServerAddress() {
            // TODO Auto-generated method stub
            return null;
          }};
      }
      
      @Override
      public <A extends Collection<? super UserSetting>> A into(A target) {
        // TODO Auto-generated method stub
        return null;
      }
      
      @Override
      public void forEach(Block<? super UserSetting> block) {
        // TODO Auto-generated method stub
        
      }
      
      @Override
      public UserSetting first() {
        // TODO Auto-generated method stub
        return null;
      }
      
      @Override
      public FindIterable<UserSetting> sort(Bson sort) {
        // TODO Auto-generated method stub
        return null;
      }
      
      @Override
      public FindIterable<UserSetting> snapshot(boolean snapshot) {
        // TODO Auto-generated method stub
        return null;
      }
      
      @Override
      public FindIterable<UserSetting> skip(int skip) {
        // TODO Auto-generated method stub
        return null;
      }
      
      @Override
      public FindIterable<UserSetting> showRecordId(boolean showRecordId) {
        // TODO Auto-generated method stub
        return null;
      }
      
      @Override
      public FindIterable<UserSetting> returnKey(boolean returnKey) {
        // TODO Auto-generated method stub
        return null;
      }
      
      @Override
      public FindIterable<UserSetting> projection(Bson projection) {
        // TODO Auto-generated method stub
        return null;
      }
      
      @Override
      public FindIterable<UserSetting> partial(boolean partial) {
        // TODO Auto-generated method stub
        return null;
      }
      
      @Override
      public FindIterable<UserSetting> oplogReplay(boolean oplogReplay) {
        // TODO Auto-generated method stub
        return null;
      }
      
      @Override
      public FindIterable<UserSetting> noCursorTimeout(boolean noCursorTimeout) {
        // TODO Auto-generated method stub
        return null;
      }
      
      @Override
      public FindIterable<UserSetting> modifiers(Bson modifiers) {
        // TODO Auto-generated method stub
        return null;
      }
      
      @Override
      public FindIterable<UserSetting> min(Bson min) {
        // TODO Auto-generated method stub
        return null;
      }
      
      @Override
      public FindIterable<UserSetting> maxTime(long maxTime, TimeUnit timeUnit) {
        // TODO Auto-generated method stub
        return null;
      }
      
      @Override
      public FindIterable<UserSetting> maxScan(long maxScan) {
        // TODO Auto-generated method stub
        return null;
      }
      
      @Override
      public FindIterable<UserSetting> maxAwaitTime(long maxAwaitTime, TimeUnit timeUnit) {
        // TODO Auto-generated method stub
        return null;
      }
      
      @Override
      public FindIterable<UserSetting> max(Bson max) {
        // TODO Auto-generated method stub
        return null;
      }
      
      @Override
      public FindIterable<UserSetting> limit(int limit) {
        // TODO Auto-generated method stub
        return null;
      }
      
      @Override
      public FindIterable<UserSetting> hint(Bson hint) {
        // TODO Auto-generated method stub
        return null;
      }
      
      @Override
      public FindIterable<UserSetting> filter(Bson filter) {
        // TODO Auto-generated method stub
        return null;
      }
      
      @Override
      public FindIterable<UserSetting> cursorType(CursorType cursorType) {
        // TODO Auto-generated method stub
        return null;
      }
      
      @Override
      public FindIterable<UserSetting> comment(String comment) {
        // TODO Auto-generated method stub
        return null;
      }
      
      @Override
      public FindIterable<UserSetting> collation(Collation collation) {
        // TODO Auto-generated method stub
        return null;
      }
      
      @Override
      public FindIterable<UserSetting> batchSize(int batchSize) {
        // TODO Auto-generated method stub
        return null;
      }
    });
    
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
