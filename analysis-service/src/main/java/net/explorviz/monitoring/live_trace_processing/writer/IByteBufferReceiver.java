package net.explorviz.monitoring.live_trace_processing.writer;

import net.explorviz.monitoring.live_trace_processing.main.ByteBufferEvent;

public interface IByteBufferReceiver {

	void onEvent(ByteBufferEvent event);

}
