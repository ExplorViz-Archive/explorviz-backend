package net.explorviz.common.live_trace_processing.record.event.statics;

import java.nio.ByteBuffer;
import net.explorviz.common.live_trace_processing.IdNotAvailableException;
import net.explorviz.common.live_trace_processing.StringRegistryReceiver;
import net.explorviz.common.live_trace_processing.StringRegistrySender;
import net.explorviz.common.live_trace_processing.record.IRecord;
import net.explorviz.common.live_trace_processing.record.event.AbstractBeforeOperationEventRecord;
import net.explorviz.common.live_trace_processing.record.trace.HostApplicationMetaDataRecord;
import net.explorviz.common.live_trace_processing.writer.IRecordSender;

public class BeforeStaticOperationEventRecord extends AbstractBeforeOperationEventRecord {
  public static final byte CLAZZ_ID = 11;
  public static final byte CLAZZ_ID_FROM_WORKER = CLAZZ_ID + 64;

  public static final int COMPRESSED_BYTE_LENGTH =
      AbstractBeforeOperationEventRecord.COMPRESSED_BYTE_LENGTH - 4; // objectId
  public static final int COMPRESSED_BYTE_LENGTH_WITH_CLAZZ_ID = 1 + COMPRESSED_BYTE_LENGTH;

  public BeforeStaticOperationEventRecord(final long traceId, final int orderIndex,
      final String operationSignature, final String clazz, final String implementedInterface,
      final HostApplicationMetaDataRecord hostApplicationMetadata) {
    super(traceId, orderIndex, 0, operationSignature, clazz, implementedInterface,
        hostApplicationMetadata);
  }

  public BeforeStaticOperationEventRecord(final ByteBuffer buffer,
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
    if (o instanceof BeforeStaticOperationEventRecord) {
      return super.compareTo(o);
    }
    return -1;
  }

  @Override
  public String toString() {
    return BeforeStaticOperationEventRecord.class.getSimpleName() + " - " + super.toString();
  }
}
