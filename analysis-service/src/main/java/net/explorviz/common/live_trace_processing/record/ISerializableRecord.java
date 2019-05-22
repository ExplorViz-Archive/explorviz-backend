package net.explorviz.common.live_trace_processing.record;

import java.nio.ByteBuffer;
import net.explorviz.common.live_trace_processing.StringRegistrySender;
import net.explorviz.common.live_trace_processing.writer.IRecordSender;

public interface ISerializableRecord extends IRecord {
  int getRecordSizeInBytes();

  void putIntoByteBuffer(ByteBuffer buffer, StringRegistrySender stringRegistry,
      final IRecordSender writer);

  // 0: HostApplicationMetaDataRecord
  // 1: BeforeOperationEventRecord
  // 2: AfterFailedOperationEventRecord
  // 3: AfterOperationEventRecord
  // 4: StringRegistryRecord
  // 5: SystemMonitoringRecord
  // 6: Trace
  // 7: BeforeConstructorEventRecord
  // 8: AfterFailedConstructorEventRecord
  // 9: AfterConstructorEventRecord
  // 10: ReceivedRemoteCallRecord
  // 11: BeforeStaticOperationEventRecord
  // 12: AfterFailedStaticOperationEventRecord
  // 13: AfterStaticOperationEventRecord
  // 14: SentRemoteCallRecord
  // 15: UnknownReceivedRemoteCallRecord
}
