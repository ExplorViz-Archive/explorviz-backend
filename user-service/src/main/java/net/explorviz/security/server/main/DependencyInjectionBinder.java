package net.explorviz.security.server.main;

import javax.inject.Singleton;
import net.explorviz.security.injection.KafkaProducerFactory;
import net.explorviz.security.server.injection.DatastoreFactory;
import net.explorviz.security.server.resources.BatchRequestResource;
import net.explorviz.security.services.BatchService;
import net.explorviz.security.services.KafkaUserService;
import net.explorviz.security.services.TokenService;
import net.explorviz.security.services.UserService;
import net.explorviz.security.services.exceptions.UserValidationService;
import net.explorviz.shared.common.idgen.IdGenerator;
import net.explorviz.shared.common.injection.CommonDependencyInjectionBinder;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.glassfish.hk2.api.TypeLiteral;
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

    this.bindFactory(KafkaProducerFactory.class)
        .to(new TypeLiteral<KafkaProducer<String, String>>() {});

    this.bind(KafkaUserService.class).to(KafkaUserService.class).in(Singleton.class);

    this.bind(TokenService.class).to(TokenService.class).in(Singleton.class);
    this.bind(UserValidationService.class).to(UserValidationService.class).in(Singleton.class);
    this.bind(UserService.class).to(UserService.class).in(Singleton.class);

    this.bind(IdGenerator.class).to(IdGenerator.class).in(Singleton.class);
    this.bind(BatchService.class).to(BatchService.class).in(Singleton.class);
    this.bind(BatchRequestResource.class).to(BatchRequestResource.class).in(Singleton.class);

  }
}
