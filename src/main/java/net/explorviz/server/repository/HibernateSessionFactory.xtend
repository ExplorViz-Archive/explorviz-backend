package net.explorviz.server.repository

import org.hibernate.SessionFactory
import org.hibernate.boot.registry.StandardServiceRegistry
import org.hibernate.boot.registry.StandardServiceRegistryBuilder
import org.hibernate.boot.MetadataSources
import org.hibernate.Session
import javax.ws.rs.core.Context
import javax.servlet.ServletContext

class HibernateSessionFactory {
	private static SessionFactory instance = null
	
	ServletContext servletContext;
	
	new(@Context ServletContext servletContext) {
		this.servletContext = servletContext
	}	

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
		if (net.explorviz.server.repository.HibernateSessionFactory.instance !== null) {
			return net.explorviz.server.repository.HibernateSessionFactory.instance
		} else {
			// config in src/main/webapp/WEB-INF/hibernate.cfg.xml
			val pathToHibernateConf = "/WEB-INF/hibernate.cfg.xml"		
			val configFile = servletContext.getResource(pathToHibernateConf);
			
			val StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure(configFile).build();
			try {
				net.explorviz.server.repository.HibernateSessionFactory.instance = new MetadataSources(registry).buildMetadata().buildSessionFactory();
				return net.explorviz.server.repository.HibernateSessionFactory.instance
			} catch (Exception e) {
				StandardServiceRegistryBuilder.destroy(registry);
				throw (e)
			}
		}
	}

}
