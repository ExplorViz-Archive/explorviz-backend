package net.explorviz.common.live_trace_processing.record.event.constructor;

import java.nio.ByteBuffer;
import net.explorviz.common.live_trace_processing.IdNotAvailableException;
import net.explorviz.common.live_trace_processing.StringRegistryReceiver;
import net.explorviz.common.live_trace_processing.StringRegistrySender;
import net.explorviz.common.live_trace_processing.record.IRecord;
import net.explorviz.common.live_trace_processing.record.event.AbstractAfterEventRecord;
import net.explorviz.common.live_trace_processing.record.trace.HostApplicationMetaDataRecord;
import net.explorviz.common.live_trace_processing.writer.IRecordSender;

public class AfterConstructorEventRecord extends AbstractAfterEventRecord {
  public static final byte CLAZZ_ID = 9;
  public static final byte CLAZZ_ID_FROM_WORKER = CLAZZ_ID + 64;

  public AfterConstructorEventRecord(final long timestamp, final long traceId, final int orderIndex,
      final HostApplicationMetaDataRecord hostApplicationMetadata) {
    super(timestamp, traceId, orderIndex, hostApplicationMetadata);
  }

  public AfterConstructorEventRecord(final ByteBuffer buffer,
      final StringRegistryReceiver stringRegistry) throws IdNotAvailableException {
    super(buffer, stringRegistry);
  }

  @Override
  public void putIntoByteBuffer(final ByteBuffer buffer, final StringRegistrySender stringRegistry,
      final IRecordSender writer) {
    buffer.put(CLAZZ_ID_FROM_WORKER);
    buffer.putInt(this.getRecordSizeInBytes());
    super.putIntoByteBuffer(buffer, stringRegistry, writer);
  }

  @Override
  public int compareTo(final IRecord o) {
    if (o instanceof AfterConstructorEventRecord) {
      return super.compareTo(o);
    }
    return -1;
  }

  @Override
  public String toString() {
    return AfterConstructorEventRecord.class.getSimpleName() + " - " + super.toString();
  }
}