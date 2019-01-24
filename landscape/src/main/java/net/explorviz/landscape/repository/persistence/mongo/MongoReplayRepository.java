package net.explorviz.landscape.repository.persistence.mongo;

import com.github.jasminb.jsonapi.exceptions.DocumentSerializationException;
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
  public long getReplayTotalRequests(final long timestamp) {
    return this.repo.getReplayTotalRequests(timestamp);
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
