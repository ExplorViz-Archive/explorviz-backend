package net.explorviz.common.live_trace_processing.writer;

import net.explorviz.common.live_trace_processing.record.misc.StringRegistryRecord;

public interface IStringRecordSender {
  void sendOutStringRecord(StringRegistryRecord record);

  void sendOutStringRecordAll(StringRegistryRecord record);

  void sendOutStringRecordAllSingle(StringRegistryRecord record);
}
