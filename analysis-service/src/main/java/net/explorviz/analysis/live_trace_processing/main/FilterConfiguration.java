package net.explorviz.analysis.live_trace_processing.main;

import java.util.Queue;
import java.util.concurrent.TimeUnit;
import net.explorviz.analysis.live_trace_processing.filter.reconstruction.TraceReconstructionFilter;
import net.explorviz.analysis.live_trace_processing.filter.reduction.TracesSummarizationFilter;
import net.explorviz.analysis.live_trace_processing.reader.TCPReader;
import net.explorviz.common.live_trace_processing.Constants;
import net.explorviz.common.live_trace_processing.configuration.Configuration;
import net.explorviz.common.live_trace_processing.configuration.ConfigurationFactory;
import net.explorviz.common.live_trace_processing.filter.PipesMerger;
import net.explorviz.common.live_trace_processing.filter.SinglePipeConnector;
import net.explorviz.common.live_trace_processing.record.IRecord;

public class FilterConfiguration {
  public static void configureAndStartFilters(final Configuration configuration,
      final Queue<IRecord> sink) {
    final SinglePipeConnector<IRecord> traceReductionConnector =
        new SinglePipeConnector<>(Constants.TRACE_RECONSTRUCTION_DISRUPTOR_SIZE);

    new TracesSummarizationFilter(traceReductionConnector, TimeUnit.SECONDS.toNanos(1), sink)
        .start();

    final PipesMerger<IRecord> traceReconstructionMerger =
        new PipesMerger<>(Constants.TCP_READER_DISRUPTOR_SIZE);

    new TraceReconstructionFilter(traceReconstructionMerger,
        traceReductionConnector.registerProducer(), Constants.TRACE_RECONSTRUCTION_TIMEOUT_IN_SEC)
            .start();

    new TCPReader(configuration.getIntProperty(ConfigurationFactory.READER_LISTENING_PORT, 10133),
        traceReconstructionMerger).read();
  }
}
