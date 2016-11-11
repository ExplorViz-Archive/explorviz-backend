package net.explorviz.model.helper;

import net.explorviz.model.helper.BaseEntity;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.xbase.lib.Pure;

@SuppressWarnings("all")
public class Point extends BaseEntity {
  @Accessors
  private float x;
  
  @Accessors
  private float y;
  
  private final float DELTA = 0.01f;
  
  public boolean equals(final Point other) {
    boolean _and = false;
    float _abs = Math.abs((other.x - this.x));
    boolean _lessEqualsThan = (_abs <= this.DELTA);
    if (!_lessEqualsThan) {
      _and = false;
    } else {
      float _abs_1 = Math.abs((other.y - this.y));
      boolean _lessEqualsThan_1 = (_abs_1 <= this.DELTA);
      _and = _lessEqualsThan_1;
    }
    return _and;
  }
  
  public Point sub(final Point other) {
    Point _xblockexpression = null;
    {
      final Point point = new Point();
      point.x = (this.x - other.x);
      point.y = (this.y - other.y);
      _xblockexpression = point;
    }
    return _xblockexpression;
  }
  
  public Point add(final Point other) {
    Point _xblockexpression = null;
    {
      final Point point = new Point();
      point.x = (this.x + other.x);
      point.y = (this.y + other.y);
      _xblockexpression = point;
    }
    return _xblockexpression;
  }
  
  @Pure
  public float getX() {
    return this.x;
  }
  
  public void setX(final float x) {
    this.x = x;
  }
  
  @Pure
  public float getY() {
    return this.y;
  }
  
  public void setY(final float y) {
    this.y = y;
  }
}
