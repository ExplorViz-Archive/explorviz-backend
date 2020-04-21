package net.explorviz.kiekeradapter.main;

import net.explorviz.kiekeradapter.configuration.SignatureConverter;
import net.explorviz.kiekeradapter.configuration.teetime.StageConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import teetime.framework.Execution;

/**
 * Imports Kieker Monitoring Records into ExplorViz.
 */
public final class KiekerAdapter {

  private static final Logger LOGGER = LoggerFactory.getLogger(KiekerAdapter.class);
  private static KiekerAdapter instance;
  private static SignatureConverter signatureConverter;

  /**
   * Singleton utility method.
   *
   * @return
   */
  public static synchronized KiekerAdapter getInstance() {

    if (KiekerAdapter.instance == null) {
      KiekerAdapter.instance = new KiekerAdapter();
    }

    return KiekerAdapter.instance;
  }

  /**
   * Teetime starting point.
   */
  public void startReader() {
    LOGGER.info("Starting kiekerAdapter...");
    final StageConfiguration config = new StageConfiguration();
    final Execution<StageConfiguration> execution = new Execution<>(config);
    execution.executeNonBlocking();
  }

  public static SignatureConverter getSignatureConverter() {
    return signatureConverter;
  }

  public static void setSignatureConverter(final SignatureConverter signatureConverter) {
    KiekerAdapter.signatureConverter = signatureConverter;
  }

}
