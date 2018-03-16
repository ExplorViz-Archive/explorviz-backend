package net.explorviz.server.security;

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SessionFactory for Hibnerate transactions
 *
 * @author Christian Zirkelbach (czi@informatik.uni-kiel.de)
 *
 */
public class HibernateSessionFactory {

	private static final Logger LOGGER = LoggerFactory.getLogger(HibernateSessionFactory.class);

	private static SessionFactory instance = null;
	ServletContext servletContext;

	public HibernateSessionFactory(@Context final ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	/**
	 * Starts a transaction
	 *
	 * @return
	 */
	public synchronized Session beginTransaction() {
		final Session session = getInstance().openSession();
		session.beginTransaction();
		return session;
	}

	/**
	 * Performs a commit and cloeses the transaction
	 *
	 * @param session
	 */
	public synchronized void commitTransactionAndClose(final Session session) {
		session.getTransaction().commit();
		session.close();
	}

	/**
	 * Returns an instance of SessionFactory
	 *
	 * @return
	 */
	public synchronized SessionFactory getInstance() {
		if (instance != null) {
			return HibernateSessionFactory.instance;
		} else {
			// config located at src/main/webapp/WEB-INF/hibernate.cfg.xml
			final String pathToHibernateConf = "/WEB-INF/hibernate.cfg.xml";
			URL configFile;
			try {
				configFile = servletContext.getResource(pathToHibernateConf);
				final StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure(configFile)
						.build();
				try {
					instance = new MetadataSources(registry).buildMetadata().buildSessionFactory();
					return instance;
				} catch (final Exception e) {
					StandardServiceRegistryBuilder.destroy(registry);
					LOGGER.debug(e.getMessage());
				}
			} catch (final MalformedURLException e) {
				LOGGER.debug(e.getMessage());

			}
			return null;
		}
	}

}
