package net.explorviz.model.helper

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import net.explorviz.model.NodeGroup
import net.explorviz.model.Application
import net.explorviz.model.Node
import net.explorviz.model.Communication
import net.explorviz.model.Landscape
import net.explorviz.model.RuntimeInformation
import net.explorviz.model.CommunicationClazz
import net.explorviz.model.Clazz
import net.explorviz.model.DatabaseQuery
import net.explorviz.math.Vector3f
import net.explorviz.math.Vector4f

@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="type")
@JsonIdentityInfo(generator=ObjectIdGenerators.IntSequenceGenerator, property="id")
@JsonSubTypes(@JsonSubTypes.Type(value=DrawEdgeEntity, name="drawEdgeEntity"),
	@JsonSubTypes.Type(value=Draw3DNodeEntity, name="draw3DNodeEntity"),
	@JsonSubTypes.Type(value=Landscape, name="landscape"),	
	@JsonSubTypes.Type(value=System, name="system"),
	@JsonSubTypes.Type(value=Communication, name="communication"),	
	@JsonSubTypes.Type(value=NodeGroup, name="nodeGroup"),
	@JsonSubTypes.Type(value=Node, name="node"),	
	@JsonSubTypes.Type(value=Application, name="application"),
	@JsonSubTypes.Type(value=CommunicationAccumulator, name="communicationAccumulator"),
	@JsonSubTypes.Type(value=CommunicationTileAccumulator, name="communicationTileAccumulator"),
	@JsonSubTypes.Type(value=CommunicationAppAccumulator, name="communicationAppAccumulator"),
	@JsonSubTypes.Type(value=Point, name="point"),
	@JsonSubTypes.Type(value=CommunicationTileAccumulator, name="communicationTileAccumulator"),
	@JsonSubTypes.Type(value=RuntimeInformation, name="runtimeInformation"),
	@JsonSubTypes.Type(value=CommunicationClazz, name="communicationClazz"),
	@JsonSubTypes.Type(value=Clazz, name="clazz"),
	@JsonSubTypes.Type(value=DatabaseQuery, name="databaseQuery"),
	@JsonSubTypes.Type(value=Vector3f, name="vector3f"),
	@JsonSubTypes.Type(value=Vector4f, name="vector4f"))
abstract class BaseEntity {
}
