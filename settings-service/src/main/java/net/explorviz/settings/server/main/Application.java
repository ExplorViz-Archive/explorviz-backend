package net.explorviz.settings.server.main;

import net.explorviz.settings.server.main.SetupApplicationListener;
import net.explorviz.settings.server.resources.SettingsResource;
import net.explorviz.settings.server.resources.UserSettingsResource;
import net.explorviz.settings.server.main.DependencyInjectionBinder;
import net.explorviz.shared.security.filters.AuthorizationFilter;
import org.glassfish.jersey.server.ResourceConfig;

public class Application extends ResourceConfig {

  public Application() {

    // register CDI
    this.register(new DependencyInjectionBinder());
    
    this.register(new SettingsResource());
    this.register(new UserSettingsResource());
    
    this.register(net.explorviz.shared.security.filters.AuthenticationFilter.class);
    this.register(AuthorizationFilter.class);
    
    this.register(SetupApplicationListener.class);
    
    
  }
   
}
