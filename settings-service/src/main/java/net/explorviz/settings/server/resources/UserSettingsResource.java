package net.explorviz.settings.server.resources;

import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Type;
import java.util.Map;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import net.explorviz.settings.services.SettingsRepository;
import net.explorviz.settings.services.UnknownSettingException;
import net.explorviz.settings.services.UserSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Endpoint to access and manipulate user settings
 *
 */
@Path("v1/usersettings/")
public class UserSettingsResource {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(UserSettingsResource.class.getSimpleName());
 
  private static final String MEDIA_TYPE = "application/vnd.api+json";
  private static final String ADMIN_ROLE = "admin";
  
  
  private UserSettingsService userSettingService;
  private SettingsRepository settingRepo;
  
  @Inject
  public UserSettingsResource(UserSettingsService userSettingsService) {
    this.userSettingService = userSettingsService;
  }
  
  @GET
  @Path("{uid}")
  public CustomSettings getForUser(@PathParam("uid") String userId) {
    Map<String, Object> sets = userSettingService.getForUser(userId);
    return new CustomSettings(sets, userId);
  }
  
  @PUT
  @Path("{uid}")
  @Consumes(MEDIA_TYPE)
  public Response setForUser(@PathParam("uid") String userId, SettingValue value){
   

    try {
      userSettingService.setForUser(userId, value.settingId, value.value);
    } catch (IllegalArgumentException  e) {
      if (LOGGER.isInfoEnabled()) {
        LOGGER.info("Types of setting and value don't match");
      }
      throw new BadRequestException("Types of setting and value don't match");
    } catch (UnknownSettingException e) {
      if (LOGGER.isInfoEnabled()) {
        LOGGER.info(String.format("Unknown setting: %s", value.settingId));
      }
      throw new BadRequestException(String.format("Unknown setting: %s", value.settingId));
    } 
   
    return Response.noContent().build();
  }
  
  
  @DELETE
  @Path("{uid}/{sid}")
  @Consumes(MEDIA_TYPE)
  public Response resetToDefaultForUser(@PathParam("uid") String userId, @PathParam("sid") String settingId) {
    userSettingService.setDefault(userId, settingId);
    
    return Response.noContent().build();
  }
  
  
  /**
   * Helper class that represents a single setting value.
   * Needed for json api serialization since json api converter can't handle composit ids.
   *
   */
  @Type("settingvalue")
  public static class SettingValue {
    
    @Id
    private String settingId;
    private Object value;
    
    public SettingValue() {}
    
    public boolean isBool() {
      return (value instanceof Boolean);
    }
    
    public boolean isString() {
      return (value instanceof String);
    }
    
    public boolean isDouble() {
      return (value instanceof Double);
    }
    
    public Object getValue() {
      return value;
    }
    
    public String getSettingId() {
      return settingId;
    }
    
  }
  
  /**
   * Wrapper class for custom settings. Needed as jsonapi converter can't handle maps as top level elements.
   *
   */
  @Type("customsetting")
  public static class CustomSettings {
    
    private Map<String, Object> settings;
    @Id
    private String userId;
    
    public CustomSettings(Map<String, Object> settings, String userId) {
      this.settings = settings;
      this.userId = userId;
    }
    
    public CustomSettings() {}
    
    
    public Map<String, Object> getSettings() {
      return settings;
    }
    
    public String getUserId() {
      return userId;
    }
  }
  
}
