package net.explorviz.model.application;

import java.util.HashSet;
import java.util.Set;

import com.github.jasminb.jsonapi.annotations.Type;

import net.explorviz.model.helper.BaseEntity;

/**
 * Model representing detailed runtime information for
 * {@link ClazzCommunication} between two {@link Clazz}
 *
 * @author Christian Zirkelbach (czi@informatik.uni-kiel.de)
 *
 */
@SuppressWarnings("serial")
@Type("runtimeinformation")
public class RuntimeInformation extends BaseEntity {

	private int calledTimes;
	private float overallTraceDuration;
	private int requests;
	private float averageResponseTime;
	private final Set<Integer> orderIndexes = new HashSet<Integer>();

	public int getCalledTimes() {
		return calledTimes;
	}

	public void setCalledTimes(final int calledTimes) {
		this.calledTimes = calledTimes;
	}

	public float getOverallTraceDuration() {
		return overallTraceDuration;
	}

	public void setOverallTraceDuration(final float overallTraceDuration) {
		this.overallTraceDuration = overallTraceDuration;
	}

	public int getRequests() {
		return requests;
	}

	public void setRequests(final int requests) {
		this.requests = requests;
	}

	public float getAverageResponseTimeInNanoSec() {
		return averageResponseTime;
	}

	public void setAverageResponseTime(final float averageResponseTime) {
		this.averageResponseTime = averageResponseTime;
	}

	public Set<Integer> getOrderIndexes() {
		return orderIndexes;
	}

}