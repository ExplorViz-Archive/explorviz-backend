package net.explorviz.server.providers;

import net.explorviz.discovery.model.Agent;
import net.explorviz.discovery.model.Procezz;
import net.explorviz.model.application.AggregatedClazzCommunication;
import net.explorviz.model.application.Application;
import net.explorviz.model.application.ApplicationCommunication;
import net.explorviz.model.application.Clazz;
import net.explorviz.model.application.ClazzCommunication;
import net.explorviz.model.application.Component;
import net.explorviz.model.application.CumulatedClazzCommunication;
import net.explorviz.model.application.DatabaseQuery;
import net.explorviz.model.application.RuntimeInformation;
import net.explorviz.model.landscape.Landscape;
import net.explorviz.model.landscape.Node;
import net.explorviz.model.landscape.NodeGroup;
import net.explorviz.model.store.Timestamp;
import net.explorviz.shared.security.User;

/**
 * Handles the registration of (core) model types for the JSONAPI provideer.
 */
public final class CoreModelHandler {

  /**
   * Registers all model types (below the package model) for the JSONAPI provider.
   */
  public static void registerAllCoreModels() {
    GenericTypeFinder.getTypeMap().putIfAbsent("Timestamp", Timestamp.class);
    GenericTypeFinder.getTypeMap().putIfAbsent("Landscape", Landscape.class);
    GenericTypeFinder.getTypeMap().putIfAbsent("System", System.class);
    GenericTypeFinder.getTypeMap().putIfAbsent("NodeGroup", NodeGroup.class);
    GenericTypeFinder.getTypeMap().putIfAbsent("Node", Node.class);
    GenericTypeFinder.getTypeMap().putIfAbsent("Application", Application.class);
    GenericTypeFinder.getTypeMap().putIfAbsent("Component", Component.class);
    GenericTypeFinder.getTypeMap().putIfAbsent("Clazz", Clazz.class);
    GenericTypeFinder.getTypeMap().putIfAbsent("ClazzCommunication", ClazzCommunication.class);
    GenericTypeFinder.getTypeMap().putIfAbsent("AggregatedClazzCommunication",
        AggregatedClazzCommunication.class);
    GenericTypeFinder.getTypeMap().putIfAbsent("CumulatedClazzCommunication",
        CumulatedClazzCommunication.class);
    GenericTypeFinder.getTypeMap().putIfAbsent("ApplicationCommunication",
        ApplicationCommunication.class);
    GenericTypeFinder.getTypeMap().putIfAbsent("RuntimeInformation", RuntimeInformation.class);
    GenericTypeFinder.getTypeMap().putIfAbsent("DatabaseQuery", DatabaseQuery.class);
    GenericTypeFinder.getTypeMap().putIfAbsent("User", User.class);

    GenericTypeFinder.getTypeMap().putIfAbsent("Agent", Agent.class);
    GenericTypeFinder.getTypeMap().putIfAbsent("Procezz", Procezz.class);
  }
}
