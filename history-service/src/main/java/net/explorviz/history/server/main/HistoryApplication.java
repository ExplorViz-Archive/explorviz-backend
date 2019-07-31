package net.explorviz.history.server.main; // NOPMD

import net.explorviz.history.server.resources.LandscapeResource;
import net.explorviz.history.server.resources.TimestampResource;
import net.explorviz.shared.common.provider.GenericTypeFinder;
import net.explorviz.shared.common.provider.JsonApiListProvider;
import net.explorviz.shared.common.provider.JsonApiProvider;
import net.explorviz.shared.exceptions.mapper.GeneralExceptionMapper;
import net.explorviz.shared.exceptions.mapper.InvalidJsonApiResourceExceptionMapper;
import net.explorviz.shared.exceptions.mapper.UnregisteredTypeExceptionMapper;
import net.explorviz.shared.exceptions.mapper.WebApplicationExceptionMapper;
import net.explorviz.shared.landscape.model.application.AggregatedClazzCommunication;
import net.explorviz.shared.landscape.model.application.Application;
import net.explorviz.shared.landscape.model.application.ApplicationCommunication;
import net.explorviz.shared.landscape.model.application.Clazz;
import net.explorviz.shared.landscape.model.application.ClazzCommunication;
import net.explorviz.shared.landscape.model.application.Component;
import net.explorviz.shared.landscape.model.application.DatabaseQuery;
import net.explorviz.shared.landscape.model.application.Trace;
import net.explorviz.shared.landscape.model.application.TraceStep;
import net.explorviz.shared.landscape.model.event.EEventType;
import net.explorviz.shared.landscape.model.event.Event;
import net.explorviz.shared.landscape.model.landscape.Landscape;
import net.explorviz.shared.landscape.model.landscape.Node;
import net.explorviz.shared.landscape.model.landscape.NodeGroup;
import net.explorviz.shared.landscape.model.landscape.System;
import net.explorviz.shared.landscape.model.store.Timestamp;
import net.explorviz.shared.querying.PaginationJsonApiWriter;
import net.explorviz.shared.querying.PaginationParameterFilter;
import net.explorviz.shared.security.filters.AuthenticationFilter;
import net.explorviz.shared.security.filters.AuthorizationFilter;
import net.explorviz.shared.security.filters.CorsResponseFilter;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * Starting configuration for the history service - includes registring models, resources, exception
 * handers, providers.
 */
public class HistoryApplication extends ResourceConfig {

  /**
   * Starting configuration for the history service - includes registring models, resources,
   * exception handers, providers.
   */
  public HistoryApplication() {

    super();

    HistoryApplication.registerLandscapeModels();

    this.register(new DependencyInjectionBinder());

    // register filters, e.g., authentication
    this.register(AuthenticationFilter.class);
    this.register(AuthorizationFilter.class);
    this.register(CorsResponseFilter.class);
    this.register(PaginationParameterFilter.class);

    // exception handling (mind the order !)
    this.register(WebApplicationExceptionMapper.class);
    this.register(InvalidJsonApiResourceExceptionMapper.class);
    this.register(UnregisteredTypeExceptionMapper.class);
    this.register(GeneralExceptionMapper.class);

    this.register(SetupApplicationListener.class);

    // easy (de-)serializing models for HTTP Requests
    this.register(JsonApiProvider.class);
    this.register(JsonApiListProvider.class);
    this.register(PaginationJsonApiWriter.class);

    this.register(LandscapeResource.class);
    this.register(TimestampResource.class);

    // swagger
    this.packages("io.swagger.v3.jaxrs2.integration.resources");
  }

  /**
   * Registers ExplorViz's meta model classes for the {@link GenericTypeFinder} workaround for
   * {@link JsonApiListProvider} (de)serialization.
   */
  public static void registerLandscapeModels() {
    GenericTypeFinder.getTypeMap().putIfAbsent("Timestamp", Timestamp.class);
    GenericTypeFinder.getTypeMap().putIfAbsent("Event", Event.class);
    GenericTypeFinder.getTypeMap().putIfAbsent("EEventType", EEventType.class);
    GenericTypeFinder.getTypeMap().putIfAbsent("Landscape", Landscape.class);
    GenericTypeFinder.getTypeMap().putIfAbsent("System", System.class);
    GenericTypeFinder.getTypeMap().putIfAbsent("NodeGroup", NodeGroup.class);
    GenericTypeFinder.getTypeMap().putIfAbsent("Node", Node.class);
    GenericTypeFinder.getTypeMap().putIfAbsent("Application", Application.class);
    GenericTypeFinder.getTypeMap()
        .putIfAbsent("ApplicationCommunication", ApplicationCommunication.class);
    GenericTypeFinder.getTypeMap().putIfAbsent("Component", Component.class);
    GenericTypeFinder.getTypeMap().putIfAbsent("Clazz", Clazz.class);
    GenericTypeFinder.getTypeMap().putIfAbsent("ClazzCommunication", ClazzCommunication.class);
    GenericTypeFinder.getTypeMap().putIfAbsent("Trace", Trace.class);
    GenericTypeFinder.getTypeMap().putIfAbsent("TraceStep", TraceStep.class);
    GenericTypeFinder.getTypeMap()
        .putIfAbsent("AggregatedClazzCommunication", AggregatedClazzCommunication.class);
    GenericTypeFinder.getTypeMap().putIfAbsent("DatabaseQuery", DatabaseQuery.class);
  }
}
