package net.explorviz.settings.server.main;

import net.explorviz.settings.model.UserPreference;
import net.explorviz.settings.model.FlagSetting;
import net.explorviz.settings.model.RangeSetting;
import net.explorviz.settings.model.Setting;
import net.explorviz.settings.server.providers.SettingJsonApiDeserializer;
import net.explorviz.settings.server.providers.UserSettingJsonApiDeserializer;
import net.explorviz.settings.server.resources.CustomSettingsResource;
import net.explorviz.settings.server.resources.SettingsInfoResource;
import net.explorviz.shared.common.jsonapi.ResourceConverterFactory;
import net.explorviz.shared.common.provider.GenericTypeFinder;
import net.explorviz.shared.common.provider.JsonApiListProvider;
import net.explorviz.shared.common.provider.JsonApiProvider;
import net.explorviz.shared.exceptions.mapper.GeneralExceptionMapper;
import net.explorviz.shared.exceptions.mapper.WebApplicationExceptionMapper;
import org.glassfish.jersey.server.ResourceConfig;

public class Application extends ResourceConfig {

  public Application() {

    GenericTypeFinder.getTypeMap().put("Setting", Setting.class);
    GenericTypeFinder.getTypeMap().put("RangeSetting", RangeSetting.class);
    GenericTypeFinder.getTypeMap().put("FlagSetting", FlagSetting.class);
    GenericTypeFinder.getTypeMap().put("CustomSetting", UserPreference.class);



    // register CDI
    this.register(new DependencyInjectionBinder());


    // exception handling (mind the order !)
    this.register(WebApplicationExceptionMapper.class);
    this.register(GeneralExceptionMapper.class);


    // JSON API Serializing
    this.register(SettingJsonApiDeserializer.class);
    this.register(UserSettingJsonApiDeserializer.class);
    this.register(JsonApiProvider.class);
    this.register(JsonApiListProvider.class);
    this.register(ResourceConverterFactory.class);



    this.register(SetupApplicationListener.class);



    this.register(SettingsInfoResource.class);
    this.register(CustomSettingsResource.class);

  }

}