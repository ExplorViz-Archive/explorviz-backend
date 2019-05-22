package net.explorviz.analysis.live_trace_processing.filter.reduction;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import net.explorviz.common.live_trace_processing.record.event.AbstractBeforeEventRecord;
import net.explorviz.common.live_trace_processing.record.event.AbstractBeforeOperationEventRecord;
import net.explorviz.common.live_trace_processing.record.event.AbstractEventRecord;
import net.explorviz.common.live_trace_processing.record.trace.HostApplicationMetaDataRecord;
import net.explorviz.common.live_trace_processing.record.trace.RuntimeStatisticInformation;
import net.explorviz.common.live_trace_processing.record.trace.Trace;

class TracesSummarizationBuffer {
  private Trace accumulator;
  private final long bufferCreatedTimestamp;

  public TracesSummarizationBuffer(final long bufferCreatedTimestamp) {
    this.bufferCreatedTimestamp = bufferCreatedTimestamp;
  }

  public long getBufferCreatedTimestamp() {
    return this.bufferCreatedTimestamp;
  }

  public Trace getAggregatedTrace() {
    return this.accumulator;
  }

  public void insertTrace(final Trace trace) {
    if (this.accumulator == null) {
      this.accumulator = trace;
    } else {
      final List<AbstractEventRecord> aggregatedRecords = this.accumulator.getTraceEvents();
      final List<AbstractEventRecord> records = trace.getTraceEvents();

      for (int i = 0; i < aggregatedRecords.size(); i++) {
        final AbstractEventRecord event = aggregatedRecords.get(i);
        final AbstractEventRecord event2 = records.get(i);

        final List<HostApplicationMetaDataRecord> hostMetaList1 =
            event.getHostApplicationMetadataList();
        final List<HostApplicationMetaDataRecord> hostMetaList2 =
            event2.getHostApplicationMetadataList();

        if (event instanceof AbstractBeforeEventRecord) {
          final AbstractBeforeEventRecord abstractBeforeEventRecord =
              (AbstractBeforeEventRecord) event;
          final AbstractBeforeEventRecord abstractBeforeEventRecord2 =
              (AbstractBeforeEventRecord) event2;

          final List<RuntimeStatisticInformation> runtimeList1 =
              abstractBeforeEventRecord.getRuntimeStatisticInformationList();
          final List<RuntimeStatisticInformation> runtimeList2 =
              abstractBeforeEventRecord2.getRuntimeStatisticInformationList();

          final HashMap<HostApplicationMetaDataRecord, RuntimeStatisticInformation> toAdd =
              new HashMap<>();

          for (int j = 0; j < hostMetaList2.size(); j++) {
            final HostApplicationMetaDataRecord host2 = hostMetaList2.get(j);
            final int indexInHostList1 = this.indexOfHost(host2, hostMetaList1);
            if (indexInHostList1 >= 0) {
              // found, so merge the entries
              final RuntimeStatisticInformation runtime1 = runtimeList1.get(indexInHostList1);
              final RuntimeStatisticInformation runtime2 = runtimeList2.get(j);

              if (!runtime2.isInitialized()) {
                runtime1.merge(runtime2, this.getRightObjectId(abstractBeforeEventRecord2));
              } else {
                runtime1.merge(runtime2);
              }
            } else {
              // not found, so insert later
              toAdd.put(host2, runtimeList2.get(j));
            }
          }

          for (final Entry<HostApplicationMetaDataRecord, RuntimeStatisticInformation> entry : toAdd
              .entrySet()) {
            if (entry.getKey() != null) {
              hostMetaList1.add(entry.getKey());
              final RuntimeStatisticInformation runtime2 = entry.getValue();
              if (!runtime2.isInitialized()) {
                runtime2.makeAccumulator(this.getRightObjectId(abstractBeforeEventRecord2));
              }
              runtimeList1.add(runtime2);
            }
          }
        } else {
          for (int j = 0; j < hostMetaList2.size(); j++) {
            final HostApplicationMetaDataRecord host2 = hostMetaList2.get(j);
            final int indexInHostList1 = this.indexOfHost(host2, hostMetaList1);
            if (indexInHostList1 == -1 && host2 != null) {
              // not found, so insert
              hostMetaList1.add(host2);
            }
          }
        }
      }
    }
  }

  private int indexOfHost(final HostApplicationMetaDataRecord hostToSeek,
      final List<HostApplicationMetaDataRecord> hostMetaList) {
    for (int i = 0; i < hostMetaList.size(); i++) {
      if (hostMetaList.get(i).equals(hostToSeek)) {
        return i;
      }
    }
    return -1;
  }

  private int getRightObjectId(final AbstractBeforeEventRecord abstractBeforeEventRecord2) {
    int objectId = 0;

    if (abstractBeforeEventRecord2 instanceof AbstractBeforeOperationEventRecord) {
      objectId = ((AbstractBeforeOperationEventRecord) abstractBeforeEventRecord2).getObjectId();
    }
    return objectId;
  }
}
