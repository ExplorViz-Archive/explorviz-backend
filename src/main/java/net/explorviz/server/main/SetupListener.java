package net.explorviz.server.main;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.explorviz.model.security.User;
import net.explorviz.repository.LandscapeExchangeService;
import net.explorviz.server.security.HibernateSessionFactory;
import net.explorviz.server.security.PasswordStorage;
import net.explorviz.server.security.PasswordStorage.CannotPerformOperationException;

@WebListener
public class SetupListener implements ServletContextListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(SetupListener.class);

	@Override
	public void contextInitialized(final ServletContextEvent servletContextEvent) {

		String hashedPassword = "";
		try {
			hashedPassword = PasswordStorage.createHash("admin");
		} catch (final CannotPerformOperationException e) {
			LOGGER.error("Couldn't create default admin user : ", e);
			return;
		}

		// set-up sessions
		final HibernateSessionFactory sessionFactory = new HibernateSessionFactory(
				servletContextEvent.getServletContext());

		final Session session = sessionFactory.beginTransaction();
		session.save(new User("admin", hashedPassword));
		sessionFactory.commitTransactionAndClose(session);

		final boolean dummyModeEnabled = Configuration.dummyMode;

		// Start ExplorViz Listener
		LandscapeExchangeService.startRepository();
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

	@Override
	public void contextDestroyed(final ServletContextEvent servletContextEvent) {
		// Nothing to dispose
	}

}
