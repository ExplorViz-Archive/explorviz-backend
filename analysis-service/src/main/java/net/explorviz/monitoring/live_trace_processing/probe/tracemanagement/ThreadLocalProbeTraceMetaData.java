package net.explorviz.monitoring.live_trace_processing.probe.tracemanagement;

public final class ThreadLocalProbeTraceMetaData extends ThreadLocal<ProbeTraceMetaData> {
  @Override
  protected final ProbeTraceMetaData initialValue() {
    return new ProbeTraceMetaData();
  }
}
