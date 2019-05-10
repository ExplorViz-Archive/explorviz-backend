package net.explorviz.history.server.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
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
  // private static final long QUERY_PARAM_DEFAULT_VALUE_LONG = 0L;

  private final LandscapeRepository<Landscape> landscapeRepo;
  private final LandscapeRepository<String> landscapeStringRepo;

  private final ReplayRepository<String> replayStringRepo;
  private final ReplayRepository<Landscape> replayRepo;


  @Inject
  public LandscapeResource(final LandscapeRepository<String> landscapeStringRepo,
      final ReplayRepository<String> replayStringRepo,
      final LandscapeRepository<Landscape> landscapeRepo,
      final ReplayRepository<Landscape> replayRepo) {
    this.landscapeStringRepo = landscapeStringRepo;
    this.replayStringRepo = replayStringRepo;
    this.landscapeRepo = landscapeRepo;
    this.replayRepo = replayRepo;
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
        .orElseThrow(() -> new NotFoundException("Landscape with id " + id + " not found."));
  }

  /**
   * Returns a list of either user-uploaded (any POSTed landscape) or service-generated (from
   * landscape-service) {@link net.explorviz.shared.landscape.model.landscape.Landscape}.
   *
   * @param timestamp - query parameter
   * @return a list of timestamps with the passed query parameter
   */
  @GET
  @Produces(MEDIA_TYPE)
  public List<Landscape> getLandscapeByTimestamp(@QueryParam("timestamp") final long timestamp) {
    final List<Landscape> returnList = new ArrayList<>();

    Stream
        .of(this.landscapeRepo.getByTimestamp(timestamp), this.replayRepo.getByTimestamp(timestamp))
        .filter(Optional::isPresent).map(Optional::get).findFirst()
        .ifPresent((l) -> returnList.add(l));

    return returnList;
  }


  /*
   * @GET
   *
   * @Produces(MEDIA_TYPE) public String getLandscapes(@QueryParam("startTimestamp") final long
   * startTimestamp,
   *
   * @QueryParam("intervalSize") final int intervalSize,
   *
   * @QueryParam("returnUploadedLandscapes") final boolean returnUploadedLandscapes,
   *
   * @QueryParam("maxLength") final int maxLength) {
   *
   * if (maxLength < 0) { throw new BadRequestException("MaxLength must not be negative."); }
   *
   * if (intervalSize < 0) { throw new BadRequestException("Interval size must not be negative."); }
   *
   * final Landscape potentialLandscape = Stream
   * .of(this.landscapeRepo.getByTimestamp(startTimestamp),
   * this.replayRepo.getByTimestamp(startTimestamp))
   * .filter(Optional::isPresent).map(Optional::get).findFirst() .orElseThrow(() -> new
   * NotFoundException( "Landscape with timestamp " + startTimestamp + " not found."));
   *
   *
   *
   * // TODO get Collection of landscapes from Repository. // Maybe save partial JSON-API String of
   * a landscape also in MongoDB, e.g., "FIELD_CONTENT" // At the time of writing, we do not really
   * have a use case for this list. // But we should implement this feature to be future-proof.
   *
   * // TODO test this resource as shown in TimestampResource tests
   *
   * // Code from TimestampResource:
   *
   * /* List<Timestamp> timestamps = this.landscapeRepo.get();
   *
   * if (returnUploadedLandscapes) { timestamps = this.replayRepo.getAllTimestamps(); }
   *
   * final List<Timestamp> tempResultList = this.getTimestampInterval(timestamps, startTimestamp,
   * intervalSize);
   *
   * if (intervalSize == 0 && maxLength > 0 && maxLength < tempResultList.size()) { return
   * tempResultList.subList(0, maxLength); } else { return tempResultList; }
   */

}
