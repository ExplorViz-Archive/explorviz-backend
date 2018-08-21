package net.explorviz.server.main;

import javax.inject.Inject;
import javax.servlet.annotation.WebListener;

import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEvent.Type;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.explorviz.repository.LandscapeExchangeService;
import net.explorviz.shared.server.helper.PropertyService;

/**
 * Primary starting class - executed, when the servlet context is started
 */
@WebListener
public class SetupApplicationListener implements ApplicationEventListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(SetupApplicationListener.class);

	@Inject
	LandscapeExchangeService exchangeService;

	@Override
	public void onEvent(final ApplicationEvent event) {

		// After this type, CDI (e.g. injected LandscapeExchangeService) has been
		// fullfilled
		final Type t = Type.INITIALIZATION_FINISHED;

		if (event.getType().equals(t)) {
			startExplorVizBackend();
			startDatabase();
		}
	}

	private void startDatabase() {
		//		String hashedPassword = "";
		//		try {
		//			hashedPassword = PasswordStorage.createHash("admin");
		//		} catch (final CannotPerformOperationException e) {
		//			LOGGER.error("Couldn't create default admin user : ", e);
		//			return;
		//		}
	}

	@Override
	public RequestEventListener onRequest(final RequestEvent requestEvent) {
		return null;
	}

	private void startExplorVizBackend() {
		// Start ExplorViz Listener
		exchangeService.startRepository();

		final boolean dummyModeEnabled = PropertyService.getBooleanProperty("repository.useDummyMode");

		LOGGER.info("\n");
		LOGGER.info("* * * * * * * * * * * * * * * * * * *\n");
		LOGGER.info("Server (ExplorViz Backend) sucessfully started. Traces can now be processed.\n");

		if (dummyModeEnabled) {
			LOGGER.info("Dummy monitoring data is generated now!\n");
		} else {
			LOGGER.info("Please start kiekerSampleApplication now!\n");
		}
		LOGGER.info("* * * * * * * * * * * * * * * * * * *\n");
	}

}
