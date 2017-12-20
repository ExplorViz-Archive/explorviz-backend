package net.explorviz.kiekeradapter.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.explorviz.kiekeradapter.configuration.SignatureConverter;
import net.explorviz.kiekeradapter.configuration.teetime.StageConfiguration;
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
		logger.info("Starting kiekerAdapter...");
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
