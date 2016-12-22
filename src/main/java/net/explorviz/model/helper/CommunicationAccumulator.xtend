package net.explorviz.model.helper;

import java.util.ArrayList
import java.util.List
import org.eclipse.xtend.lib.annotations.Accessors
import net.explorviz.model.Landscape
import com.github.jasminb.jsonapi.annotations.Relationship
import com.github.jasminb.jsonapi.annotations.Type

@Type("communicationaccumulator")
class CommunicationAccumulator extends DrawEdgeEntity {
	@Relationship("tiles")
	@Accessors val transient List<CommunicationTileAccumulator> tiles = new ArrayList<CommunicationTileAccumulator>

	@Relationship("parent")
	@Accessors Landscape parent
}
