package net.explorviz.landscape.server.main;

import net.explorviz.landscape.server.resources.LandscapeBroadcastResource;
import net.explorviz.shared.common.provider.GenericTypeFinder;
import net.explorviz.shared.common.provider.JsonApiListProvider;
import net.explorviz.shared.common.provider.JsonApiProvider;
import net.explorviz.shared.exceptions.mapper.GeneralExceptionMapper;
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
import net.explorviz.shared.security.filters.AuthenticationFilter;
import net.explorviz.shared.security.filters.AuthorizationFilter;
import net.explorviz.shared.security.filters.CorsResponseFilter;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * Starting configuration for the backend - includes registring models, resources, exception
 * handers, providers, and embedds extensions.
 */
public class LandscapeApplication extends ResourceConfig {
  public LandscapeApplication() {

    LandscapeApplication.registerLandscapeModels();

    this.register(new DependencyInjectionBinder());

    // easy (de-)serializing models for HTTP Requests
    this.register(JsonApiProvider.class);
    this.register(JsonApiListProvider.class);

    // https://stackoverflow.com/questions/30653012/
    // multipart-form-data-no-injection-source-found-for-a-parameter-of-type-public-ja/30656345
    // register for uploading landscapes
    this.register(MultiPartFeature.class);

    // register(JacksonFeature)

    // register filters, e.g., authentication
    this.register(AuthenticationFilter.class);
    this.register(AuthorizationFilter.class);
    this.register(CorsResponseFilter.class);

    // resources
    this.register(LandscapeBroadcastResource.class);

    // exception handling (mind the order !)
    this.register(WebApplicationExceptionMapper.class);
    this.register(GeneralExceptionMapper.class);

    this.register(SetupApplicationListener.class);
  }

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
