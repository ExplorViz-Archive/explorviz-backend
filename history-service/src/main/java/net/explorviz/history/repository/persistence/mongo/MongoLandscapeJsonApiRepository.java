package net.explorviz.history.repository.persistence.mongo;

import com.github.jasminb.jsonapi.exceptions.DocumentSerializationException;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.core.Response;
import net.explorviz.history.repository.persistence.LandscapeRepository;
import net.explorviz.landscape.model.landscape.Landscape;
import net.explorviz.landscape.model.store.Timestamp;
import net.explorviz.shared.config.annotations.Config;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Stores and retrieves landscapes from a mongodb, which is given in the
 * {@code explorviz.properties} resource.
 *
 * <p>
 *
 * This repository will return all requested landscape objects in the json api format, which is the
 * format the objects are persisted in internally. Prefer this class over
 * {@link MongoLandscapeRepository} if you don't need an actually landscape object to avoid costy
 * de-/serialization.
 *
 * </p>
 *
 */
public class MongoLandscapeJsonApiRepository implements LandscapeRepository<String> {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(MongoLandscapeJsonApiRepository.class);

  private final MongoHelper mongoHelper;

  private final LandscapeSerializationHelper serializationHelper;

  @Config("repository.history.intervalInMinutes")
  private int intervalInMinutes;

  @Inject
  public MongoLandscapeJsonApiRepository(final MongoHelper mongoHelper,
      final LandscapeSerializationHelper helper) {
    this.mongoHelper = mongoHelper;
    this.serializationHelper = helper;
  }

  @Override
  public void save(final Long timestamp, final Landscape landscape, final int totalRequests) {

    String landscapeJsonApi;
    try {
      landscapeJsonApi = this.serializationHelper.serialize(landscape);
    } catch (final DocumentSerializationException e) {
      throw new InternalServerErrorException("Error serializing: " + e.getMessage(), e);
    }

    final MongoCollection<Document> landscapeCollection = this.mongoHelper.getLandscapeCollection();

    final Document landscapeDocument = new Document();
    landscapeDocument.append(MongoHelper.FIELD_ID, landscape.getId());
    landscapeDocument.append(MongoHelper.FIELD_TIMESTAMP, timestamp);
    landscapeDocument.append(MongoHelper.FIELD_LANDSCAPE, landscapeJsonApi);
    landscapeDocument.append(MongoHelper.FIELD_REQUESTS, totalRequests);

    try {
      landscapeCollection.insertOne(landscapeDocument);
    } catch (final MongoException e) {
      if (LOGGER.isErrorEnabled()) {
        LOGGER.error("No document saved.");
        return;
      }
    }
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(String.format("Saved landscape {timestamp: %d, id: %s, totalRequests: %d}",
          timestamp,
          landscape.getId(),
          totalRequests));
    }
  }

  @Override
  public Optional<String> getByTimestamp(final long timestamp) {
    final MongoCollection<Document> landscapeCollection = this.mongoHelper.getLandscapeCollection();

    final Document landscapeDocument = new Document();
    landscapeDocument.append(MongoHelper.FIELD_TIMESTAMP, timestamp);

    final FindIterable<Document> result = landscapeCollection.find(landscapeDocument);

    if (result.first() == null) {
      return Optional.empty();
    } else {
      return Optional.of((String) result.first().get(MongoHelper.FIELD_LANDSCAPE));
    }
  }

  @Override
  public Optional<String> getByTimestamp(final Timestamp timestamp) {
    return this.getByTimestamp(timestamp.getTimestamp());
  }

  @Override
  public Optional<String> getById(final String id) {
    final MongoCollection<Document> landscapeCollection = this.mongoHelper.getLandscapeCollection();

    final Document landscapeDocument = new Document();
    landscapeDocument.append(MongoHelper.FIELD_ID, id);

    final FindIterable<Document> result = landscapeCollection.find(landscapeDocument);

    if (result.first() == null) {
      return Optional.empty();
    } else {
      return Optional.of((String) result.first().get(MongoHelper.FIELD_LANDSCAPE));
    }
  }

  @Override
  public void cleanup(final long from) {
    final long enddate = from - TimeUnit.MINUTES.toMillis(this.intervalInMinutes);

    final MongoCollection<Document> landscapeCollection = this.mongoHelper.getLandscapeCollection();
    final MongoCollection<Document> replayCollection = this.mongoHelper.getReplayCollection();

    final Document landscapeDocument = new Document();
    landscapeDocument.append(MongoHelper.FIELD_TIMESTAMP, new BasicDBObject("$lt", enddate));

    final DeleteResult landsapeResult = landscapeCollection.deleteMany(landscapeDocument);
    final DeleteResult replayResult = replayCollection.deleteMany(landscapeDocument);

    // TODO: Replays
    if (LOGGER.isInfoEnabled()) {
      LOGGER.info(String.format("Cleaned %d landscape and %d replay objects",
          landsapeResult.getDeletedCount(),
          replayResult.getDeletedCount()));
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
  public int getTotalRequests(final long timestamp) {
    final MongoCollection<Document> landscapeCollection = this.mongoHelper.getLandscapeCollection();

    final Document landscapeDocument = new Document();
    landscapeDocument.append(MongoHelper.FIELD_TIMESTAMP, timestamp);

    final FindIterable<Document> result = landscapeCollection.find(landscapeDocument);

    if (result.first() == null) {
      throw new ClientErrorException("Landscape not found for provided timestamp " + timestamp,
          Response.Status.NOT_FOUND);
    } else {
      return (int) result.first().get(MongoHelper.FIELD_REQUESTS);
    }
  }



}
