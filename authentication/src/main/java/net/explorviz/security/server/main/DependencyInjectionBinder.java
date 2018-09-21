package net.explorviz.security.server.main;

import javax.inject.Singleton;
import net.explorviz.security.services.TokenService;
import net.explorviz.security.services.UserService;
import net.explorviz.shared.annotations.Config;
import net.explorviz.shared.annotations.injection.ConfigInjectionResolver;
import net.explorviz.shared.exceptions.ErrorObjectHelper;
import net.explorviz.shared.security.TokenParserService;
import org.glassfish.hk2.api.InjectionResolver;
import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

public class DependencyInjectionBinder extends AbstractBinder {

  @Override
  public void configure() {

    // injectable config properties
    this.bind(new ConfigInjectionResolver()).to(new TypeLiteral<InjectionResolver<Config>>() {});

    this.bind(TokenService.class).to(TokenService.class).in(Singleton.class);
    this.bind(UserService.class).to(UserService.class).in(Singleton.class);
    this.bind(TokenParserService.class).to(TokenParserService.class).in(Singleton.class);

    // ErrorObject Handler
    this.bind(ErrorObjectHelper.class).to(ErrorObjectHelper.class).in(Singleton.class);
  }
}
