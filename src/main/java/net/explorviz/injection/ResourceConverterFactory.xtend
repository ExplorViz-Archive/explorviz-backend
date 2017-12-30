package net.explorviz.injection

import org.glassfish.hk2.api.Factory
import com.github.jasminb.jsonapi.ResourceConverter
import com.github.jasminb.jsonapi.SerializationFeature
import net.explorviz.model.Application
import net.explorviz.model.Clazz
import net.explorviz.model.Communication
import net.explorviz.model.CommunicationClazz
import net.explorviz.model.Component
import net.explorviz.model.Landscape
import net.explorviz.model.Node
import net.explorviz.model.NodeGroup
import net.explorviz.model.helper.CommunicationAccumulator
import net.explorviz.model.helper.CommunicationTileAccumulator
import net.explorviz.model.TimestampStorage
import net.explorviz.model.Timestamp
import net.explorviz.model.DatabaseQuery
import net.explorviz.server.security.User

class ResourceConverterFactory implements Factory<ResourceConverter> {
	val ResourceConverter converter

	new() {
		this.converter = new ResourceConverter(TimestampStorage, Timestamp, Landscape, net.explorviz.model.System,
			NodeGroup, Node, Application, Component, Clazz, CommunicationClazz, Communication, CommunicationAccumulator,
			CommunicationTileAccumulator, DatabaseQuery, User)
		this.converter.enableSerializationOption(SerializationFeature.INCLUDE_RELATIONSHIP_ATTRIBUTES)
	}

	override void dispose(ResourceConverter arg0) {
	}

	override ResourceConverter provide() {
		return converter
	}

	def void registerClass(Class<?> classToRegister) {
		converter.registerType(classToRegister)
	}
}
