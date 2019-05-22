package net.explorviz.analysis.live_trace_processing.filter.counting;

import java.util.Queue;
import net.explorviz.common.live_trace_processing.filter.AbstractFilter;
import net.explorviz.common.live_trace_processing.filter.SinglePipeConnector;
import net.explorviz.common.live_trace_processing.record.IRecord;
import net.explorviz.common.live_trace_processing.record.event.AbstractBeforeOperationEventRecord;
import net.explorviz.common.live_trace_processing.record.event.AbstractEventRecord;
import net.explorviz.common.live_trace_processing.record.misc.TerminateRecord;
import net.explorviz.common.live_trace_processing.record.misc.TimedPeriodRecord;
import net.explorviz.common.live_trace_processing.record.trace.RuntimeStatisticInformation;
import net.explorviz.common.live_trace_processing.record.trace.Trace;

public class RecordCountingFilter extends AbstractFilter {

	private final SinglePipeConnector<IRecord> pipeConnector;
	private final Queue<IRecord> receiver;

	public RecordCountingFilter(final SinglePipeConnector<IRecord> pipeConnector,
			final Queue<IRecord> receiver) {
		super(receiver, "MethodCalls/10 sec", 1000 * 10);
		this.pipeConnector = pipeConnector;
		this.receiver = receiver;
		counter.setEnabled(true);
	}

	@Override
	public void run() {
		pipeConnector.process(this);
	}

	@Override
	public void processRecord(final IRecord record) {
		if (record instanceof Trace) {
			final Trace trace = (Trace) record;
			for (final AbstractEventRecord event : trace.getTraceEvents()) {
				if (event instanceof AbstractBeforeOperationEventRecord) {
					final AbstractBeforeOperationEventRecord abstractBeforeEventRecord = (AbstractBeforeOperationEventRecord) event;

					for (final RuntimeStatisticInformation runtime : abstractBeforeEventRecord
							.getRuntimeStatisticInformationList()) {
						counter.inputObjectsCount(runtime.getCount());
					}
				}
			}
			if (receiver != null) {
				deliver(record);
			}
		} else if (record instanceof AbstractBeforeOperationEventRecord) {
			counter.inputObjectsCount(1);
			if (receiver != null) {
				deliver(record);
			}
		} else if (record instanceof TimedPeriodRecord) {
			if (receiver != null) {
				deliver(record);
			}
		} else if (record instanceof TerminateRecord) {
			terminate();
			if (receiver != null) {
				deliver(record);
			}
		} else {
			if (receiver != null) {
				deliver(record);
			}
		}
	}

	private void terminate() {

	}
}
