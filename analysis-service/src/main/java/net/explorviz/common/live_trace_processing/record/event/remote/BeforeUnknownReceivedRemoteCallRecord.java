package net.explorviz.common.live_trace_processing.record.event.remote;

import java.nio.ByteBuffer;
import net.explorviz.common.live_trace_processing.IdNotAvailableException;
import net.explorviz.common.live_trace_processing.StringRegistryReceiver;
import net.explorviz.common.live_trace_processing.StringRegistrySender;
import net.explorviz.common.live_trace_processing.record.IRecord;
import net.explorviz.common.live_trace_processing.record.event.AbstractBeforeEventRecord;
import net.explorviz.common.live_trace_processing.record.trace.HostApplicationMetaDataRecord;
import net.explorviz.common.live_trace_processing.writer.IRecordSender;

public class BeforeUnknownReceivedRemoteCallRecord extends AbstractBeforeEventRecord {
  public static final byte CLAZZ_ID = 16;
  public static final byte CLAZZ_ID_FROM_WORKER = CLAZZ_ID + 64;

  public static final int COMPRESSED_BYTE_LENGTH =
      AbstractBeforeEventRecord.COMPRESSED_BYTE_LENGTH + 4 + 4;
  public static final int COMPRESSED_BYTE_LENGTH_WITH_CLAZZ_ID = 1 + COMPRESSED_BYTE_LENGTH;

  public static final int BYTE_LENGTH = AbstractBeforeEventRecord.BYTE_LENGTH + 4 + 4;
  protected static final int BYTE_LENGTH_WITH_CLAZZ_ID = 1 + BYTE_LENGTH;

  private final String sender;
  private final String destination;

  public BeforeUnknownReceivedRemoteCallRecord(final String sender, final String destination,
      final long traceId, final int orderIndex, final HostApplicationMetaDataRecord thisHost) {
    super(traceId, orderIndex, thisHost);
    this.sender = sender;
    this.destination = destination;
  }

  public BeforeUnknownReceivedRemoteCallRecord(final ByteBuffer buffer,
      final StringRegistryReceiver stringRegistry) throws IdNotAvailableException {
    super(buffer, stringRegistry);

    this.sender = stringRegistry.getStringFromId(buffer.getInt());
    this.destination = stringRegistry.getStringFromId(buffer.getInt());
  }

  public String getSender() {
    return this.sender;
  }

  public String getDestination() {
    return this.destination;
  }

  @Override
  public int getRecordSizeInBytes() {
    return BYTE_LENGTH_WITH_CLAZZ_ID;
  }

  @Override
  public void putIntoByteBuffer(final ByteBuffer buffer, final StringRegistrySender stringRegistry,
      final IRecordSender writer) {
    buffer.put(CLAZZ_ID_FROM_WORKER);
    buffer.putInt(this.getRecordSizeInBytes());
    super.putIntoByteBuffer(buffer, stringRegistry, writer);

    buffer.putInt(stringRegistry.getIdForString(this.sender));
    buffer.putInt(stringRegistry.getIdForString(this.destination));
  }

  @Override
  public int compareTo(final IRecord o) {
    if (o instanceof BeforeUnknownReceivedRemoteCallRecord) {
      final BeforeUnknownReceivedRemoteCallRecord record2 =
          (BeforeUnknownReceivedRemoteCallRecord) o;
      final int cmpSender = this.getSender().compareTo(record2.getSender());
      if (cmpSender != 0) {
        return cmpSender;
      }

      final int cmpDestination = this.getDestination().compareTo(record2.getDestination());
      if (cmpDestination != 0) {
        return cmpDestination;
      }

      return super.compareTo(o);
    }
    return -1;
  }

  @Override
  public String toString() {
    return BeforeSentRemoteCallRecord.class.getSimpleName() + " - " + super.toString()
        + ", getSender()=" + this.getSender() + ", getDestination()=" + this.getDestination();
  }
}
