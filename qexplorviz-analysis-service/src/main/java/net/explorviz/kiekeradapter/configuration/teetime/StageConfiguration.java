package net.explorviz.kiekeradapter.configuration.teetime;

import kieker.analysis.source.rewriter.NoneTraceMetadataRewriter;
import kieker.analysis.source.tcp.MultipleConnectionTcpSourceStage;
import net.explorviz.kiekeradapter.filter.teetime.KiekerToExplorVizTransformStage;
import teetime.framework.Configuration;

/**
 * Teetime Pipe and Filter configuration for the kiekeradapter.
 */
public class StageConfiguration extends Configuration {

  private static final int TCP_READER_INPUT_PORT = 10_133;
  private static final int TCP_READER_BUFFER_SIZE = 1024;

  /**
   * Custom {@link Configuration} class for TeeTime Pipe and Filter Execution.
   */
  public StageConfiguration() {
    super();
    final MultipleConnectionTcpSourceStage tcpReaderStage =
        new MultipleConnectionTcpSourceStage(StageConfiguration.TCP_READER_INPUT_PORT,
            StageConfiguration.TCP_READER_BUFFER_SIZE, new NoneTraceMetadataRewriter());

    final KiekerToExplorVizTransformStage kiekerToExplTransformStage =
        new KiekerToExplorVizTransformStage();

    this.connectPorts(tcpReaderStage.getOutputPort(), kiekerToExplTransformStage.getInputPort());
  }
}
