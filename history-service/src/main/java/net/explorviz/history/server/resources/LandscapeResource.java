package net.explorviz.history.server.resources;

import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import net.explorviz.history.repository.persistence.LandscapeRepository;
import net.explorviz.history.repository.persistence.ReplayRepository;
import net.explorviz.shared.landscape.model.landscape.Landscape;


/**
 * Resource providing persisted {@link Landscape} data for the frontend.
 */
@Path("v1/landscapes")
@RolesAllowed({"admin"})
public class LandscapeResource {

  private static final String MEDIA_TYPE = "application/vnd.api+json";
  private static final long QUERY_PARAM_DEFAULT_VALUE_LONG = 0L;

  private final LandscapeRepository<String> landscapeStringRepo;
  private final ReplayRepository<String> replayStringRepo;


  @Inject
  public LandscapeResource(final LandscapeRepository<String> landscapeStringRepo,
      final ReplayRepository<String> replayStringRepo) {
    this.landscapeStringRepo = landscapeStringRepo;
    this.replayStringRepo = replayStringRepo;
  }

  // akr: IMHO best option for decision between 404 or 200 Empty
  // https://stackoverflow.com/a/48746789

  /**
   * Returns a landscape by its id or 404.
   *
   * @param id - entity id of the landscape
   * @return landscape object found by passed id or 404.
   */
  @GET
  @Path("{id}")
  @Produces(MEDIA_TYPE)
  public String getLandscapeById(@PathParam("id") final String id) {

    // this can be done better since Java 9

    // Check existence in landscapeRepo and replayRepo or throw Exception
    return Stream.of(this.landscapeStringRepo.getById(id), this.replayStringRepo.getById(id))
        .filter(Optional::isPresent).map(Optional::get).findFirst()
        .orElseThrow(() -> new NotFoundException("Landscape with id " + id + " not found.")); // NOCS
  }

  /**
   * Returns {@link net.explorviz.shared.landscape.model.landscape.Landscape} with the passed query
   * parameter.
   *
   * @param timestamp - query parameter
   * @return the requested timestamp
   */
  @GET
  @Produces(MEDIA_TYPE)
  public String getLandscape(@QueryParam("timestamp") final long timestamp) {

    System.out.println(timestamp);

    if (timestamp == QUERY_PARAM_DEFAULT_VALUE_LONG) {
      throw new BadRequestException("Query parameter 'timestamp' is mandatory");
    }

    // Check existence in landscapeRepo and replayRepo or throw Exception
    return Stream
        .of(this.landscapeStringRepo.getByTimestamp(timestamp),
            this.replayStringRepo.getByTimestamp(timestamp))
        .filter(Optional::isPresent).map(Optional::get).findFirst().orElseThrow(
            () -> new NotFoundException("Landscape with timestamp " + timestamp + " not found."));
  }

}
