package net.explorviz.landscape.repository.persistence.mongo;

import com.github.jasminb.jsonapi.exceptions.DocumentSerializationException;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.InternalServerErrorException;
import net.explorviz.landscape.model.landscape.Landscape;
import net.explorviz.landscape.repository.persistence.ReplayRepository;

public class MongoReplayRepository implements ReplayRepository<Landscape> {


  @Inject
  private MongoReplayJsonApiRepository repo;

  @Inject
  private LandscapeSerializationHelper serializationHelper;

  @Override
  public void save(final long timestamp, final Landscape replay, final long totalRequests) {
    this.repo.save(timestamp, replay, totalRequests);
  }

  @Override
  public Landscape getByTimestamp(final long timestamp) {
    final String jsonLandscape = this.repo.getByTimestamp(timestamp);
    try {
      return this.serializationHelper.deserialize(jsonLandscape);
    } catch (final DocumentSerializationException e) {
      throw new InternalServerErrorException("Error serializing: " + e.getMessage(), e);
    }
  }

  @Override
  public Landscape getById(final long id) {
    final String jsonLandscape = this.repo.getById(id);
    try {
      return this.serializationHelper.deserialize(jsonLandscape);
    } catch (final DocumentSerializationException e) {
      throw new InternalServerErrorException("Error serializing: " + e.getMessage(), e);
    }
  }

  @Override
  public long getTotalRequests(final long timestamp) {
    return this.repo.getTotalRequests(timestamp);
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
  public List<Long> getAllTimestamps() {
    return this.repo.getAllTimestamps();
  }

}
