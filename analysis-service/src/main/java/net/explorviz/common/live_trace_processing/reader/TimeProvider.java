package net.explorviz.common.live_trace_processing.reader;

public class TimeProvider {
	public static final long getCurrentTimestamp() {
		// return System.currentTimeMillis();

		return System.nanoTime();
	}
}
