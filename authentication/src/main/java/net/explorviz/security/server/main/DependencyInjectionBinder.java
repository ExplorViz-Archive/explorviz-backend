package net.explorviz.security.server.main;

import com.github.jasminb.jsonapi.ResourceConverter;
import javax.inject.Singleton;
import net.explorviz.security.server.providers.ResourceConverterFactory;
import net.explorviz.security.services.TokenService;
import net.explorviz.security.services.UserCrudMongoService;
import net.explorviz.security.services.UserCrudService;
import net.explorviz.security.services.UserValidationService;
import net.explorviz.shared.annotations.Config;
import net.explorviz.shared.annotations.injection.ConfigInjectionResolver;
import net.explorviz.shared.exceptions.ErrorObjectHelper;
import net.explorviz.shared.security.TokenParserService;
import org.glassfish.hk2.api.InjectionResolver;
import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

/**
 * The DependencyInjectionBinder is used to register Contexts and Dependency Injection (CDI) aspects
 * for this application.
 */
public class DependencyInjectionBinder extends AbstractBinder {

  @Override
  public void configure() {

    // injectable config properties
    this.bind(new ConfigInjectionResolver()).to(new TypeLiteral<InjectionResolver<Config>>() {});

    this.bindFactory(ResourceConverterFactory.class).to(ResourceConverter.class)
        .in(Singleton.class);

    this.bind(TokenService.class).to(TokenService.class).in(Singleton.class);
    this.bind(UserValidationService.class).to(UserValidationService.class).in(Singleton.class);
    this.bind(TokenParserService.class).to(TokenParserService.class).in(Singleton.class);
    this.bind(UserCrudMongoService.class).to(UserCrudService.class).in(Singleton.class);



    // ErrorObject Handler
    this.bind(ErrorObjectHelper.class).to(ErrorObjectHelper.class).in(Singleton.class);


  }
}
