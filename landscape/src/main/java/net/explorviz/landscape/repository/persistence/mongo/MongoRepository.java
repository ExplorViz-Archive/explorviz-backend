package net.explorviz.landscape.repository.persistence.mongo;

import com.github.jasminb.jsonapi.exceptions.DocumentSerializationException;
import javax.inject.Inject;
import javax.ws.rs.InternalServerErrorException;
import net.explorviz.landscape.model.landscape.Landscape;
import net.explorviz.landscape.repository.persistence.LandscapeRepository;

/**
 * Stores and retrieves landscapes from a mongodb, which is given in the
 * {@code explorviz.properties} resource.
 *
 * <p>
 *
 * This repository will return all requested landscapes as actual java objects. Since landscapes are
 * stored in json api format, this requires costy deserialization. If you don't need the actual
 * object but rather just their strin representation, use {@link MongoJsonApiRepository}
 *
 * </p>
 *
 * <p>
 *
 * This class is just a decorator for {@link MongoJsonApiRepository} which deserializes the
 * retrieved objects.
 *
 * </p>
 */
public class MongoRepository implements LandscapeRepository<Landscape> {


  @Inject
  private MongoJsonApiRepository repo;

  @Inject
  private LandscapeSerializationHelper serializationHelper;


  @Override
  public void saveLandscape(final long timestamp, final Landscape landscape,
      final long totalRequests) {
    this.repo.saveLandscape(timestamp, landscape, totalRequests);
  }

  @Override
  public Landscape getLandscapeByTimestamp(final long timestamp) {
    final String jsonLandscape = this.repo.getLandscapeByTimestamp(timestamp);
    try {
      return this.serializationHelper.deserialize(jsonLandscape);
    } catch (final DocumentSerializationException e) {
      throw new InternalServerErrorException("Error serializing: " + e.getMessage(), e); // NOPMD
    }
  }

  @Override
  public Landscape getLandscapeById(final long id) {
    final String jsonLandscape = this.repo.getLandscapeById(id);
    try {
      return this.serializationHelper.deserialize(jsonLandscape);
    } catch (final DocumentSerializationException e) {
      throw new InternalServerErrorException("Error serializing: " + e.getMessage(), e);
    }

  }



  @Override
  public void saveReplay(final long timestamp, final Landscape replay, final long totalRequests) {
    this.repo.saveReplay(timestamp, replay, totalRequests);
  }

  @Override
  public Landscape getReplayByTimestamp(final long timestamp) {
    final String jsonLandscape = this.repo.getReplayByTimestamp(timestamp);
    try {
      return this.serializationHelper.deserialize(jsonLandscape);
    } catch (final DocumentSerializationException e) {
      throw new InternalServerErrorException("Error serializing: " + e.getMessage(), e);
    }
  }

  @Override
  public Landscape getReplayById(final long id) {
    final String jsonLandscape = this.repo.getReplayById(id);
    try {
      return this.serializationHelper.deserialize(jsonLandscape);
    } catch (final DocumentSerializationException e) {
      throw new InternalServerErrorException("Error serializing: " + e.getMessage(), e);
    }
  }


  @Override
  public void cleanup(final long from) {
    this.repo.cleanup();

  }

  @Override
  public void clear() {
    this.repo.clear();
  }

  @Override
  public long getLandscapeTotalRequests(final long timestamp) {
    return this.repo.getLandscapeTotalRequests(timestamp);
  }

  @Override
  public long getReplayTotalRequests(final long timestamp) {
    return this.repo.getReplayTotalRequests(timestamp);
  }



}
