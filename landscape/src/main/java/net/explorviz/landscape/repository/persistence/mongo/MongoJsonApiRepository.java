package net.explorviz.landscape.repository.persistence.mongo;

import com.github.jasminb.jsonapi.exceptions.DocumentSerializationException;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import javax.inject.Inject;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.InternalServerErrorException;
import net.explorviz.landscape.model.landscape.Landscape;
import net.explorviz.landscape.repository.persistence.LandscapeRepository;
import net.explorviz.landscape.server.main.Configuration;
import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Stores and retrieves landscapes from a mongodb, which is given in the
 * {@code explorviz.properties} resource.
 *
 * <p>
 *
 * This repository will return all requested landscape objects in the json api format, which is the
 * format the objects are persisted in internally. Prefer this class over {@link MongoRepository} if
 * you don't need an actually landscape object to avoid costy de-/serialization.
 *
 * </p>
 *
 */
public class MongoJsonApiRepository implements LandscapeRepository<String> {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(MongoJsonApiRepository.class.getSimpleName());



  private final MongoHelper mongoHelper;


  private final LandscapeSerializationHelper serializationHelper;


  @Inject
  public MongoJsonApiRepository(final MongoHelper mongoHelper,
      final LandscapeSerializationHelper helper) {
    this.mongoHelper = mongoHelper;
    this.serializationHelper = helper;
  }



  @Override
  public void saveLandscape(final long timestamp, final Landscape landscape) {

    String landscapeJsonApi;
    try {
      landscapeJsonApi = this.serializationHelper.serialize(landscape);
    } catch (final DocumentSerializationException e) {
      throw new InternalServerErrorException("Error serializing: " + e.getMessage(), e);
    }

    final DBCollection landscapeCollection = this.mongoHelper.getLandscapeCollection();

    final DBObject landscapeDbo = new BasicDBObject();
    landscapeDbo.put(MongoHelper.ID_FIELD, timestamp);
    landscapeDbo.put(MongoHelper.LANDSCAPE_FIELD, landscapeJsonApi);

    final WriteResult res = landscapeCollection.save(landscapeDbo);

    if (res.getN() == 0) {
      if (LOGGER.isErrorEnabled()) {
        LOGGER.error("No document saved.");
      }
    } else if (LOGGER.isInfoEnabled()) {
      LOGGER.info(
          String.format("Saved landscape {timestamp: %d, id: %d}", timestamp, landscape.getId()));
    }
  }

  @Override
  public String getLandscapeByTimestamp(final long timestamp) {
    final DBCollection landscapeCollection = this.mongoHelper.getLandscapeCollection();
    final DBObject query = new BasicDBObject(MongoHelper.ID_FIELD, timestamp);
    final DBCursor result = landscapeCollection.find(query);
    if (result.count() > 0) {
      return (String) result.one().get(MongoHelper.LANDSCAPE_FIELD);
    } else {
      throw new ClientErrorException("Landscape not found for provided timestamp " + timestamp,
          404);
    }
  }

  @Override
  public String getLandscapeById(final long id) {
    final String regexQuery = "\\{\"data\":\\{\"type\":\"landscape\",\"id\":\"" + id;


    final Pattern pat = Pattern.compile(regexQuery, Pattern.CASE_INSENSITIVE);

    final DBCollection landscapeCollection = this.mongoHelper.getLandscapeCollection();
    final DBObject query = new BasicDBObject(MongoHelper.LANDSCAPE_FIELD, pat);
    final DBCursor result = landscapeCollection.find(query);
    if (result.count() != 0) {
      return (String) result.one().get(MongoHelper.LANDSCAPE_FIELD);
    } else {
      throw new ClientErrorException(String.format("Landscape with provided id %d not found", id),
          404);
    }
  }



  @Override
  public Map<Long, Long> getAllForTimeshift() {
    throw new NotImplementedException("Not implemented");
  }

  @Override
  public void cleanup(final long from) {
    final long enddate =
        from - TimeUnit.MINUTES.toMillis(Configuration.HISTORY_INTERVAL_IN_MINUTES);

    final DBCollection landscapeCollection = this.mongoHelper.getLandscapeCollection();
    final DBCollection replayCollection = this.mongoHelper.getReplayCollection();
    final DBObject query =
        new BasicDBObject(MongoHelper.ID_FIELD, new BasicDBObject("$lt", enddate));
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
  public void saveReplay(final long timestamp, final Landscape replay) {
    String landscapeJsonApi;
    try {
      landscapeJsonApi = this.serializationHelper.serialize(replay);
    } catch (final DocumentSerializationException e) {
      throw new InternalServerErrorException("Error serializing: " + e.getMessage(), e);
    }

    final DBCollection landscapeCollection = this.mongoHelper.getReplayCollection();

    final DBObject landscapeDbo = new BasicDBObject();
    landscapeDbo.put("_id", timestamp);
    landscapeDbo.put(MongoHelper.LANDSCAPE_FIELD, landscapeJsonApi);

    landscapeCollection.save(landscapeDbo);
  }



  @Override
  public String getReplayByTimestamp(final long timestamp) {
    final DBCollection landscapeCollection = this.mongoHelper.getReplayCollection();
    final DBObject query = new BasicDBObject(MongoHelper.ID_FIELD, timestamp);
    final DBCursor result = landscapeCollection.find(query);
    if (result.count() > 0) {
      return (String) result.one().get(MongoHelper.LANDSCAPE_FIELD);
    } else {
      throw new ClientErrorException("Landscape not found for provided timestamp " + timestamp,
          404);
    }
  }



  @Override
  public String getReplayById(final long id) {
    final String regexQuery = "\\{\"data\":\\{\"type\":\"landscape\",\"id\":\"" + id + "\"";

    final Pattern pat = Pattern.compile(regexQuery, Pattern.CASE_INSENSITIVE);

    final DBCollection landscapeCollection = this.mongoHelper.getReplayCollection();
    final DBObject query = new BasicDBObject(MongoHelper.LANDSCAPE_FIELD, pat);
    final DBCursor result = landscapeCollection.find(query);
    if (result.count() != 0) {
      return (String) result.one().get(MongoHelper.LANDSCAPE_FIELD);
    } else {
      throw new ClientErrorException("Landscape not found for provided id " + id, 404);
    }
  }



}
