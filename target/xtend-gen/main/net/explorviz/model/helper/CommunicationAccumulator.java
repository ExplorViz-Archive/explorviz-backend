package net.explorviz.model.helper;

import java.util.ArrayList;
import java.util.List;
import net.explorviz.model.helper.CommunicationTileAccumulator;
import net.explorviz.model.helper.DrawEdgeEntity;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.xbase.lib.Pure;

@SuppressWarnings("all")
public class CommunicationAccumulator extends DrawEdgeEntity {
  @Accessors
  private final transient List<CommunicationTileAccumulator> tiles = new ArrayList<CommunicationTileAccumulator>();
  
  @Pure
  public List<CommunicationTileAccumulator> getTiles() {
    return this.tiles;
  }
}
