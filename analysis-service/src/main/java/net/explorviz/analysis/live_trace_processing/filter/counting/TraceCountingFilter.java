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

public class TraceCountingFilter extends AbstractFilter {

	private final SinglePipeConnector<IRecord> pipeConnector;

	public TraceCountingFilter(final SinglePipeConnector<IRecord> pipeConnector,
			final Queue<IRecord> receiver) {
		super(receiver, "TraceCalls/10 sec", 1000 * 10);
		this.pipeConnector = pipeConnector;
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
			if (!trace.getTraceEvents().isEmpty()) {
				final AbstractEventRecord abstractEventRecord = trace.getTraceEvents().get(0);
				if (abstractEventRecord instanceof AbstractBeforeOperationEventRecord) {
					final AbstractBeforeOperationEventRecord abstractBeforeOperationEventRecord = (AbstractBeforeOperationEventRecord) abstractEventRecord;
					for (final RuntimeStatisticInformation runtime : abstractBeforeOperationEventRecord
							.getRuntimeStatisticInformationList()) {
						counter.inputObjectsCount(runtime.getCount());
					}
				}
			}
			deliver(record);
		} else if (record instanceof TimedPeriodRecord) {
			deliver(record);
		} else if (record instanceof TerminateRecord) {
			terminate();
			deliver(record);
		} else {
			deliver(record);
		}
	}

	private void terminate() {

	}
}
