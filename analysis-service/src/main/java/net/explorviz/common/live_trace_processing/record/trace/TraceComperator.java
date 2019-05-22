package net.explorviz.common.live_trace_processing.record.trace;

import java.util.Comparator;

public class TraceComperator implements Comparator<Trace> {
  @Override
  public int compare(final Trace t1, final Trace t2) {
    return t1.compareTo(t2);
  }
}
