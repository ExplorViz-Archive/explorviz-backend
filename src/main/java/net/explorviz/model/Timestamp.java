package net.explorviz.model;

import com.github.jasminb.jsonapi.annotations.Type;

import net.explorviz.model.helper.BaseEntity;

@SuppressWarnings("serial")
@Type("timestamp")
public class Timestamp extends BaseEntity {

	private long calls;

	public Timestamp(final long timestamp, final long calls) {
		this.setTimestamp(timestamp);
		this.setCalls(calls);
	}

	public long getCalls() {
		return calls;
	}

	public void setCalls(final long calls) {
		this.calls = calls;
	}

}