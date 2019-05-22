package net.explorviz.monitoring.live_trace_processing.probe.distributed;

import java.nio.ByteBuffer;
import net.explorviz.common.live_trace_processing.reader.TimeProvider;
import net.explorviz.common.live_trace_processing.record.event.remote.AfterReceivedRemoteCallRecord;
import net.explorviz.common.live_trace_processing.record.event.remote.AfterSentRemoteCallRecord;
import net.explorviz.common.live_trace_processing.record.event.remote.AfterUnknownReceivedRemoteCallRecord;
import net.explorviz.common.live_trace_processing.record.event.remote.BeforeReceivedRemoteCallRecord;
import net.explorviz.common.live_trace_processing.record.event.remote.BeforeSentRemoteCallRecord;
import net.explorviz.common.live_trace_processing.record.event.remote.BeforeUnknownReceivedRemoteCallRecord;
import net.explorviz.monitoring.live_trace_processing.main.MonitoringController;
import net.explorviz.monitoring.live_trace_processing.main.MonitoringStringRegistry;
import net.explorviz.monitoring.live_trace_processing.probe.AbstractAspect;

public final class DistributedMonitoringRecordWriter {
  public static final String UNKNOWN_DESTINATION = "";
  public static final String UNKNOWN_SENDER = "";

  private static long beforeTime = 0;

  private DistributedMonitoringRecordWriter() {}

  public static void writeBeforeSentRecord(final long ownTraceId, final int ownOrderId,
      final String technology) {
    final ByteBuffer buffer = AbstractAspect.bufferStore.get();

    if (BeforeSentRemoteCallRecord.COMPRESSED_BYTE_LENGTH_WITH_CLAZZ_ID > buffer.remaining()) {
      AbstractAspect.updateLastSendingTimeToCurrent();
      MonitoringController.sendOutBuffer(buffer);
    }

    beforeTime = TimeProvider.getCurrentTimestamp();

    buffer.put(BeforeSentRemoteCallRecord.CLAZZ_ID);
    buffer.putLong(ownTraceId);
    buffer.putInt(ownOrderId);
    buffer.putInt(MonitoringStringRegistry.getIdForString(technology));
  }

  public static void writeAfterSentRecord(final long ownTraceId, final int ownOrderId) {
    final ByteBuffer buffer = AbstractAspect.bufferStore.get();

    if (AfterSentRemoteCallRecord.COMPRESSED_BYTE_LENGTH_WITH_CLAZZ_ID > buffer.remaining()) {
      AbstractAspect.updateLastSendingTimeToCurrent();
      MonitoringController.sendOutBuffer(buffer);
    }

    buffer.put(AfterSentRemoteCallRecord.CLAZZ_ID);
    buffer.putLong(TimeProvider.getCurrentTimestamp() - beforeTime);
    buffer.putLong(ownTraceId);
    buffer.putInt(ownOrderId);
  }

  public static void writeBeforeReceivedRecord(final long ownTraceId, final int ownOrderId,
      final long remoteTraceId, final int remoteOrderId) {
    final ByteBuffer buffer = AbstractAspect.bufferStore.get();

    if (BeforeReceivedRemoteCallRecord.COMPRESSED_BYTE_LENGTH_WITH_CLAZZ_ID > buffer.remaining()) {
      AbstractAspect.updateLastSendingTimeToCurrent();
      MonitoringController.sendOutBuffer(buffer);
    }

    beforeTime = TimeProvider.getCurrentTimestamp();

    buffer.put(BeforeReceivedRemoteCallRecord.CLAZZ_ID);
    buffer.putLong(remoteTraceId);
    buffer.putInt(remoteOrderId);
    buffer.putLong(ownTraceId);
    buffer.putInt(ownOrderId);
  }

  public static void writeAfterReceivedRecord(final long ownTraceId, final int ownOrderId) {
    final ByteBuffer buffer = AbstractAspect.bufferStore.get();

    if (AfterReceivedRemoteCallRecord.COMPRESSED_BYTE_LENGTH_WITH_CLAZZ_ID > buffer.remaining()) {
      AbstractAspect.updateLastSendingTimeToCurrent();
      MonitoringController.sendOutBuffer(buffer);
    }

    buffer.put(AfterReceivedRemoteCallRecord.CLAZZ_ID);
    buffer.putLong(TimeProvider.getCurrentTimestamp() - beforeTime);
    buffer.putLong(ownTraceId);
    buffer.putInt(ownOrderId);
  }

  public static void writeBeforeUnknownReceivedRecord(final long ownTraceId, final int ownOrderId,
      final String sender, final String destination) {
    final ByteBuffer buffer = AbstractAspect.bufferStore.get();

    if (BeforeUnknownReceivedRemoteCallRecord.COMPRESSED_BYTE_LENGTH_WITH_CLAZZ_ID > buffer
        .remaining()) {
      AbstractAspect.updateLastSendingTimeToCurrent();
      MonitoringController.sendOutBuffer(buffer);
    }

    beforeTime = TimeProvider.getCurrentTimestamp();

    buffer.put(BeforeUnknownReceivedRemoteCallRecord.CLAZZ_ID);
    buffer.putLong(ownTraceId);
    buffer.putInt(ownOrderId);
    buffer.putInt(MonitoringStringRegistry.getIdForString(sender));
    buffer.putInt(MonitoringStringRegistry.getIdForString(destination));
  }

  public static void writeAfterUnknownReceivedRecord(final long ownTraceId, final int ownOrderId) {
    final ByteBuffer buffer = AbstractAspect.bufferStore.get();

    if (AfterUnknownReceivedRemoteCallRecord.COMPRESSED_BYTE_LENGTH_WITH_CLAZZ_ID > buffer
        .remaining()) {
      AbstractAspect.updateLastSendingTimeToCurrent();
      MonitoringController.sendOutBuffer(buffer);
    }

    buffer.put(AfterUnknownReceivedRemoteCallRecord.CLAZZ_ID);
    buffer.putLong(TimeProvider.getCurrentTimestamp() - beforeTime);
    buffer.putLong(ownTraceId);
    buffer.putInt(ownOrderId);
  }
}
