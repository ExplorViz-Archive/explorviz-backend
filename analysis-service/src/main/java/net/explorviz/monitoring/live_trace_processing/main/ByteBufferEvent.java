package net.explorviz.monitoring.live_trace_processing.main;

import com.lmax.disruptor.EventFactory;
import java.nio.ByteBuffer;
import net.explorviz.common.live_trace_processing.Constants;

public final class ByteBufferEvent {
  private ByteBuffer value = ByteBuffer.allocate(Constants.SENDING_BUFFER_SIZE);

  public final ByteBuffer getValue() {
    return this.value;
  }

  public void setValue(final ByteBuffer value) {
    this.value = value;
  }

  public final static EventFactory<ByteBufferEvent> EVENT_FACTORY =
      new EventFactory<ByteBufferEvent>() {
        @Override
        public ByteBufferEvent newInstance() {
          return new ByteBufferEvent();
        }
      };
}
