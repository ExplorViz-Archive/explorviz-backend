package net.explorviz.settings.server.main;

import javax.inject.Singleton;
import net.explorviz.settings.model.Setting;
import net.explorviz.settings.server.inject.DatastoreFactory;
import net.explorviz.settings.services.CustomSettingsRepository;
import net.explorviz.settings.services.CustomSettingsService;
import net.explorviz.settings.services.MongoRepository;
import net.explorviz.settings.services.SettingsRepository;
import net.explorviz.settings.services.mongo.MongoHelper;
import net.explorviz.shared.common.injection.CommonDependencyInjectionBinder;
import org.glassfish.hk2.api.TypeLiteral;
import xyz.morphia.Datastore;

public class DependencyInjectionBinder extends CommonDependencyInjectionBinder {

  @Override
  public void configure() {

    super.configure();

    this.bindFactory(DatastoreFactory.class).to(Datastore.class).in(Singleton.class);

    // Service
    this.bind(SettingsRepository.class).to(new TypeLiteral<MongoRepository<Setting, String>>() {})
        .in(Singleton.class);

    this.bind(MongoHelper.class).to(MongoHelper.class).in(Singleton.class);
    this.bind(SettingsRepository.class).to(SettingsRepository.class).in(Singleton.class);
    this.bind(CustomSettingsRepository.class).to(CustomSettingsRepository.class)
        .in(Singleton.class);
    this.bind(CustomSettingsService.class).to(CustomSettingsService.class).in(Singleton.class);
  }


}
