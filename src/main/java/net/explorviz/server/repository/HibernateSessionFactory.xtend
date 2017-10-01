package net.explorviz.server.repository

import org.hibernate.SessionFactory
import org.hibernate.boot.registry.StandardServiceRegistry
import org.hibernate.boot.registry.StandardServiceRegistryBuilder
import org.hibernate.boot.MetadataSources
import org.hibernate.Session
import javax.ws.rs.core.Context
import javax.servlet.ServletContext

class HibernateSessionFactory {
	
	ServletContext servletContext;
	
	new(@Context ServletContext servletContext) {
		this.servletContext = servletContext
	}

	private SessionFactory INSTANCE = null

	def synchronized beginTransaction() {
		val session = getInstance().openSession();
		session.beginTransaction();
		session
	}

	def synchronized commitTransactionAndClose(Session session) {
		session.getTransaction().commit();
		session.close();
	}

	def synchronized getInstance() {
		if (INSTANCE != null) {
			return INSTANCE
		} else {
			// config in src/main/webapp/WEB-INF/hibernate.cfg.xml
			val pathToHibernateConf = servletContext.contextPath + "/WEB-INF/hibernate.cfg.xml"
			println(pathToHibernateConf)
			
			//
			val configFile = servletContext.getResource(pathToHibernateConf);
			println(configFile)
			//
			
			val StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure(configFile).build();
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
