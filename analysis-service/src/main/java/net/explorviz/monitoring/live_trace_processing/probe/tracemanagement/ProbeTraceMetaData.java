package net.explorviz.monitoring.live_trace_processing.probe.tracemanagement;


public class ProbeTraceMetaData {
  private long traceId;
  private int nextOrderId;
  private int stackDepth;

  public ProbeTraceMetaData() {
    this.reset();
  }

  public final long getTraceId() {
    return this.traceId;
  }

  public final void setTraceId(final long traceId) {
    this.traceId = traceId;
  }

  public final int getNextOrderId() {
    return this.nextOrderId++;
  }

  public final void reset() {
    this.nextOrderId = 0;
    this.stackDepth = 0;
  }

  public final void incrementStackDepth() {
    if (this.stackDepth == 0) {
      TraceRegistry.registerTrace(this);
    }
    this.stackDepth++;
  }

  public final void decreaseStackDepthAndEndTraceIfNeccessary() {
    this.stackDepth--;
    if (this.stackDepth == 0) {
      TraceRegistry.unregisterTrace(this);
    }
  }
}
