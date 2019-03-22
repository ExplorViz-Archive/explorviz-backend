package net.explorviz.landscape.repository.persistence.mongo;

import com.github.jasminb.jsonapi.exceptions.DocumentSerializationException;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
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
import org.bson.Document;
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
  public void save(final long timestamp, final Landscape replayLandscape, final int totalRequests) {
    String landscapeJsonApi;
    try {
      landscapeJsonApi = this.serializationHelper.serialize(replayLandscape);
    } catch (final DocumentSerializationException e) {
      throw new InternalServerErrorException("Error serializing: " + e.getMessage(), e);
    }

    final MongoCollection<Document> landscapeCollection = this.mongoHelper.getReplayCollection();

    final Document landscapeDocument = new Document();
    landscapeDocument.append(MongoHelper.FIELD_ID, timestamp);
    landscapeDocument.append(MongoHelper.FIELD_LANDSCAPE, landscapeJsonApi);
    landscapeDocument.append(MongoHelper.FIELD_REQUESTS, totalRequests);

    try {
      landscapeCollection.insertOne(landscapeDocument);
    } catch (final Exception e) {
      if (LOGGER.isErrorEnabled()) {
        LOGGER.error("No document saved.");
        return;
      }
    }
    if (LOGGER.isInfoEnabled()) {
      LOGGER.info(String.format("Saved landscape {timestamp: %d, id: %d, totalRequests: %d}",
          timestamp, replayLandscape.getId(), totalRequests));
    }
  }

  @Override
  public String getByTimestamp(final long timestamp) {
    final MongoCollection<Document> landscapeCollection = this.mongoHelper.getReplayCollection();

    final Document landscapeDocument = new Document();
    landscapeDocument.append(MongoHelper.FIELD_ID, timestamp);

    final FindIterable<Document> result = landscapeCollection.find(landscapeDocument);

    if (result.first() != null) {
      return (String) result.first().get(MongoHelper.FIELD_LANDSCAPE);
    } else {
      throw new ClientErrorException("Landscape not found for provided timestamp " + timestamp,
          404);
    }
  }

  @Override
  public String getById(final String id) {
    final String regexQuery = "\\{\"data\":\\{\"type\":\"landscape\",\"id\":\"" + id;

    final Pattern pat = Pattern.compile(regexQuery, Pattern.CASE_INSENSITIVE);

    final MongoCollection<Document> landscapeCollection = this.mongoHelper.getReplayCollection();

    final Document landscapeDocument = new Document();
    landscapeDocument.append(MongoHelper.FIELD_LANDSCAPE, pat);

    final FindIterable<Document> result = landscapeCollection.find(landscapeDocument);

    if (result.first() == null) {
      throw new ClientErrorException(String.format("Landscape with provided id %d not found", id),
          404);
    } else {
      return (String) result.first().get(MongoHelper.FIELD_LANDSCAPE);
    }
  }

  @Override
  public int getTotalRequests(final long timestamp) {
    final MongoCollection<Document> landscapeCollection = this.mongoHelper.getReplayCollection();

    final Document landscapeDocument = new Document();
    landscapeDocument.append(MongoHelper.FIELD_ID, timestamp);

    final FindIterable<Document> result = landscapeCollection.find(landscapeDocument);

    if (result.first() == null) {
      throw new ClientErrorException("Replay not found for provided timestamp " + timestamp, 404);
    } else {
      return (int) result.first().get(MongoHelper.FIELD_REQUESTS);

    }
  }

  @Override
  public void cleanup(final long from) {
    final long enddate =
        from - TimeUnit.MINUTES.toMillis(Configuration.HISTORY_INTERVAL_IN_MINUTES);

    final MongoCollection<Document> landscapeCollection = this.mongoHelper.getLandscapeCollection();
    final MongoCollection<Document> replayCollection = this.mongoHelper.getReplayCollection();

    final Document landscapeDocument = new Document();
    landscapeDocument.append(MongoHelper.FIELD_ID, new BasicDBObject("$lt", enddate));

    final DeleteResult landsapeResult = landscapeCollection.deleteMany(landscapeDocument);
    final DeleteResult replayResult = replayCollection.deleteMany(landscapeDocument);

    // TODO: Replays
    if (LOGGER.isInfoEnabled()) {
      LOGGER.info(String.format("Cleaned %d landscape and %d replay objects",
          landsapeResult.getDeletedCount(), replayResult.getDeletedCount()));
    }
  }

  @Override
  public void clear() {
    final MongoCollection<Document> landscapeCollection = this.mongoHelper.getLandscapeCollection();
    final MongoCollection<Document> replayCollection = this.mongoHelper.getReplayCollection();
    landscapeCollection.deleteMany(new Document());
    replayCollection.deleteMany(new Document());
  }

  @Override
  public List<Long> getAllTimestamps() {
    final MongoCollection<Document> landscapeCollection = this.mongoHelper.getReplayCollection();
    final List<Long> result = new LinkedList<>();

    final FindIterable<Document> documents = landscapeCollection.find();

    for (final Document doc : documents) {
      result.add((long) doc.get(MongoHelper.FIELD_ID));
    }

    return result;
  }

}
