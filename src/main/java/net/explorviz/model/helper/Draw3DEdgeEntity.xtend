package net.explorviz.model.helper;

import java.util.ArrayList
import java.util.List
import org.eclipse.xtend.lib.annotations.Accessors
import net.explorviz.math.Vector3f

abstract class Draw3DEdgeEntity extends BaseEntity{
	@Accessors transient val List<Vector3f> points = new ArrayList<Vector3f>
	@Accessors transient var float pipeSize

	@Accessors var EdgeState state = EdgeState.NORMAL

}

enum EdgeState {
	NORMAL, TRANSPARENT, SHOW_DIRECTION_IN, SHOW_DIRECTION_OUT, SHOW_DIRECTION_IN_AND_OUT, REPLAY_HIGHLIGHT, HIDDEN
}