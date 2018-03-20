package net.explorviz.kiekeradapter.configuration.teetime;

import net.explorviz.kiekeradapter.filter.teetime.KiekerToExplorVizTransformStage;
import net.explorviz.kiekeradapter.filter.teetime.MultipleConnectionTcpReaderStage;
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
		final MultipleConnectionTcpReaderStage tcpReaderStage = new MultipleConnectionTcpReaderStage(tcpReaderInputPort,
				tcpReaderBufferSize);
		final KiekerToExplorVizTransformStage kiekerToExplorVizTransformStage = new KiekerToExplorVizTransformStage();

		connectPorts(tcpReaderStage.getOutputPort(), kiekerToExplorVizTransformStage.getInputPort());
	}
}