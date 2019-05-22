package net.explorviz.common.live_trace_processing.filter;

import net.explorviz.common.live_trace_processing.record.IRecord;

public abstract class AbstractSink extends Thread implements ITraceSink {
  @Override
  public abstract void processRecord(final IRecord record);
}
