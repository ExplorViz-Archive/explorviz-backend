package net.explorviz.analysis.live_trace_processing.filter.reduction;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.TreeMap;
import net.explorviz.common.live_trace_processing.filter.AbstractFilter;
import net.explorviz.common.live_trace_processing.filter.SinglePipeConnector;
import net.explorviz.common.live_trace_processing.reader.TimeProvider;
import net.explorviz.common.live_trace_processing.record.IRecord;
import net.explorviz.common.live_trace_processing.record.event.AbstractBeforeEventRecord;
import net.explorviz.common.live_trace_processing.record.event.AbstractBeforeOperationEventRecord;
import net.explorviz.common.live_trace_processing.record.event.AbstractEventRecord;
import net.explorviz.common.live_trace_processing.record.misc.TerminateRecord;
import net.explorviz.common.live_trace_processing.record.misc.TimedPeriodRecord;
import net.explorviz.common.live_trace_processing.record.trace.RuntimeStatisticInformation;
import net.explorviz.common.live_trace_processing.record.trace.Trace;
import net.explorviz.common.live_trace_processing.record.trace.TraceComperator;

public class TracesSummarizationFilter extends AbstractFilter {
  // private static final String INVALID_TRACE = "invalid trace...";

  private final long maxCollectionDuration;

  private final Map<Trace, TracesSummarizationBuffer> trace2buffer =
      new TreeMap<>(new TraceComperator());

  private final SinglePipeConnector<IRecord> pipeConnector;

  public TracesSummarizationFilter(final SinglePipeConnector<IRecord> pipeConnector,
      final long maxCollectionDuration, final Queue<IRecord> sinkReceiver) {
    super(sinkReceiver, "Reduced traces/sec", 1000);
    this.pipeConnector = pipeConnector;

    this.maxCollectionDuration = maxCollectionDuration;
  }

  @Override
  public void run() {
    this.pipeConnector.process(this);
  }

  @Override
  public void processRecord(final IRecord record) {
    if (record instanceof Trace) {
      final Trace trace = (Trace) record;

      if (trace.isValid() && !trace.containsRemoteRecord()) {
        this.insertIntoBuffer(trace);
      } else {
        // trace with remote records or invalid trace cant be reduced
        // System.out.println(INVALID_TRACE);
        // System.out.println("invalid trace... trace:" +
        // trace.toString());
        this.makeTraceElementsAccumulator(trace);
        this.deliver(trace);
      }
    } else if (record instanceof TimedPeriodRecord) {
      this.processTimeoutQueue(TimeProvider.getCurrentTimestamp());
      this.deliver(record);
    } else if (record instanceof TerminateRecord) {
      this.terminate();
      this.deliver(record);
    } else {
      this.deliver(record);
    }
  }

  private void insertIntoBuffer(final Trace trace) {
    TracesSummarizationBuffer traceAggregationBuffer = this.trace2buffer.get(trace);
    if (traceAggregationBuffer == null) {
      this.makeTraceElementsAccumulator(trace);

      traceAggregationBuffer = new TracesSummarizationBuffer(TimeProvider.getCurrentTimestamp());
      this.trace2buffer.put(trace, traceAggregationBuffer);
    }
    traceAggregationBuffer.insertTrace(trace);
  }

  private void makeTraceElementsAccumulator(final Trace trace) {
    final List<AbstractEventRecord> traceEvents = trace.getTraceEvents();

    for (final AbstractEventRecord event : traceEvents) {
      if (event instanceof AbstractBeforeEventRecord) {
        final AbstractBeforeEventRecord abstractBeforeEventRecord =
            (AbstractBeforeEventRecord) event;

        final List<RuntimeStatisticInformation> runtimeStatisticInformationList =
            abstractBeforeEventRecord.getRuntimeStatisticInformationList();
        if (runtimeStatisticInformationList.size() == 1) {
          final RuntimeStatisticInformation runtimeStatisticInformation =
              runtimeStatisticInformationList.get(0);
          if (!runtimeStatisticInformation.isInitialized()) {
            int objectId = 0;

            if (abstractBeforeEventRecord instanceof AbstractBeforeOperationEventRecord) {
              final AbstractBeforeOperationEventRecord abstractBeforeOperationEventRecord =
                  (AbstractBeforeOperationEventRecord) abstractBeforeEventRecord;
              objectId = abstractBeforeOperationEventRecord.getObjectId();
            }

            runtimeStatisticInformation.makeAccumulator(objectId);
          }
        }
      }
    }
  }

  private void processTimeoutQueue(final long timestamp) {
    final long bufferTimeout = timestamp - this.maxCollectionDuration;
    final Iterator<Entry<Trace, TracesSummarizationBuffer>> iter =
        this.trace2buffer.entrySet().iterator();

    while (iter.hasNext()) {
      final Entry<Trace, TracesSummarizationBuffer> traceBufferEntry = iter.next();

      if (traceBufferEntry.getValue().getBufferCreatedTimestamp() <= bufferTimeout) {
        final Trace aggregatedTrace = traceBufferEntry.getValue().getAggregatedTrace();
        this.deliver(aggregatedTrace);
        iter.remove();
      }
    }
  }

  private void terminate() {
    for (final TracesSummarizationBuffer traceBuffer : this.trace2buffer.values()) {
      this.deliver(traceBuffer.getAggregatedTrace());
    }
    this.trace2buffer.clear();
  }
}
