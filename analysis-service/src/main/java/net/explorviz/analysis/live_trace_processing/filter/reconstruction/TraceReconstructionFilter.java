package net.explorviz.analysis.live_trace_processing.filter.reconstruction;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.TreeMap;
import net.explorviz.common.live_trace_processing.filter.AbstractFilter;
import net.explorviz.common.live_trace_processing.filter.PipesMerger;
import net.explorviz.common.live_trace_processing.reader.TimeProvider;
import net.explorviz.common.live_trace_processing.record.IRecord;
import net.explorviz.common.live_trace_processing.record.event.AbstractEventRecord;
import net.explorviz.common.live_trace_processing.record.misc.TerminateRecord;
import net.explorviz.common.live_trace_processing.record.misc.TimedPeriodRecord;
import net.explorviz.common.live_trace_processing.record.trace.HostApplicationMetaDataRecord;
import net.explorviz.common.live_trace_processing.record.trace.Trace;

public final class TraceReconstructionFilter extends AbstractFilter implements Runnable {
  private final Map<AbstractEventRecord, TraceReconstructionBuffer> traceIdAndHost2trace =
      new TreeMap<>(new TraceIdAndHostComperator());

  private final PipesMerger<IRecord> traceReconstructionMerger;

  private final int traceTimeoutInSec;

  public TraceReconstructionFilter(final PipesMerger<IRecord> traceReconstructionMerger,
      final Queue<IRecord> receiverQueue, final int traceTimeoutInSec) {
    super(receiverQueue, "Reconstructed traces/sec", 1000);
    this.traceReconstructionMerger = traceReconstructionMerger;
    this.traceTimeoutInSec = traceTimeoutInSec;
  }

  @Override
  public void run() {
    this.traceReconstructionMerger.process(this);
  }

  @Override
  public final void processRecord(final IRecord record) {
    if (record instanceof AbstractEventRecord) {
      final AbstractEventRecord abstractEvent = (AbstractEventRecord) record;

      final TraceReconstructionBuffer traceBuffer = this.getBufferForRecord(abstractEvent);
      traceBuffer.insertEvent(abstractEvent);

      if (traceBuffer.isFinished()) {
        final Trace trace = traceBuffer.toTrace(true);
        this.traceIdAndHost2trace.remove(trace.getTraceEvents().get(0));
        this.deliver(traceBuffer.toTrace(true));
      }
    } else if (record instanceof TimedPeriodRecord) {
      this.checkForTimeouts(TimeProvider.getCurrentTimestamp());
      this.deliver(record);
    } else if (record instanceof TerminateRecord) {
      this.terminate();
      this.deliver(record);
    } else {
      this.deliver(record);
    }
  }

  private final TraceReconstructionBuffer getBufferForRecord(final AbstractEventRecord record) {
    TraceReconstructionBuffer traceBuffer = this.traceIdAndHost2trace.get(record);
    if (traceBuffer == null) {
      traceBuffer = new TraceReconstructionBuffer(this.traceTimeoutInSec);
      this.traceIdAndHost2trace.put(record, traceBuffer);
    }
    return traceBuffer;
  }

  private void checkForTimeouts(final long timestamp) {
    final Iterator<Entry<AbstractEventRecord, TraceReconstructionBuffer>> iterator =
        this.traceIdAndHost2trace.entrySet().iterator();

    while (iterator.hasNext()) {
      final Entry<AbstractEventRecord, TraceReconstructionBuffer> entry = iterator.next();
      final TraceReconstructionBuffer traceBuffer = entry.getValue();
      if (!traceBuffer.isTimedout()) {
        traceBuffer.decreaseTimeoutCounter();
      } else {
        this.deliver(traceBuffer.toTrace(false));
        iterator.remove();
      }
    }
  }

  private void terminate() {
    for (final TraceReconstructionBuffer entry : this.traceIdAndHost2trace.values()) {
      this.deliver(entry.toTrace(false));
    }
    this.traceIdAndHost2trace.clear();
  }
}


class TraceIdAndHostComperator implements Comparator<AbstractEventRecord> {
  @Override
  public int compare(final AbstractEventRecord o1, final AbstractEventRecord o2) {

    final long cmpTraceId = o1.getTraceId() - o2.getTraceId();
    if (cmpTraceId != 0) {
      return (int) cmpTraceId;
    }

    final int cmpHostLength =
        o1.getHostApplicationMetadataList().size() - o2.getHostApplicationMetadataList().size();
    if (cmpHostLength != 0) {
      return cmpHostLength;
    }

    for (final HostApplicationMetaDataRecord hostMeta1 : o1.getHostApplicationMetadataList()) {
      boolean foundMatch = false;
      for (final HostApplicationMetaDataRecord hostMeta2 : o2.getHostApplicationMetadataList()) {
        if (hostMeta1.equals(hostMeta2)) {
          foundMatch = true;
          break;
        }
      }
      if (!foundMatch) {
        return -1;
      }
    }

    return 0;
  }
}
