package net.explorviz.settings.server.main;

import net.explorviz.settings.model.FlagSetting;
import net.explorviz.settings.model.RangeSetting;
import net.explorviz.settings.model.Setting;
import net.explorviz.settings.model.UserPreference;
import net.explorviz.settings.server.providers.SettingJsonApiDeserializer;
import net.explorviz.settings.server.providers.UserSettingJsonApiDeserializer;
import net.explorviz.settings.server.resources.EntryPointResource;
import net.explorviz.settings.server.resources.SettingsResource;
import net.explorviz.settings.server.resources.UserPreferencesResource;
import net.explorviz.shared.common.jsonapi.ResourceConverterFactory;
import net.explorviz.shared.common.provider.GenericTypeFinder;
import net.explorviz.shared.common.provider.JsonApiListProvider;
import net.explorviz.shared.common.provider.JsonApiProvider;
import net.explorviz.shared.exceptions.mapper.GeneralExceptionMapper;
import net.explorviz.shared.exceptions.mapper.InvalidJsonApiResourceExceptionMapper;
import net.explorviz.shared.exceptions.mapper.UnregisteredTypeExceptionMapper;
import net.explorviz.shared.exceptions.mapper.WebApplicationExceptionMapper;
import net.explorviz.shared.querying.PaginationJsonApiWriter;
import net.explorviz.shared.querying.PaginationParameterFilter;
import net.explorviz.shared.security.filters.AuthenticationFilter;
import net.explorviz.shared.security.filters.AuthorizationFilter;
import net.explorviz.shared.security.filters.CorsResponseFilter;
import org.glassfish.jersey.server.ResourceConfig;

public class Application extends ResourceConfig {

  public Application() {

    GenericTypeFinder.getTypeMap().put("Setting", Setting.class);
    GenericTypeFinder.getTypeMap().put("RangeSetting", RangeSetting.class);
    GenericTypeFinder.getTypeMap().put("FlagSetting", FlagSetting.class);
    GenericTypeFinder.getTypeMap().put("UserPreference", UserPreference.class);



    // register CDI
    this.register(new DependencyInjectionBinder());

    this.register(AuthenticationFilter.class);
    this.register(CorsResponseFilter.class);


    // exception handling (mind the order !)
    this.register(WebApplicationExceptionMapper.class);
    this.register(InvalidJsonApiResourceExceptionMapper.class);
    this.register(UnregisteredTypeExceptionMapper.class);
    this.register(GeneralExceptionMapper.class);

    this.register(AuthorizationFilter.class);
    this.register(PaginationParameterFilter.class);
    this.register(PaginationJsonApiWriter.class);

    // JSON API Serializing
    this.register(SettingJsonApiDeserializer.class);
    this.register(UserSettingJsonApiDeserializer.class);
    this.register(JsonApiProvider.class);
    this.register(JsonApiListProvider.class);
    this.register(ResourceConverterFactory.class);

    this.register(SetupApplicationListener.class);

    // register all resources
    this.register(SettingsResource.class);
    this.register(UserPreferencesResource.class);
    this.register(EntryPointResource.class);

    // swagger
    this.packages("io.swagger.v3.jaxrs2.integration.resources");

  }

}
