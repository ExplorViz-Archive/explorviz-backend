package net.explorviz.server.providers;

import net.explorviz.model.Application;
import net.explorviz.model.Clazz;
import net.explorviz.model.Component;
import net.explorviz.model.DatabaseQuery;
import net.explorviz.model.Landscape;
import net.explorviz.model.Node;
import net.explorviz.model.NodeGroup;
import net.explorviz.model.Timestamp;
import net.explorviz.model.communication.ApplicationCommunication;
import net.explorviz.model.communication.ClazzCommunication;
import net.explorviz.model.helper.CommunicationAccumulator;
import net.explorviz.model.helper.CommunicationTileAccumulator;
import net.explorviz.server.security.User;

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
		GenericTypeFinder.typeMap.putIfAbsent("CommunicationClazz", ClazzCommunication.class);
		GenericTypeFinder.typeMap.putIfAbsent("Communication", ApplicationCommunication.class);
		GenericTypeFinder.typeMap.putIfAbsent("CommunicationAccumulator", CommunicationAccumulator.class);
		GenericTypeFinder.typeMap.putIfAbsent("CommunicationTileAccumulator", CommunicationTileAccumulator.class);
		GenericTypeFinder.typeMap.putIfAbsent("DatabaseQuery", DatabaseQuery.class);
		GenericTypeFinder.typeMap.putIfAbsent("User", User.class);
	}
}
