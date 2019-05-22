package net.explorviz.monitoring.live_trace_processing.probe;

import net.explorviz.common.live_trace_processing.reader.TimeProvider;

public class ThreadLocalLastSendingTime extends ThreadLocal<Long> {
  @Override
  protected Long initialValue() {
    return TimeProvider.getCurrentTimestamp();
  }
}
