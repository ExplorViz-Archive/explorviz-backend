package net.explorviz.common.live_trace_processing.record.trace;

import java.util.List;
import net.explorviz.common.live_trace_processing.record.IRecord;
import net.explorviz.common.live_trace_processing.record.event.AbstractEventRecord;

public final class Trace implements IRecord {
	private final List<AbstractEventRecord> traceEvents;
	private final boolean valid;
	private boolean containsRemoteRecord;

	public Trace(final List<AbstractEventRecord> traceEvents,
			final boolean valid, final boolean containsRemoteRecord) {
		this.traceEvents = traceEvents;
		this.valid = valid;
		this.containsRemoteRecord = containsRemoteRecord;
	}

	public List<AbstractEventRecord> getTraceEvents() {
		return traceEvents;
	}

	public boolean isValid() {
		return valid;
	}

	public boolean containsRemoteRecord() {
		return containsRemoteRecord;
	}

	public void setContainsRemoteRecord(final boolean containsRemoteRecord) {
		this.containsRemoteRecord = containsRemoteRecord;
	}

	@Override
	public int compareTo(final IRecord o) {
		final List<AbstractEventRecord> recordsT1 = getTraceEvents();
		if (o instanceof Trace) {
			final Trace otherTrace = (Trace) o;
			final List<AbstractEventRecord> recordsT2 = otherTrace
					.getTraceEvents();

			if (recordsT1.size() - recordsT2.size() != 0) {
				return recordsT1.size() - recordsT2.size();
			}

			for (int i = 0; i < recordsT1.size(); i++) {
				final int cmpRecord = recordsT1.get(i).compareTo(
						recordsT2.get(i));
				if (cmpRecord != 0) {
					return cmpRecord;
				}
			}
			return 0;
		}
		return -1;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder(64);
		sb.append("\n\tTrace: ");
		for (final AbstractEventRecord traceEvent : traceEvents) {
			sb.append("\n\t");
			sb.append(traceEvent);
		}
		return sb.toString();
	}
}
