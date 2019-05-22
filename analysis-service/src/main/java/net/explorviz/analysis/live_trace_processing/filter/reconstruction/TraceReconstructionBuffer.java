package net.explorviz.analysis.live_trace_processing.filter.reconstruction;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import net.explorviz.common.live_trace_processing.Constants;
import net.explorviz.common.live_trace_processing.record.event.AbstractAfterEventRecord;
import net.explorviz.common.live_trace_processing.record.event.AbstractAfterFailedEventRecord;
import net.explorviz.common.live_trace_processing.record.event.AbstractBeforeEventRecord;
import net.explorviz.common.live_trace_processing.record.event.AbstractEventRecord;
import net.explorviz.common.live_trace_processing.record.event.remote.BeforeReceivedRemoteCallRecord;
import net.explorviz.common.live_trace_processing.record.event.remote.BeforeSentRemoteCallRecord;
import net.explorviz.common.live_trace_processing.record.trace.RuntimeStatisticInformation;
import net.explorviz.common.live_trace_processing.record.trace.Trace;

class TraceReconstructionBuffer {
	private final List<AbstractEventRecord> events = new ArrayList<AbstractEventRecord>(
			Constants.TRACE_RECONSTRUCTION_BUFFER_INITIAL_SIZE);

	private int openEvents;

	private final int TIMEOUT_IN_SECONDS;
	private int updatedInThisPeriodCounter;

	private int maxOrderIndex = -1;

	public TraceReconstructionBuffer(final int traceTimeoutInSec) {
		TIMEOUT_IN_SECONDS = traceTimeoutInSec;
		updatedInThisPeriodCounter = TIMEOUT_IN_SECONDS;
	}

	public final void insertEvent(final AbstractEventRecord event) {
		resetTimeoutCounter();
		setMaxOrderIndex(event);

		if ((event instanceof AbstractBeforeEventRecord)) {
			openEvents++;
			final AbstractBeforeEventRecord beforeEvent = (AbstractBeforeEventRecord) event;
			if (beforeEvent.getRuntimeStatisticInformationList() == null) {
				final ArrayList<RuntimeStatisticInformation> runtime = new ArrayList<RuntimeStatisticInformation>(
						1);
				runtime.add(new RuntimeStatisticInformation(1, -1, -1));
				beforeEvent.setRuntimeStatisticInformationList(runtime);
			}
		} else if ((event instanceof AbstractAfterFailedEventRecord)
				|| (event instanceof AbstractAfterEventRecord)) {
			openEvents--;
		}

		events.add(event);
	}

	public boolean isTimedout() {
		return updatedInThisPeriodCounter <= 0;
	}

	public void decreaseTimeoutCounter() {
		updatedInThisPeriodCounter--;
	}

	public void resetTimeoutCounter() {
		updatedInThisPeriodCounter = TIMEOUT_IN_SECONDS;
	}

	private final int setMaxOrderIndex(final AbstractEventRecord event) {
		final int orderIndex = event.getOrderIndex();
		if (orderIndex > maxOrderIndex) {
			maxOrderIndex = orderIndex;
		}
		return orderIndex;
	}

	public final boolean isFinished() {
		return !isInvalid();
	}

	public final boolean isInvalid() {
		return ((openEvents != 0) || events.isEmpty() || ((maxOrderIndex + 1) != events.size()));
	}

	public final Trace toTrace(final boolean valid) {
		final Stack<AbstractBeforeEventRecord> stack = new Stack<AbstractBeforeEventRecord>();
		boolean containsRemoteRecord = false;

		for (final AbstractEventRecord event : events) {
			if (event instanceof AbstractBeforeEventRecord) {
				final AbstractBeforeEventRecord beforeEvent = (AbstractBeforeEventRecord) event;
				stack.push(beforeEvent);
				if ((event instanceof BeforeReceivedRemoteCallRecord)
						|| (event instanceof BeforeSentRemoteCallRecord)) {
					containsRemoteRecord = true;
				}
			} else if (event instanceof AbstractAfterEventRecord) {
				initRuntimeIfNeccessary(stack,
						((AbstractAfterEventRecord) event).getMethodDuration());
			} else if (event instanceof AbstractAfterFailedEventRecord) {
				initRuntimeIfNeccessary(stack,
						((AbstractAfterFailedEventRecord) event).getMethodDuration());
			}
		}
		return new Trace(new ArrayList<AbstractEventRecord>(events), valid, containsRemoteRecord);
	}

	private void initRuntimeIfNeccessary(final Stack<AbstractBeforeEventRecord> stack,
			final long methodDuration) {
		if (!stack.isEmpty()) {
			final AbstractBeforeEventRecord beforeEvent = stack.pop();

			final List<RuntimeStatisticInformation> runtimeStatisticInformationList = beforeEvent
					.getRuntimeStatisticInformationList();
			if (runtimeStatisticInformationList.size() == 1) { // only on first
				// reconstruction
				final RuntimeStatisticInformation runtimeStatisticInformation = runtimeStatisticInformationList
						.get(0);
				if (!runtimeStatisticInformation.isInitialized()) {
					runtimeStatisticInformation.set(1, methodDuration, methodDuration
							* methodDuration);
				}
			}
		}
	}
}
