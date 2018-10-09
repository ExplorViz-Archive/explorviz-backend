package net.explorviz.landscape.repository;

import explorviz.live_trace_processing.configuration.Configuration;
import explorviz.live_trace_processing.configuration.ConfigurationFactory;
import explorviz.live_trace_processing.filter.SinglePipeConnector;
import explorviz.live_trace_processing.main.FilterConfiguration;
import explorviz.live_trace_processing.record.IRecord;
import java.util.Queue;

public class RepositoryStarter {
  public void start(final LandscapeRepositoryModel model) {
    final SinglePipeConnector<IRecord> modelConnector = new SinglePipeConnector<>(64);

    new LandscapeRepositorySink(modelConnector, model).start();

    final Queue<IRecord> sink = modelConnector.registerProducer();

    final Configuration configuration = ConfigurationFactory.createSingletonConfiguration();

    FilterConfiguration.configureAndStartFilters(configuration, sink);
  }
}
