package net.explorviz.server.repository

import org.hibernate.SessionFactory
import org.hibernate.boot.registry.StandardServiceRegistry
import org.hibernate.boot.registry.StandardServiceRegistryBuilder
import org.hibernate.boot.MetadataSources
import org.hibernate.Session
import javax.ws.rs.core.Context
import javax.servlet.ServletContext

class HibernateSessionFactory {
	
	@Context static ServletContext servletContext;

	private static SessionFactory INSTANCE = null

	def static synchronized beginTransaction() {
		val session = getInstance().openSession();
		session.beginTransaction();
		session
	}

	def static synchronized commitTransactionAndClose(Session session) {
		session.getTransaction().commit();
		session.close();
	}

	def static synchronized getInstance() {
		if (INSTANCE != null) {
			return INSTANCE
		} else {
			// config in src/main/webapp/WEB-INF/hibernate.cfg.xml
			
			val pathToHibernateConf = servletContext.getRealPath("hibernate.cfg.xml");
			println(pathToHibernateConf)
			val StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure(pathToHibernateConf).build();
			try {
				INSTANCE = new MetadataSources(registry).buildMetadata().buildSessionFactory();
				return INSTANCE
			} catch (Exception e) {
				StandardServiceRegistryBuilder.destroy(registry);
				throw (e)
			}
		}
	}

}
