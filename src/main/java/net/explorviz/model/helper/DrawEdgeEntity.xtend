package net.explorviz.model.helper;

import java.util.ArrayList
import java.util.List
import org.eclipse.xtend.lib.annotations.Accessors
import net.explorviz.math.Vector3f
import org.eclipse.elk.alg.layered.graph.LEdge
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties("kielerEdgeReferences")
abstract class DrawEdgeEntity extends BaseEntity {
	@Accessors transient val List<LEdge> kielerEdgeReferences = new ArrayList<LEdge>

	@Accessors transient var float lineThickness
	@Accessors transient var float positionZ
	@Accessors transient val List<Point> points = new ArrayList<Point>

	@Accessors transient val List<Vector3f> pointsFor3D = new ArrayList<Vector3f>

}
