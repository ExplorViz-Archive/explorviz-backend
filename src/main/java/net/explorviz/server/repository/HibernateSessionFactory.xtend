package net.explorviz.server.repository

import org.hibernate.SessionFactory
import org.hibernate.boot.registry.StandardServiceRegistry
import org.hibernate.boot.registry.StandardServiceRegistryBuilder
import org.hibernate.boot.MetadataSources

class HibernateSessionFactory {

	private static SessionFactory INSTANCE = null

	def static synchronized getInstance() {
		if (INSTANCE != null) {
			return INSTANCE
		} else {
			// config in src/main/resources/hibernate.cfg.xml
			val StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
			try {
				INSTANCE = new MetadataSources(registry).buildMetadata().buildSessionFactory();
				return INSTANCE
			} catch (Exception e) {
				StandardServiceRegistryBuilder.destroy(registry);
				throw(e)
			}
		}
	}

}
