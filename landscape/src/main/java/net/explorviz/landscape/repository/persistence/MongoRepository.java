package net.explorviz.landscape.repository.persistence;

import com.github.jasminb.jsonapi.JSONAPIDocument;
import com.github.jasminb.jsonapi.ResourceConverter;
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
import net.explorviz.landscape.server.main.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongoRepository implements LandscapeRepository<String, Long> {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(MongoRepository.class.getSimpleName());
  private static final String LANDSCAPE_FIELD = "landscape";



  private final MongoHelper mongoHelper;


  private final ResourceConverter jsonApiConverter;

  @Inject
  public MongoRepository(final MongoHelper mongoHelper, final ResourceConverter converter) {
    this.mongoHelper = mongoHelper;
    this.jsonApiConverter = converter;
  }



  @Override
  public void saveLandscape(final long timestamp, final Landscape landscape) {

    final String landscapeJsonApi;
    try {
      landscapeJsonApi = this.serializeLandscape(landscape);
    } catch (final DocumentSerializationException e) {
      throw new InternalServerErrorException("Error serializing: " + e.getMessage(), e);
    }

    final DBCollection landscapeCollection = this.mongoHelper.getLandscapeCollection();

    final DBObject landscapeDbo = new BasicDBObject();
    landscapeDbo.put("_id", timestamp);
    landscapeDbo.put(LANDSCAPE_FIELD, landscapeJsonApi);

    landscapeCollection.save(landscapeDbo);
  }

  @Override
  public String getLandscapeByTimestamp(final long timestamp) {


    final DBCollection landscapeCollection = this.mongoHelper.getLandscapeCollection();
    final DBObject query = new BasicDBObject("_id", timestamp);
    final DBCursor result = landscapeCollection.find(query);
    if (result.count() == 1) {
      return (String) result.one().get(LANDSCAPE_FIELD);
    } else {
      throw new ClientErrorException("Landscape not found for provided timestamp " + timestamp,
          404);
    }
  }

  @Override
  public String getLandscapeById(final Long id) {
    final String regexQuery = "\\{\"data\":\\{\"type\":\"landscape\",\"id\":\"" + id + "\"";

    final Pattern pat = Pattern.compile(regexQuery, Pattern.CASE_INSENSITIVE);

    final DBCollection landscapeCollection = this.mongoHelper.getLandscapeCollection();
    final DBObject query = new BasicDBObject(LANDSCAPE_FIELD, pat);
    final DBCursor result = landscapeCollection.find(query);
    if (result.count() == 1) {
      return (String) result.one().get(LANDSCAPE_FIELD);
    } else {
      throw new ClientErrorException("Landscape not found for provided id " + id, 404);
    }
  }



  @Override
  public Map<Long, Long> getAllForTimeshift() {
    throw new RuntimeException("Not implemented");
  }

  @Override
  public void cleanup(final long from) {
    final long enddate =
        from - TimeUnit.MINUTES.toMillis(Configuration.HISTORY_INTERVAL_IN_MINUTES);

    final DBCollection landscapeCollection = this.mongoHelper.getLandscapeCollection();
    final DBObject query = new BasicDBObject("_id", new BasicDBObject("$lt", enddate));
    final WriteResult result = landscapeCollection.remove(query);

    if (LOGGER.isInfoEnabled()) {
      LOGGER.info(String.format("Cleaned %d old objects", result.getN()));
    }
  }

  @Override
  public void clear() {
    final DBCollection landscapeCollection = this.mongoHelper.getLandscapeCollection();
    landscapeCollection.remove(new BasicDBObject());
  }



  /**
   * Serializes a landscape to a json-api string.
   *
   * @throws DocumentSerializationException if the landscape could not be parsed.
   */
  private String serializeLandscape(final Landscape l) throws DocumentSerializationException {
    final JSONAPIDocument<Landscape> landscapeDoc = new JSONAPIDocument<>(l);
    final byte[] landscapeBytes = this.jsonApiConverter.writeDocument(landscapeDoc);
    return new String(landscapeBytes);

  }


}
