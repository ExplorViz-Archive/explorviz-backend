package net.explorviz.server.main

import javax.servlet.ServletContextListener
import javax.servlet.ServletContextEvent
import net.explorviz.server.repository.HibernateSessionFactory
import net.explorviz.server.security.PasswordStorage
import net.explorviz.server.security.User

class SetupListener implements ServletContextListener {

	override contextDestroyed(ServletContextEvent servletContextEvent) {
		throw new UnsupportedOperationException("TODO: auto-generated method stub")
	}

	override contextInitialized(ServletContextEvent servletContextEvent) {
		val hibernateSessionFactory = new HibernateSessionFactory(servletContextEvent.servletContext)

		val hashedPassword = PasswordStorage.createHash("admin");

		val session = hibernateSessionFactory.beginTransaction();
		session.save(new User("admin", hashedPassword));
		hibernateSessionFactory.commitTransactionAndClose(session);
	}

}
