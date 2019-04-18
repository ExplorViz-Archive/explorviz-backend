package net.explorviz.settings.server.main;

import javax.inject.Singleton;
import net.explorviz.settings.model.Setting;
import net.explorviz.settings.model.UserSetting;
import net.explorviz.settings.server.inject.DatastoreFactory;
import net.explorviz.settings.services.MongoRepository;
import net.explorviz.settings.services.SettingsRepository;
import net.explorviz.settings.services.SettingsService;
import net.explorviz.settings.services.UserSettingsRepository;
import net.explorviz.shared.common.injection.CommonDependencyInjectionBinder;
import org.glassfish.hk2.api.TypeLiteral;
import xyz.morphia.Datastore;

public class DependencyInjectionBinder extends CommonDependencyInjectionBinder {
  
  @Override
  public void configure() {

    super.configure();

    this.bindFactory(DatastoreFactory.class).to(Datastore.class).in(Singleton.class);
    
    // Service
    this.bind(SettingsRepository.class).to(new TypeLiteral<MongoRepository<Setting, String>>() {}).in(Singleton.class);
    this.bind(UserSettingsRepository.class).to(new TypeLiteral<MongoRepository<UserSetting, UserSetting.UserSettingId>>() {}).in(Singleton.class);
    this.bind(SettingsService.class).to(SettingsService.class).in(Singleton.class);

    
  }
  

}
