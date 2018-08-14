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
		GenericTypeFinder.typeMap.putIfAbsent("AggregatedClazzCommunication", AggregatedClazzCommunication.class);
		GenericTypeFinder.typeMap.putIfAbsent("CumulatedClazzCommunication", CumulatedClazzCommunication.class);
		GenericTypeFinder.typeMap.putIfAbsent("ApplicationCommunication", ApplicationCommunication.class);
		GenericTypeFinder.typeMap.putIfAbsent("RuntimeInformation", RuntimeInformation.class);
		GenericTypeFinder.typeMap.putIfAbsent("DatabaseQuery", DatabaseQuery.class);
		GenericTypeFinder.typeMap.putIfAbsent("User", User.class);

		GenericTypeFinder.typeMap.putIfAbsent("Agent", Agent.class);
		GenericTypeFinder.typeMap.putIfAbsent("Procezz", Procezz.class);
	}
}
