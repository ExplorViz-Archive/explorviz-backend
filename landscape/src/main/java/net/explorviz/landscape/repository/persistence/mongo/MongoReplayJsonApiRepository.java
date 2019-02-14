package net.explorviz.landscape.repository.persistence.mongo;

import com.github.jasminb.jsonapi.exceptions.DocumentSerializationException;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import javax.inject.Inject;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.InternalServerErrorException;
import net.explorviz.landscape.repository.persistence.ReplayRepository;
import net.explorviz.landscape.server.main.Configuration;
import net.explorviz.shared.landscape.model.landscape.Landscape;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongoReplayJsonApiRepository implements ReplayRepository<String> {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(MongoLandscapeJsonApiRepository.class.getSimpleName());


  @Inject
  private MongoHelper mongoHelper;

  @Inject
  private LandscapeSerializationHelper serializationHelper;

  @Override
  public void save(final long timestamp, final Landscape replay, final int totalRequests) {
    String landscapeJsonApi;
    try {
      landscapeJsonApi = this.serializationHelper.serialize(replay);
    } catch (final DocumentSerializationException e) {
      throw new InternalServerErrorException("Error serializing: " + e.getMessage(), e);
    }

    final DBCollection landscapeCollection = this.mongoHelper.getReplayCollection();

    final DBObject landscapeDbo = new BasicDBObject();
    landscapeDbo.put(MongoHelper.FIELD_ID, timestamp);
    landscapeDbo.put(MongoHelper.FIELD_LANDSCAPE, landscapeJsonApi);
    landscapeDbo.put(MongoHelper.FIELD_REQUESTS, totalRequests);

    landscapeCollection.save(landscapeDbo);
  }



  @Override
  public String getByTimestamp(final long timestamp) {
    final DBCollection landscapeCollection = this.mongoHelper.getReplayCollection();
    final DBObject query = new BasicDBObject(MongoHelper.FIELD_ID, timestamp);
    final DBCursor result = landscapeCollection.find(query);
    if (result.count() > 0) {
      return (String) result.one().get(MongoHelper.FIELD_LANDSCAPE);
    } else {
      throw new ClientErrorException("Landscape not found for provided timestamp " + timestamp,
          404);
    }
  }



  @Override
  public String getById(final long id) {
    final String regexQuery = "\\{\"data\":\\{\"type\":\"landscape\",\"id\":\"" + id + "\"";

    final Pattern pat = Pattern.compile(regexQuery, Pattern.CASE_INSENSITIVE);

    final DBCollection landscapeCollection = this.mongoHelper.getReplayCollection();
    final DBObject query = new BasicDBObject(MongoHelper.FIELD_LANDSCAPE, pat);
    final DBCursor result = landscapeCollection.find(query);
    if (result.count() == 0) {
      throw new ClientErrorException("Landscape not found for provided id " + id, 404);
    } else {
      return (String) result.one().get(MongoHelper.FIELD_LANDSCAPE);
    }
  }

  @Override
  public int getTotalRequests(final long timestamp) {
    final DBCollection collection = this.mongoHelper.getReplayCollection();
    final DBObject query = new BasicDBObject(MongoHelper.FIELD_ID, timestamp);
    final DBCursor result = collection.find(query);
    if (result.count() == 0) {
      throw new ClientErrorException("Replay not found for provided timestamp " + timestamp, 404);
    } else {
      return (int) result.one().get(MongoHelper.FIELD_REQUESTS);

    }
  }

  @Override
  public void cleanup(final long from) {
    final long enddate =
        from - TimeUnit.MINUTES.toMillis(Configuration.HISTORY_INTERVAL_IN_MINUTES);

    final DBCollection landscapeCollection = this.mongoHelper.getLandscapeCollection();
    final DBCollection replayCollection = this.mongoHelper.getReplayCollection();
    final DBObject query =
        new BasicDBObject(MongoHelper.FIELD_ID, new BasicDBObject("$lt", enddate));
    final WriteResult landsapeResult = landscapeCollection.remove(query);
    final WriteResult replayResult = replayCollection.remove(query);
    // TODO: Replays

    if (LOGGER.isInfoEnabled()) {
      LOGGER.info(String.format("Cleaned %d landscape and %d replay objects", landsapeResult.getN(),
          replayResult.getN()));
    }
  }

  @Override
  public void clear() {
    final DBCollection landscapeCollection = this.mongoHelper.getLandscapeCollection();
    final DBCollection replayCollection = this.mongoHelper.getReplayCollection();
    landscapeCollection.remove(new BasicDBObject());
    replayCollection.remove(new BasicDBObject());
  }

  @Override
  public List<Long> getAllTimestamps() {
    final DBCollection landCollection = this.mongoHelper.getReplayCollection();
    final List<Long> result = new LinkedList<>();
    try (DBCursor cursor = landCollection.find()) {
      while (cursor.hasNext()) {
        result.add((long) cursor.next().get(MongoHelper.FIELD_ID));
      }
    }
    return result;
  }

}
