package net.explorviz.model.helper;

import java.util.ArrayList
import java.util.List
import org.eclipse.xtend.lib.annotations.Accessors

class CommunicationAccumulator extends DrawEdgeEntity {
	@Accessors val transient List<CommunicationTileAccumulator> tiles = new ArrayList<CommunicationTileAccumulator>

}
