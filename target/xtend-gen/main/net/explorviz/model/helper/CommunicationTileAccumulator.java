package net.explorviz.model.helper;

import java.util.ArrayList;
import java.util.List;
import net.explorviz.model.Communication;
import net.explorviz.model.helper.DrawEdgeEntity;
import net.explorviz.model.helper.Point;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.xbase.lib.Pure;

@SuppressWarnings("all")
public class CommunicationTileAccumulator extends DrawEdgeEntity {
  @Accessors
  private int requestsCache;
  
  @Accessors
  private final transient List<Communication> communications = new ArrayList<Communication>(4);
  
  @Accessors
  private transient boolean alreadyDrawn = false;
  
  @Accessors
  private Point startPoint;
  
  @Accessors
  private Point endPoint;
  
  @Pure
  public int getRequestsCache() {
    return this.requestsCache;
  }
  
  public void setRequestsCache(final int requestsCache) {
    this.requestsCache = requestsCache;
  }
  
  @Pure
  public List<Communication> getCommunications() {
    return this.communications;
  }
  
  @Pure
  public boolean isAlreadyDrawn() {
    return this.alreadyDrawn;
  }
  
  public void setAlreadyDrawn(final boolean alreadyDrawn) {
    this.alreadyDrawn = alreadyDrawn;
  }
  
  @Pure
  public Point getStartPoint() {
    return this.startPoint;
  }
  
  public void setStartPoint(final Point startPoint) {
    this.startPoint = startPoint;
  }
  
  @Pure
  public Point getEndPoint() {
    return this.endPoint;
  }
  
  public void setEndPoint(final Point endPoint) {
    this.endPoint = endPoint;
  }
}
