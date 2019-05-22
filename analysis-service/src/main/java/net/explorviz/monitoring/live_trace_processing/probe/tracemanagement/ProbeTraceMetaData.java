package net.explorviz.monitoring.live_trace_processing.probe.tracemanagement;


public class ProbeTraceMetaData {
	private long traceId;
	private int nextOrderId;
	private int stackDepth;

	public ProbeTraceMetaData() {
		reset();
	}

	public final long getTraceId() {
		return traceId;
	}

	public final void setTraceId(final long traceId) {
		this.traceId = traceId;
	}

	public final int getNextOrderId() {
		return nextOrderId++;
	}

	public final void reset() {
		nextOrderId = 0;
		stackDepth = 0;
	}

	public final void incrementStackDepth() {
		if (stackDepth == 0) {
			TraceRegistry.registerTrace(this);
		}
		stackDepth++;
	}

	public final void decreaseStackDepthAndEndTraceIfNeccessary() {
		stackDepth--;
		if (stackDepth == 0) {
			TraceRegistry.unregisterTrace(this);
		}
	}
}
