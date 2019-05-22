package net.explorviz.monitoring.live_trace_processing.probe;

import java.nio.ByteBuffer;

public class ThreadLocalByteBuffer extends ThreadLocal<ByteBuffer> {
  private final int messageBufferSize;

  public ThreadLocalByteBuffer(final int messageBufferSize) {
    this.messageBufferSize = messageBufferSize;
  }

  @Override
  protected ByteBuffer initialValue() {
    final ByteBuffer result = ByteBuffer.allocate(this.messageBufferSize);
    return result;
  }
}
