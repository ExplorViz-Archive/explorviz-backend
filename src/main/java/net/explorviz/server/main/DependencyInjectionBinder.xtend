package net.explorviz.server.main

import org.glassfish.hk2.utilities.binding.AbstractBinder
import com.github.jasminb.jsonapi.ResourceConverter
import javax.inject.Singleton
import net.explorviz.repository.LandscapeExchangeService
import net.explorviz.repository.HibernateSessionFactory
import net.explorviz.repository.LandscapeRepositoryModel
import net.explorviz.server.injection.LandscapeRepositoryFactory
import net.explorviz.server.injection.ResourceConverterFactory

class DependencyInjectionBinder extends AbstractBinder {
	override void configure() {
		this.bindFactory(LandscapeRepositoryFactory).to(LandscapeRepositoryModel).in(Singleton)
		this.bindFactory(ResourceConverterFactory).to(ResourceConverter).in(Singleton)
		
		this.bind(LandscapeExchangeService).to(LandscapeExchangeService).in(Singleton)
		this.bind(HibernateSessionFactory).to(HibernateSessionFactory).in(Singleton)
	}
}
