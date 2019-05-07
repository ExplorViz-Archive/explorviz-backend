package net.explorviz.settings.server.resources;

import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import net.explorviz.settings.model.CustomSetting;
import net.explorviz.settings.services.CustomSettingsRepository;
import net.explorviz.settings.services.CustomSettingsService;
import net.explorviz.settings.services.SettingValidationException;

@Path("v1/settings/custom")
public class CustomSettingsResource {

  private static final String MEDIA_TYPE = "application/vnd.api+json";


  private final CustomSettingsRepository repo;
  private final CustomSettingsService css;


  @Inject
  public CustomSettingsResource(final CustomSettingsRepository repo,
      final CustomSettingsService css) {
    super();
    this.repo = repo;
    this.css = css;
  }

  @GET
  @Produces(MEDIA_TYPE)
  public List<CustomSetting> getAll() {
    return this.repo.findAll();
  }

  @GET
  @Produces(MEDIA_TYPE)
  @Path("/{userId}")
  public List<CustomSetting> getForUser(@PathParam("userId") final String uid) {
    return this.css.getCustomsForUser(uid);
  }

  @POST
  @Consumes(MEDIA_TYPE)
  public Response createCustomSetting(final CustomSetting s) {
    try {
      this.css.validate(s);
      this.repo.create(s);
      return Response.ok().build();
    } catch (final SettingValidationException e) {
      // TODO Auto-generated catch block
      throw new BadRequestException(e.getMessage());
    }
  }

}
