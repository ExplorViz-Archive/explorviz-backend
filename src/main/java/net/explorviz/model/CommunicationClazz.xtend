package net.explorviz.model;

import java.util.HashMap
import java.util.Map
import org.eclipse.xtend.lib.annotations.Accessors
import net.explorviz.model.helper.BaseEntity

class CommunicationClazz extends BaseEntity{
	var requestsCacheCount = 0

	@Accessors String methodName
	@Accessors Map<Long, RuntimeInformation> traceIdToRuntimeMap = new HashMap<Long, RuntimeInformation>

	@Accessors Clazz source
	@Accessors Clazz target
	
	@Accessors boolean hidden = false

	def void addRuntimeInformation(Long traceId, int calledTimes, int orderIndex, int requests, float averageResponseTime, float overallTraceDuration) {
		var runtime = traceIdToRuntimeMap.get(traceId)
		if (runtime == null) {
			runtime = new RuntimeInformation()
			runtime.calledTimes = calledTimes
			runtime.orderIndexes.add(orderIndex)
			runtime.requests = requests
			runtime.overallTraceDurationInNanoSec = overallTraceDuration
			runtime.averageResponseTimeInNanoSec = averageResponseTime

			traceIdToRuntimeMap.put(traceId, runtime)
			requestsCacheCount += requests
			return
		}

		val beforeSum = runtime.requests * runtime.getAverageResponseTimeInNanoSec
		val currentSum = requests * averageResponseTime;

		runtime.averageResponseTimeInNanoSec = (beforeSum + currentSum) / (runtime.requests + requests)
		runtime.requests = runtime.requests + requests
		runtime.overallTraceDurationInNanoSec = (overallTraceDuration + runtime.getOverallTraceDurationInNanoSec) / 2f
		runtime.orderIndexes.add(orderIndex)
		requestsCacheCount += requests
	}

	def void reset() {
		requestsCacheCount = 0
		traceIdToRuntimeMap.clear()
	}

	def int getRequests() {
		requestsCacheCount
	}
}
