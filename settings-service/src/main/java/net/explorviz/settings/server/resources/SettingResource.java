package net.explorviz.settings.server.resources;

import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import net.explorviz.settings.model.Setting;
import net.explorviz.settings.services.SettingsRepository;

@Path("v1/settings/info")
public class SettingResource {

  private static final String MEDIA_TYPE = "application/vnd.api+json";

  @Inject
  private SettingsRepository repo;



  @GET
  @Produces(MEDIA_TYPE)
  public List<Setting> getAll() {
    return this.repo.findAll();
  }

  @GET
  @Produces(MEDIA_TYPE)
  @Path("/{id}")
  public Setting getById(@PathParam("id") final String id) {
    return this.repo.find(id).orElseThrow(NotFoundException::new);
  }

  @POST
  @Consumes(MEDIA_TYPE)
  public Response getSetting(final Setting s) {
    this.repo.create(s);
    return Response.ok().build();
  }


}
