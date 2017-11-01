package net.explorviz.model;

import java.util.Set
import java.util.HashSet
import org.eclipse.xtend.lib.annotations.Accessors
import net.explorviz.model.helper.BaseEntity

class RuntimeInformation extends BaseEntity {
	@Accessors int calledTimes
	@Accessors float overallTraceDurationInNanoSec
	@Accessors int requests
	@Accessors float averageResponseTimeInNanoSec

	@Accessors Set<Integer> orderIndexes = new HashSet<Integer>

}
