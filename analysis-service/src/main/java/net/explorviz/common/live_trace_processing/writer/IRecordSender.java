package net.explorviz.common.live_trace_processing.writer;

import java.nio.ByteBuffer;

public interface IRecordSender {
  void send(ByteBuffer buffer);
}
