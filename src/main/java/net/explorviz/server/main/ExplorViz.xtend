package net.explorviz.server.main

import net.explorviz.server.exceptions.mapper.GeneralExceptionMapper
import net.explorviz.server.exceptions.mapper.PathParamExceptionMapper
import net.explorviz.server.exceptions.mapper.QueryParamExceptionMapper
import org.glassfish.jersey.server.ResourceConfig
import javax.ws.rs.ApplicationPath
import net.explorviz.server.providers.GenericTypeFinder
import net.explorviz.model.Timestamp
import net.explorviz.server.security.User
import net.explorviz.model.DatabaseQuery
import net.explorviz.model.helper.CommunicationTileAccumulator
import net.explorviz.model.helper.CommunicationAccumulator
import net.explorviz.model.Communication
import net.explorviz.model.CommunicationClazz
import net.explorviz.model.Clazz
import net.explorviz.model.Component
import net.explorviz.model.Application
import net.explorviz.model.Node
import net.explorviz.model.NodeGroup
import net.explorviz.model.System
import net.explorviz.model.Landscape

@ApplicationPath("")
class ExplorViz extends ResourceConfig {
	new() {
		
		GenericTypeFinder.typeMap.putIfAbsent("Timestamp", Timestamp);
		GenericTypeFinder.typeMap.putIfAbsent("Landscape", Landscape);
		GenericTypeFinder.typeMap.putIfAbsent("System", System);
		GenericTypeFinder.typeMap.putIfAbsent("NodeGroup", NodeGroup);
		GenericTypeFinder.typeMap.putIfAbsent("Node", Node);
		GenericTypeFinder.typeMap.putIfAbsent("Application", Application);
		GenericTypeFinder.typeMap.putIfAbsent("Component", Component);
		GenericTypeFinder.typeMap.putIfAbsent("Clazz", Clazz);
		GenericTypeFinder.typeMap.putIfAbsent("CommunicationClazz", CommunicationClazz);
		GenericTypeFinder.typeMap.putIfAbsent("Communication", Communication);
		GenericTypeFinder.typeMap.putIfAbsent("CommunicationAccumulator", CommunicationAccumulator);
		GenericTypeFinder.typeMap.putIfAbsent("CommunicationTileAccumulator", CommunicationTileAccumulator);
		GenericTypeFinder.typeMap.putIfAbsent("DatabaseQuery", DatabaseQuery);
		GenericTypeFinder.typeMap.putIfAbsent("User", User);
		
		register(new DependencyInjectionBinder())
		//register(JacksonFeature)
		
		// Authentication & Authorization
		packages("net.explorviz.server.filters")
		
		// resources
		packages("net.explorviz.server.resources")
		
		// exception handling (mind the order !)
		register(QueryParamExceptionMapper)
		register(PathParamExceptionMapper)
		register(GeneralExceptionMapper)
		
		// easy (de-)serializing models for HTTP Requests
		packages("net.explorviz.server.providers")
		
		// register extensions (For development: read plugin structure at github docs)
		packages("net.explorviz.extension")
		
	}
}
