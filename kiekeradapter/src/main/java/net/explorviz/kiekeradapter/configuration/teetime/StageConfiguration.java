package net.explorviz.kiekeradapter.configuration.teetime;

import kieker.analysis.source.rewriter.NoneTraceMetadataRewriter;
import kieker.analysis.source.tcp.MultipleConnectionTcpSourceStage;
import net.explorviz.kiekeradapter.filter.teetime.KiekerToExplorVizTransformStage;
import teetime.framework.Configuration;

/**
 * Teetime Pipe and Filter configuration for the kiekeradapter
 *
 * @author Christian Zirkelbach (czi@informatik.uni-kiel.de)
 *
 */
public class StageConfiguration extends Configuration {

  final int tcpReaderInputPort = 10133;
  final int tcpReaderBufferSize = 1024;

  public StageConfiguration() {
    final MultipleConnectionTcpSourceStage tcpReaderStage = new MultipleConnectionTcpSourceStage(
        this.tcpReaderInputPort, this.tcpReaderBufferSize, new NoneTraceMetadataRewriter());

    final KiekerToExplorVizTransformStage kiekerToExplorVizTransformStage =
        new KiekerToExplorVizTransformStage();

    this.connectPorts(tcpReaderStage.getOutputPort(),
        kiekerToExplorVizTransformStage.getInputPort());
  }
}
