package net.explorviz.server.providers;

import net.explorviz.model.application.Application;
import net.explorviz.model.application.ApplicationCommunication;
import net.explorviz.model.application.Clazz;
import net.explorviz.model.application.ClazzCommunication;
import net.explorviz.model.application.Component;
import net.explorviz.model.application.DatabaseQuery;
import net.explorviz.model.application.RuntimeInformation;
import net.explorviz.model.landscape.Landscape;
import net.explorviz.model.landscape.Node;
import net.explorviz.model.landscape.NodeGroup;
import net.explorviz.model.security.User;
import net.explorviz.model.store.Timestamp;

/**
 * Handles the registration of (core) model types for the JSONAPI provideer
 *
 * @author Christian Zirkelbach (czi@informatik.uni-kiel.de)
 *
 */
public final class CoreModelHandler {

	/**
	 * Registers all model types (below the package model) for the JSONAPI provider
	 */
	public static void registerAllCoreModels() {
		GenericTypeFinder.typeMap.putIfAbsent("Timestamp", Timestamp.class);
		GenericTypeFinder.typeMap.putIfAbsent("Landscape", Landscape.class);
		GenericTypeFinder.typeMap.putIfAbsent("System", System.class);
		GenericTypeFinder.typeMap.putIfAbsent("NodeGroup", NodeGroup.class);
		GenericTypeFinder.typeMap.putIfAbsent("Node", Node.class);
		GenericTypeFinder.typeMap.putIfAbsent("Application", Application.class);
		GenericTypeFinder.typeMap.putIfAbsent("Component", Component.class);
		GenericTypeFinder.typeMap.putIfAbsent("Clazz", Clazz.class);
		GenericTypeFinder.typeMap.putIfAbsent("ClazzCommunication", ClazzCommunication.class);
		GenericTypeFinder.typeMap.putIfAbsent("ApplicationCommunication", ApplicationCommunication.class);
		GenericTypeFinder.typeMap.putIfAbsent("RuntimeInformation", RuntimeInformation.class);
		GenericTypeFinder.typeMap.putIfAbsent("DatabaseQuery", DatabaseQuery.class);
		GenericTypeFinder.typeMap.putIfAbsent("User", User.class);
	}
}
