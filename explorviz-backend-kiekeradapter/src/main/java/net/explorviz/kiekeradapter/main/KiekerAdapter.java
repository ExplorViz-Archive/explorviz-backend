package net.explorviz.kiekeradapter.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kieker.analysis.AnalysisController;
import kieker.analysis.IAnalysisController;
import kieker.analysis.exception.AnalysisConfigurationException;
import kieker.analysis.plugin.reader.tcp.TCPReader;
import kieker.common.configuration.Configuration;
import net.explorviz.kiekeradapter.configuration.SignatureConverter;
import net.explorviz.kiekeradapter.configuration.teetime.StageConfiguration;
import net.explorviz.kiekeradapter.filter.KiekerToExplorVizTransformFilter;
import teetime.framework.Execution;

/**
 * Imports Kieker Monitoring Records into ExplorViz
 * 
 * @author Christian Zirkelbach (czi@informatik.uni-kiel.de)
 *
 */
public final class KiekerAdapter {

	final Logger logger = LoggerFactory.getLogger(KiekerAdapter.class.getName());
	private static KiekerAdapter instance = null;
	private static SignatureConverter signatureConverter;

	public static synchronized KiekerAdapter getInstance() {
		if (KiekerAdapter.instance == null) {
			KiekerAdapter.instance = new KiekerAdapter();
		}
		return KiekerAdapter.instance;
	}
	
	public void startReader() {
//		final IAnalysisController analysisInstance = new AnalysisController();
//
//		final Configuration tcpReaderConfig = new Configuration();
//		final TCPReader tcpReader = new TCPReader(tcpReaderConfig, analysisInstance);
//
//		final Configuration tcpWriterConfig = new Configuration();
//		final KiekerToExplorVizTransformFilter transformFilter = new KiekerToExplorVizTransformFilter(tcpWriterConfig,
//				analysisInstance);
//
//		logger.info("Starting the kiekeradapter component...");
//
//		try {
//			analysisInstance.connect(tcpReader, TCPReader.OUTPUT_PORT_NAME_RECORDS, transformFilter,
//					KiekerToExplorVizTransformFilter.INPUT_PORT_NAME_KIEKER);
//			analysisInstance.run();
//		} catch (IllegalStateException | AnalysisConfigurationException e) {
//			logger.error("Can't start the kiekeradapter component!", e.getMessage());
//		}
		
		StageConfiguration config = new StageConfiguration();
		Execution<StageConfiguration> execution = new Execution<StageConfiguration>(config);
		execution.executeNonBlocking();
	}

	public static SignatureConverter getSignatureConverter() {
		return signatureConverter;
	}

	public static void setSignatureConverter(SignatureConverter signatureConverter) {
		KiekerAdapter.signatureConverter = signatureConverter;
	}

}
