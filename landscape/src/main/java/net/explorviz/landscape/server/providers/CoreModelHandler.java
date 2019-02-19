package net.explorviz.landscape.server.providers;

import net.explorviz.shared.common.provider.GenericTypeFinder;
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
import net.explorviz.shared.security.model.User;

/**
 * Handles the registration of (core) model types for the JSONAPI provider.
 */
public final class CoreModelHandler {

  private CoreModelHandler() {
    // Utility Class
  }

  /**
   * Registers all model types (below the package model) for the JSONAPI provider.
   */
  public static void registerAllCoreModels() {
    GenericTypeFinder.getTypeMap().putIfAbsent("Timestamp", Timestamp.class);
    GenericTypeFinder.getTypeMap().putIfAbsent("Event", Event.class);
    GenericTypeFinder.getTypeMap().putIfAbsent("EEventType", EEventType.class);
    GenericTypeFinder.getTypeMap().putIfAbsent("Landscape", Landscape.class);
    GenericTypeFinder.getTypeMap().putIfAbsent("System", System.class);
    GenericTypeFinder.getTypeMap().putIfAbsent("NodeGroup", NodeGroup.class);
    GenericTypeFinder.getTypeMap().putIfAbsent("Node", Node.class);
    GenericTypeFinder.getTypeMap().putIfAbsent("Application", Application.class);
    GenericTypeFinder.getTypeMap().putIfAbsent("ApplicationCommunication",
        ApplicationCommunication.class);
    GenericTypeFinder.getTypeMap().putIfAbsent("Component", Component.class);
    GenericTypeFinder.getTypeMap().putIfAbsent("Clazz", Clazz.class);
    GenericTypeFinder.getTypeMap().putIfAbsent("ClazzCommunication", ClazzCommunication.class);
    GenericTypeFinder.getTypeMap().putIfAbsent("Trace", Trace.class);
    GenericTypeFinder.getTypeMap().putIfAbsent("TraceStep", TraceStep.class);
    GenericTypeFinder.getTypeMap().putIfAbsent("AggregatedClazzCommunication",
        AggregatedClazzCommunication.class);
    GenericTypeFinder.getTypeMap().putIfAbsent("DatabaseQuery", DatabaseQuery.class);
    GenericTypeFinder.getTypeMap().putIfAbsent("User", User.class);
  }
}
