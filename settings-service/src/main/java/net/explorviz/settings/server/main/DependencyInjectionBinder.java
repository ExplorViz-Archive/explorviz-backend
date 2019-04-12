package net.explorviz.settings.server.main;

import javax.inject.Singleton;
import net.explorviz.settings.server.inject.DatastoreFactory;
import net.explorviz.shared.common.injection.CommonDependencyInjectionBinder;
import xyz.morphia.Datastore;

public class DependencyInjectionBinder extends CommonDependencyInjectionBinder {
  
  @Override
  public void configure() {

    super.configure();

    this.bindFactory(DatastoreFactory.class).to(Datastore.class).in(Singleton.class);
    
  }
  

}
