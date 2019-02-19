package net.explorviz.security.server.main;

import net.explorviz.security.server.filter.AuthenticationFilter;
import net.explorviz.security.server.providers.UserJsonApiDeserializer;
import net.explorviz.security.server.resources.RoleResource;
import net.explorviz.security.server.resources.TokenResource;
import net.explorviz.security.server.resources.UserResource;
import net.explorviz.security.server.resources.UserSettingsResource;
import net.explorviz.shared.common.jsonapi.ResourceConverterFactory;
import net.explorviz.shared.common.provider.GenericTypeFinder;
import net.explorviz.shared.common.provider.JsonApiListProvider;
import net.explorviz.shared.common.provider.JsonApiProvider;
import net.explorviz.shared.exceptions.mapper.GeneralExceptionMapper;
import net.explorviz.shared.exceptions.mapper.WebApplicationExceptionMapper;
import net.explorviz.shared.security.filters.AuthorizationFilter;
import net.explorviz.shared.security.filters.CorsResponseFilter;
import net.explorviz.shared.security.model.User;
import net.explorviz.shared.security.model.roles.Role;
import net.explorviz.shared.security.model.settings.BooleanSettingDescriptor;
import net.explorviz.shared.security.model.settings.NumericSettingDescriptor;
import net.explorviz.shared.security.model.settings.StringSettingDescriptor;
import net.explorviz.shared.security.model.settings.UserSettings;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * JAX-RS application. This class is responsible for registering all types of classes, e.g.,
 * resource classes or exception mappers
 */
public class Application extends ResourceConfig {

  /**
   * JAX-RS application. This class is responsible for registering all types of classes, e.g.,
   * resource classes or exception mappers
   */
  public Application() { // NOPMD

    GenericTypeFinder.getTypeMap().put("User", User.class);
    GenericTypeFinder.getTypeMap().put("Role", Role.class);
    GenericTypeFinder.getTypeMap().put("UserSettings", UserSettings.class);
    GenericTypeFinder.getTypeMap().put("BooleanSettingDescriptor", BooleanSettingDescriptor.class);
    GenericTypeFinder.getTypeMap().put("NumericSettingDescriptor", NumericSettingDescriptor.class);
    GenericTypeFinder.getTypeMap().put("StringSettingDescriptor", StringSettingDescriptor.class);

    // register CDI
    this.register(new DependencyInjectionBinder());

    this.register(AuthenticationFilter.class);
    this.register(CorsResponseFilter.class);

    this.register(net.explorviz.shared.security.filters.AuthenticationFilter.class);
    this.register(AuthorizationFilter.class);

    // exception handling (mind the order !)
    this.register(WebApplicationExceptionMapper.class);
    this.register(GeneralExceptionMapper.class);

    this.register(SetupApplicationListener.class);

    this.register(UserJsonApiDeserializer.class);
    this.register(JsonApiProvider.class);
    this.register(JsonApiListProvider.class);
    this.register(ResourceConverterFactory.class);

    // register all resources
    this.register(TokenResource.class);
    this.register(UserResource.class);
    this.register(RoleResource.class);
    this.register(UserSettingsResource.class);
  }



}
