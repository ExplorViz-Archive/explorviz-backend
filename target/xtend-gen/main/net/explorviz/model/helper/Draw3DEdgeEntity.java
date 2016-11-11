package net.explorviz.model.helper;

import java.util.ArrayList;
import java.util.List;
import net.explorviz.math.Vector3f;
import net.explorviz.model.helper.EdgeState;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.xbase.lib.Pure;

@SuppressWarnings("all")
public abstract class Draw3DEdgeEntity {
  @Accessors
  private final transient List<Vector3f> points = new ArrayList<Vector3f>();
  
  @Accessors
  private transient float pipeSize;
  
  @Accessors
  private EdgeState state = EdgeState.NORMAL;
  
  @Pure
  public List<Vector3f> getPoints() {
    return this.points;
  }
  
  @Pure
  public float getPipeSize() {
    return this.pipeSize;
  }
  
  public void setPipeSize(final float pipeSize) {
    this.pipeSize = pipeSize;
  }
  
  @Pure
  public EdgeState getState() {
    return this.state;
  }
  
  public void setState(final EdgeState state) {
    this.state = state;
  }
}
