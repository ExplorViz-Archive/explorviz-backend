package net.explorviz.repository;

import explorviz.live_trace_processing.filter.AbstractSink;
import explorviz.live_trace_processing.filter.ITraceSink;
import explorviz.live_trace_processing.filter.SinglePipeConnector;
import explorviz.live_trace_processing.record.IRecord;

public final class LandscapeRepositorySink extends AbstractSink implements ITraceSink {
  private final LandscapeRepositoryModel model;
  private final SinglePipeConnector<IRecord> modelConnector;

  public LandscapeRepositorySink(final SinglePipeConnector<IRecord> modelConnector,
      final LandscapeRepositoryModel model) {
    super();
    this.modelConnector = modelConnector;
    this.model = model;
  }

  @Override
  public void run() {
    modelConnector.process(this);
  }

  @Override
  public void processRecord(final IRecord record) {
    model.insertIntoModel(record);
  }
}
