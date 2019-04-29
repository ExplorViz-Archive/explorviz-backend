package net.explorviz.settings.server.main;

import net.explorviz.settings.model.BooleanSetting;
import net.explorviz.settings.model.DoubleSetting;
import net.explorviz.settings.model.Setting;
import net.explorviz.settings.model.StringSetting;
import net.explorviz.settings.model.UserSetting;
import net.explorviz.settings.server.providers.SettingJsonApiDeserializer;
import net.explorviz.settings.server.providers.SettingValueDeserializer;
import net.explorviz.settings.server.providers.UserSettingJsonApiDeserializer;
import net.explorviz.settings.server.resources.SettingsResource;
import net.explorviz.settings.server.resources.UserSettingsResource;
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
    GenericTypeFinder.getTypeMap().put("BooleanSettings", BooleanSetting.class);
    GenericTypeFinder.getTypeMap().put("UserSettings", UserSetting.class);
    GenericTypeFinder.getTypeMap().put("DoubleSetting", DoubleSetting.class);
    GenericTypeFinder.getTypeMap().put("StringSetting", StringSetting.class);
    GenericTypeFinder.getTypeMap().put("SettingValue", UserSettingsResource.SettingValue.class);
    GenericTypeFinder.getTypeMap().put("CustomSettings", UserSettingsResource.CustomSettings.class);


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

    this.register(SettingsResource.class);
    this.register(UserSettingsResource.class);
    this.register(SettingValueDeserializer.class);


  }

}
