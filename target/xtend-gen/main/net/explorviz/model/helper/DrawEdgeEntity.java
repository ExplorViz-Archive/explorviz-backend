package net.explorviz.model.helper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.cau.cs.kieler.klay.layered.graph.LEdge;
import java.util.ArrayList;
import java.util.List;
import net.explorviz.math.Vector3f;
import net.explorviz.model.helper.BaseEntity;
import net.explorviz.model.helper.Point;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.xbase.lib.Pure;

@JsonIgnoreProperties("kielerEdgeReferences")
@SuppressWarnings("all")
public abstract class DrawEdgeEntity extends BaseEntity {
  @Accessors
  private final transient List<LEdge> kielerEdgeReferences = new ArrayList<LEdge>();
  
  @Accessors
  private transient float lineThickness;
  
  @Accessors
  private transient float positionZ;
  
  @Accessors
  private final transient List<Point> points = new ArrayList<Point>();
  
  @Accessors
  private final transient List<Vector3f> pointsFor3D = new ArrayList<Vector3f>();
  
  @Pure
  public List<LEdge> getKielerEdgeReferences() {
    return this.kielerEdgeReferences;
  }
  
  @Pure
  public float getLineThickness() {
    return this.lineThickness;
  }
  
  public void setLineThickness(final float lineThickness) {
    this.lineThickness = lineThickness;
  }
  
  @Pure
  public float getPositionZ() {
    return this.positionZ;
  }
  
  public void setPositionZ(final float positionZ) {
    this.positionZ = positionZ;
  }
  
  @Pure
  public List<Point> getPoints() {
    return this.points;
  }
  
  @Pure
  public List<Vector3f> getPointsFor3D() {
    return this.pointsFor3D;
  }
}
