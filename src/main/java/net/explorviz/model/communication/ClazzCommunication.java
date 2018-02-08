package net.explorviz.model.communication;

import java.util.HashMap;
import java.util.Map;

import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;

import net.explorviz.model.Clazz;
import net.explorviz.model.RuntimeInformation;
import net.explorviz.model.helper.BaseEntity;

@SuppressWarnings("serial")
@Type("clazzcommunication")
public class ClazzCommunication extends BaseEntity {

	private int requestsCacheCount = 0;
	private String methodName;
	private final Map<Long, RuntimeInformation> traceIdToRuntimeMap = new HashMap<Long, RuntimeInformation>();

	@Relationship("sourceClazz")
	private Clazz sourceClazz;

	@Relationship("targetClazz")
	private Clazz targetClazz;

	private final boolean hidden = false;

	public int getRequestsCacheCount() {
		return requestsCacheCount;
	}

	public void setRequestsCacheCount(final int requestsCacheCount) {
		this.requestsCacheCount = requestsCacheCount;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(final String methodName) {
		this.methodName = methodName;
	}

	public Clazz getSourceClazz() {
		return sourceClazz;
	}

	public void setSourceClazz(final Clazz sourceClazz) {
		this.sourceClazz = sourceClazz;
	}

	public Clazz getTargetClazz() {
		return targetClazz;
	}

	public void setTargetClazz(final Clazz targetClazz) {
		this.targetClazz = targetClazz;
	}

	public Map<Long, RuntimeInformation> getTraceIdToRuntimeMap() {
		return traceIdToRuntimeMap;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void addRuntimeInformation(final Long traceId, final int calledTimes, final int orderIndex,
			final int requests, final float averageResponseTime, final float overallTraceDuration) {
		RuntimeInformation runtime = traceIdToRuntimeMap.get(traceId);

		if (runtime == null) {
			runtime = new RuntimeInformation();
			runtime.setCalledTimes(calledTimes);
			runtime.getOrderIndexes().add(orderIndex);
			runtime.setRequests(requests);
			runtime.setOverallTraceDuration(overallTraceDuration);
			runtime.setAverageResponseTime(averageResponseTime);

			traceIdToRuntimeMap.put(traceId, runtime);
			requestsCacheCount += requests;
			return;
		}

		final float beforeSum = runtime.getRequests() * runtime.getAverageResponseTimeInNanoSec();
		final float currentSum = requests * averageResponseTime;

		runtime.setAverageResponseTime((beforeSum + currentSum) / (runtime.getRequests() + requests));
		runtime.setRequests(runtime.getRequests() + requests);
		runtime.setOverallTraceDuration((overallTraceDuration + runtime.getOverallTraceDuration()) / 2f);
		runtime.getOrderIndexes().add(orderIndex);
		requestsCacheCount += requests;
	}

	public void reset() {
		requestsCacheCount = 0;
		traceIdToRuntimeMap.clear();
	}

}