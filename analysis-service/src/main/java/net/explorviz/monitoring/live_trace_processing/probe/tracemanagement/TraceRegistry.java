package net.explorviz.monitoring.live_trace_processing.probe.tracemanagement;

import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicLong;
import net.explorviz.common.live_trace_processing.configuration.Configuration;
import net.explorviz.common.live_trace_processing.configuration.ConfigurationFactory;
import net.explorviz.monitoring.live_trace_processing.main.MonitoringController;
import net.explorviz.monitoring.live_trace_processing.probe.AbstractAspect;

public final class TraceRegistry {
  private static boolean continous_monitoring;
  private static final AtomicLong nextTraceId;
  private static final ThreadLocalProbeTraceMetaData traceStorage =
      new ThreadLocalProbeTraceMetaData();

  static {
    final long uniqueOffset = new SecureRandom().nextLong();
    nextTraceId = new AtomicLong(uniqueOffset);

  }

  public static final void init(final Configuration config) {
    continous_monitoring =
        config.getBooleanProperty(ConfigurationFactory.CONTINOUS_MONITORING_ENABLED);
  }

  public static final ProbeTraceMetaData getTrace() {
    return traceStorage.get();
  }

  protected static final void registerTrace(final ProbeTraceMetaData trace) {
    trace.setTraceId(nextTraceId.getAndIncrement());
  }

  protected static final void unregisterTrace(final ProbeTraceMetaData trace) {
    trace.reset();
    if (!continous_monitoring) {
      MonitoringController.sendOutBuffer(AbstractAspect.bufferStore.get());
    }
  }
}
