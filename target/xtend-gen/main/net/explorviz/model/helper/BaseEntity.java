package net.explorviz.model.helper;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import net.explorviz.math.Vector3f;
import net.explorviz.math.Vector4f;
import net.explorviz.model.Application;
import net.explorviz.model.Clazz;
import net.explorviz.model.Communication;
import net.explorviz.model.CommunicationClazz;
import net.explorviz.model.DatabaseQuery;
import net.explorviz.model.Landscape;
import net.explorviz.model.Node;
import net.explorviz.model.NodeGroup;
import net.explorviz.model.RuntimeInformation;
import net.explorviz.model.helper.CommunicationAccumulator;
import net.explorviz.model.helper.CommunicationAppAccumulator;
import net.explorviz.model.helper.CommunicationTileAccumulator;
import net.explorviz.model.helper.Draw3DNodeEntity;
import net.explorviz.model.helper.DrawEdgeEntity;
import net.explorviz.model.helper.Point;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "id")
@JsonSubTypes({ @JsonSubTypes.Type(value = DrawEdgeEntity.class, name = "drawEdgeEntity"), @JsonSubTypes.Type(value = Draw3DNodeEntity.class, name = "draw3DNodeEntity"), @JsonSubTypes.Type(value = Landscape.class, name = "landscape"), @JsonSubTypes.Type(value = System.class, name = "system"), @JsonSubTypes.Type(value = Communication.class, name = "communication"), @JsonSubTypes.Type(value = NodeGroup.class, name = "nodeGroup"), @JsonSubTypes.Type(value = Node.class, name = "node"), @JsonSubTypes.Type(value = Application.class, name = "application"), @JsonSubTypes.Type(value = CommunicationAccumulator.class, name = "communicationAccumulator"), @JsonSubTypes.Type(value = CommunicationTileAccumulator.class, name = "communicationTileAccumulator"), @JsonSubTypes.Type(value = CommunicationAppAccumulator.class, name = "communicationAppAccumulator"), @JsonSubTypes.Type(value = Point.class, name = "point"), @JsonSubTypes.Type(value = CommunicationTileAccumulator.class, name = "communicationTileAccumulator"), @JsonSubTypes.Type(value = RuntimeInformation.class, name = "runtimeInformation"), @JsonSubTypes.Type(value = CommunicationClazz.class, name = "communicationClazz"), @JsonSubTypes.Type(value = Clazz.class, name = "clazz"), @JsonSubTypes.Type(value = DatabaseQuery.class, name = "databaseQuery"), @JsonSubTypes.Type(value = Vector3f.class, name = "vector3f"), @JsonSubTypes.Type(value = Vector4f.class, name = "vector4f") })
@SuppressWarnings("all")
public abstract class BaseEntity {
}
