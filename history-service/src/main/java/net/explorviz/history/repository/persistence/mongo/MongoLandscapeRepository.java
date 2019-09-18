package net.explorviz.history.repository.persistence.mongo;

import com.github.jasminb.jsonapi.exceptions.DocumentSerializationException;
import java.util.Optional;
import javax.inject.Inject;
import javax.ws.rs.InternalServerErrorException;
import net.explorviz.history.repository.persistence.LandscapeRepository;
import net.explorviz.landscape.model.landscape.Landscape;
import net.explorviz.landscape.model.store.Timestamp;

/**
 * Stores and retrieves landscapes from a mongodb, which is given in the
 * {@code explorviz.properties} resource.
 *
 * <p>
 *
 * This repository will return all requested landscapes as actual java objects. Since landscapes are
 * stored in json api format, this requires costy deserialization. If you don't need the actual
 * object but rather just their strin representation, use {@link MongoLandscapeJsonApiRepository}
 *
 * </p>
 *
 * <p>
 *
 * This class is just a decorator for {@link MongoLandscapeJsonApiRepository} which deserializes the
 * retrieved objects.
 *
 * </p>
 */
public class MongoLandscapeRepository implements LandscapeRepository<Landscape> {


  @Inject
  private MongoLandscapeJsonApiRepository repo;

  @Inject
  private LandscapeSerializationHelper serializationHelper;


  @Override
  public void save(final Long timestamp, final Landscape landscape, final int totalRequests) {
    this.repo.save(timestamp, landscape, totalRequests);
  }

  @Override
  public Optional<Landscape> getByTimestamp(final long timestamp) {
    final Optional<String> jsonLandscape = this.repo.getByTimestamp(timestamp);

    if (!jsonLandscape.isPresent()) {
      return Optional.empty();
    }

    try {
      return Optional.of(this.serializationHelper.deserialize(jsonLandscape.get()));
    } catch (final DocumentSerializationException e) {
      throw new InternalServerErrorException("Error serializing: " + e.getMessage(), e); // NOCS
    }
  }

  @Override
  public Optional<Landscape> getByTimestamp(final Timestamp timestamp) {
    return this.getByTimestamp(timestamp.getTimestamp());
  }

  @Override
  public Optional<Landscape> getById(final String id) {
    final Optional<String> jsonLandscape = this.repo.getById(id);

    if (!jsonLandscape.isPresent()) {
      return Optional.empty();
    }

    try {
      return Optional.of(this.serializationHelper.deserialize(jsonLandscape.get()));
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
  public int getTotalRequests(final long timestamp) {
    return this.repo.getTotalRequests(timestamp);
  }



}
