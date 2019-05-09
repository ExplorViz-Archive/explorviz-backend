package net.explorviz.settings.server.resources;

import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import net.explorviz.settings.model.Setting;
import net.explorviz.settings.services.SettingsRepository;

/**
 * API for handling {@link Setting}s and their associated information.
 *
 */
@Path("v1/settings/info")
public class SettingsInfoResource {

  private static final String MEDIA_TYPE = "application/vnd.api+json";


  private final SettingsRepository repo;

  @Inject
  public SettingsInfoResource(final SettingsRepository repo) {
    this.repo = repo;
  }

  /**
   * Endpoint to access all settings.
   *
   * @return all settings currently available.
   */
  @GET
  @Produces(MEDIA_TYPE)
  public List<Setting> getAll() {
    return this.repo.findAll();
  }

  /**
   * Endpoint to access a single setting.
   *
   * @param id the id of the setting
   * @return the setting with HTTP 200 (OK)
   * @throws NotFoundException if there is no setting with the given id
   */
  @GET
  @Produces(MEDIA_TYPE)
  @Path("/{id}")
  public Setting getById(@PathParam("id") final String id) {
    return this.repo.find(id).orElseThrow(NotFoundException::new);
  }

  /**
   * Endpoint to delete a single setting.
   *
   * @param id the id of the setting
   * @return returns a HTTP Status 201 (No Content).
   */
  @DELETE
  @Produces(MEDIA_TYPE)
  @Path("/{id}")
  public Response deleteById(@PathParam("id") final String id) {
    this.repo.delete(id);
    return Response.noContent().build();
  }

  /**
   * Endpoint for creation of new settings.
   *
   * @param s a setting more specific a subtype of {@link Setting}. @return Returns HTTP 200 (OK) on
   *        success
   * @throws BadRequestException if the setting is malformed and could not be saved
   */
  @POST
  @Consumes(MEDIA_TYPE)
  public Response createSetting(final Setting s) {
    try {
      this.repo.create(s);
      return Response.ok().build();
    } catch (final Exception e) {
      throw new BadRequestException();
    }

  }


}
