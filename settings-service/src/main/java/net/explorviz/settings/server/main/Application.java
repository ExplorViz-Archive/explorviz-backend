package net.explorviz.settings.server.main;

import net.explorviz.settings.server.main.SetupApplicationListener;
import net.explorviz.settings.server.main.DependencyInjectionBinder;
import net.explorviz.shared.common.provider.GenericTypeFinder;
import net.explorviz.shared.security.filters.AuthorizationFilter;
import net.explorviz.shared.security.model.User;
import net.explorviz.shared.security.model.roles.Role;
import net.explorviz.shared.security.model.settings.BooleanSettingDescriptor;
import net.explorviz.shared.security.model.settings.NumericSettingDescriptor;
import net.explorviz.shared.security.model.settings.StringSettingDescriptor;
import net.explorviz.shared.security.model.settings.UserSettings;
import org.glassfish.jersey.server.ResourceConfig;

public class Application extends ResourceConfig {

  public Application() {
    GenericTypeFinder.getTypeMap().put("User", User.class);
    GenericTypeFinder.getTypeMap().put("Role", Role.class);
    GenericTypeFinder.getTypeMap().put("UserSettings", UserSettings.class);
    GenericTypeFinder.getTypeMap().put("BooleanSettingDescriptor", BooleanSettingDescriptor.class);
    GenericTypeFinder.getTypeMap().put("NumericSettingDescriptor", NumericSettingDescriptor.class);
    GenericTypeFinder.getTypeMap().put("StringSettingDescriptor", StringSettingDescriptor.class);

    // register CDI
    this.register(new DependencyInjectionBinder());
    
    this.register(net.explorviz.shared.security.filters.AuthenticationFilter.class);
    this.register(AuthorizationFilter.class);
    
    this.register(SetupApplicationListener.class);
  }
   
}
