package net.explorviz.kiekeradapter.main;

/**
 * Starting the Kieker Monitoring Log Adapter (for testing purposes)
 * @author Christian Zirkelbach (czi@informatik.uni-kiel.de)
 *
 */
public class Main {
	public static void main(final String[] args) {
		final KiekerAdapter monitoringReader = new KiekerAdapter();
		monitoringReader.startReader();
	}
}
