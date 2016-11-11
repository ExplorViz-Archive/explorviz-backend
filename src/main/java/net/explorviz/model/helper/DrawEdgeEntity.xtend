package net.explorviz.model.helper;

import de.cau.cs.kieler.klay.layered.graph.LEdge
import java.util.ArrayList
import java.util.List
import org.eclipse.xtend.lib.annotations.Accessors
import net.explorviz.math.Vector3f;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties("kielerEdgeReferences")
abstract class DrawEdgeEntity extends BaseEntity {
	@Accessors transient val List<LEdge> kielerEdgeReferences = new ArrayList<LEdge>

	@Accessors transient var float lineThickness
	@Accessors transient var float positionZ
	@Accessors transient val List<Point> points = new ArrayList<Point>

	@Accessors transient val List<Vector3f> pointsFor3D = new ArrayList<Vector3f>

}
