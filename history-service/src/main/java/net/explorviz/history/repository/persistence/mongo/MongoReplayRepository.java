package net.explorviz.history.repository.persistence.mongo;

import com.github.jasminb.jsonapi.exceptions.DocumentSerializationException;
import java.util.Optional;
import javax.inject.Inject;
import javax.ws.rs.InternalServerErrorException;
import net.explorviz.history.repository.persistence.ReplayRepository;
import net.explorviz.landscape.model.landscape.Landscape;
import net.explorviz.landscape.model.store.Timestamp;

/**
 * Stores and retrieves landscapes from a MongoDb instance used for replay landscapes. MongoDb
 * information is given in the {@code explorviz.properties} resource.
 *
 * <p>
 *
 * This repository will return all requested landscapes as actual java objects. Since landscapes are
 * stored in json api format, this requires costy deserialization. If you don't need the actual
 * object but rather just their strin representation, use {@link MongoReplayJsonApiRepository}
 *
 * </p>
 *
 */
public class MongoReplayRepository implements ReplayRepository<Landscape> {


  @Inject
  private MongoReplayJsonApiRepository repo;

  @Inject
  private LandscapeSerializationHelper serializationHelper;

  @Override
  public void save(final long timestamp, final Landscape replay, final int totalRequests) {
    this.repo.save(timestamp, replay, totalRequests);
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
  public int getTotalRequestsByTimestamp(final long timestamp) {
    return this.repo.getTotalRequestsByTimestamp(timestamp);
  }


  @Override
  public void cleanup(final long from) {
    this.repo.cleanup();

  }

  @Override
  public void clear() {
    this.repo.clear();
  }



}
