package net.explorviz.settings.server.resources;

import java.util.List;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import net.explorviz.settings.model.BooleanSetting;
import net.explorviz.settings.model.Setting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Endpoint to access and manipulate settings
 *
 */
@Path("v1/settings")
public class SettingsResource {

  private static final Logger LOGGER = LoggerFactory.getLogger(SettingsResource.class); // NOPMD

  private static final String MEDIA_TYPE = "application/vnd.api+json";
  private static final String ADMIN_ROLE = "admin";
  
  
  /**
   * Retrieves all settings
   * @return all settings
   */
  @Produces(MEDIA_TYPE)
  @GET
  public List<Setting> getAll() {
    return null;
  }

  /**
   * Creates a new setting
   * @param newSetting the setting to create
   * @return a Response with code 204 on success
   */
  @Produces(MEDIA_TYPE)
  @PUT
  public Response putSetting(BooleanSetting newSetting) {
    return null; 
  }
  
  /**
   * Retrieve setting with specific id.
   * @param settingsId the d of the setting
   * @return the setting
   */
  @Produces(MEDIA_TYPE)
  @GET
  @Path("{sid}")
  public Setting getById(@PathParam("sid") String settingsId) {
    return null;
  }
  
  /**
   * Deletes a single setting
   * @param settingsId the id of the setting to delete
   * @return a Response with code 204 on success
   */
  @Produces(MEDIA_TYPE)
  @DELETE
  @Path("{sid}")
  public Response deleteSetting(@PathParam("sid") String settingsId) {
    return null;
  }
  
  
  
  
  
  
  
}
