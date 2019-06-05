package net.explorviz.security.server.main;

import javax.inject.Singleton;
import net.explorviz.security.server.injection.DatastoreFactory;
import net.explorviz.security.server.resources.BatchRequestSubResource;
import net.explorviz.security.services.BatchCreationService;
import net.explorviz.security.services.RoleService;
import net.explorviz.security.services.TokenService;
import net.explorviz.security.services.UserMongoCrudService;
import net.explorviz.security.services.UserValidationService;
import net.explorviz.shared.common.idgen.IdGenerator;
import net.explorviz.shared.common.injection.CommonDependencyInjectionBinder;
import xyz.morphia.Datastore;

/**
 * The DependencyInjectionBinder is used to register Contexts and Dependency Injection (CDI) aspects
 * for this application.
 */
public class DependencyInjectionBinder extends CommonDependencyInjectionBinder {

  @Override
  public void configure() {

    super.configure();

    this.bindFactory(DatastoreFactory.class).to(Datastore.class).in(Singleton.class);

    this.bind(TokenService.class).to(TokenService.class).in(Singleton.class);
    this.bind(UserValidationService.class).to(UserValidationService.class).in(Singleton.class);
    this.bind(UserMongoCrudService.class).to(UserMongoCrudService.class).in(Singleton.class);

    this.bind(RoleService.class).to(RoleService.class).in(Singleton.class);

    this.bind(IdGenerator.class).to(IdGenerator.class).in(Singleton.class);
    this.bind(BatchCreationService.class).to(BatchCreationService.class).in(Singleton.class);
    this.bind(BatchRequestSubResource.class).to(BatchRequestSubResource.class).in(Singleton.class);

  }
}
