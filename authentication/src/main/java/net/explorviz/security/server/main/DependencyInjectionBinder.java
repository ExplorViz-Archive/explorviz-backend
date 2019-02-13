package net.explorviz.security.server.main;

import com.github.jasminb.jsonapi.ResourceConverter;
import javax.inject.Singleton;
import net.explorviz.security.server.injection.DatastoreFactory;
import net.explorviz.security.server.providers.ResourceConverterFactory;
import net.explorviz.security.services.RoleService;
import net.explorviz.security.services.TokenService;
import net.explorviz.security.services.UserMongoCrudService;
import net.explorviz.security.services.UserValidationService;
import net.explorviz.shared.config.annotations.ConfigValues;
import net.explorviz.shared.config.annotations.injection.ConfigInjectionResolver;
import net.explorviz.shared.config.annotations.injection.ConfigValuesInjectionResolver;
import net.explorviz.shared.exceptions.ErrorObjectHelper;
import net.explorviz.shared.security.TokenParserService;
import org.glassfish.hk2.api.InjectionResolver;
import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import xyz.morphia.Datastore;

/**
 * The DependencyInjectionBinder is used to register Contexts and Dependency Injection (CDI) aspects
 * for this application.
 */
public class DependencyInjectionBinder extends AbstractBinder {

  @Override
  public void configure() {

    // Injectable config properties
    this.bind(new ConfigInjectionResolver())
        .to(new TypeLiteral<InjectionResolver<ConfigValues>>() {});
    this.bind(new ConfigValuesInjectionResolver())
        .to(new TypeLiteral<InjectionResolver<ConfigValues>>() {});

    this.bindFactory(ResourceConverterFactory.class).to(ResourceConverter.class)
        .in(Singleton.class);

    this.bindFactory(DatastoreFactory.class).to(Datastore.class).in(Singleton.class);

    this.bind(TokenService.class).to(TokenService.class).in(Singleton.class);
    this.bind(UserValidationService.class).to(UserValidationService.class).in(Singleton.class);
    this.bind(TokenParserService.class).to(TokenParserService.class).in(Singleton.class);
    this.bind(UserMongoCrudService.class).to(UserMongoCrudService.class).in(Singleton.class);

    this.bind(RoleService.class).to(RoleService.class).in(Singleton.class);

    // ErrorObject Handler
    this.bind(ErrorObjectHelper.class).to(ErrorObjectHelper.class).in(Singleton.class);


  }
}
