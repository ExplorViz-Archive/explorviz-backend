package net.explorviz.landscape.server.providers;

import net.explorviz.landscape.model.application.Application;
import net.explorviz.landscape.model.application.ApplicationCommunication;
import net.explorviz.landscape.model.application.BidrectionalClazzCommunication;
import net.explorviz.landscape.model.application.Clazz;
import net.explorviz.landscape.model.application.ClazzCommunication;
import net.explorviz.landscape.model.application.Component;
import net.explorviz.landscape.model.application.DatabaseQuery;
import net.explorviz.landscape.model.application.RuntimeInformation;
import net.explorviz.landscape.model.application.Trace;
import net.explorviz.landscape.model.application.UnidirectionalClazzCommunication;
import net.explorviz.landscape.model.landscape.Landscape;
import net.explorviz.landscape.model.landscape.Node;
import net.explorviz.landscape.model.landscape.NodeGroup;
import net.explorviz.landscape.model.store.Timestamp;
import net.explorviz.shared.security.User;

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
    GenericTypeFinder.getTypeMap().putIfAbsent("Landscape", Landscape.class);
    GenericTypeFinder.getTypeMap().putIfAbsent("System", System.class);
    GenericTypeFinder.getTypeMap().putIfAbsent("NodeGroup", NodeGroup.class);
    GenericTypeFinder.getTypeMap().putIfAbsent("Node", Node.class);
    GenericTypeFinder.getTypeMap().putIfAbsent("Application", Application.class);
    GenericTypeFinder.getTypeMap().putIfAbsent("Component", Component.class);
    GenericTypeFinder.getTypeMap().putIfAbsent("Clazz", Clazz.class);
    GenericTypeFinder.getTypeMap().putIfAbsent("ClazzCommunication", ClazzCommunication.class);
    GenericTypeFinder.getTypeMap().putIfAbsent("Trace", Trace.class);
    GenericTypeFinder.getTypeMap().putIfAbsent("UnidirectionalClazzCommunication",
        UnidirectionalClazzCommunication.class);
    GenericTypeFinder.getTypeMap().putIfAbsent("BidirectionalClazzCommunication",
        BidrectionalClazzCommunication.class);
    GenericTypeFinder.getTypeMap().putIfAbsent("ApplicationCommunication",
        ApplicationCommunication.class);
    GenericTypeFinder.getTypeMap().putIfAbsent("RuntimeInformation", RuntimeInformation.class);
    GenericTypeFinder.getTypeMap().putIfAbsent("DatabaseQuery", DatabaseQuery.class);
    GenericTypeFinder.getTypeMap().putIfAbsent("User", User.class);
  }
}
