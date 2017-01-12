package net.explorviz.server.main

import org.glassfish.hk2.utilities.binding.AbstractBinder
import com.github.jasminb.jsonapi.ResourceConverter
import net.explorviz.injection.LandscapeRepositoryFactory
import net.explorviz.injection.ResourceConverterFactory
import net.explorviz.server.repository.LandscapeRepositoryModel
import javax.inject.Singleton
import net.explorviz.server.repository.LandscapeExchangeService

class DependencyInjectionBinder extends AbstractBinder {
	override void configure() {
		this.bindFactory(LandscapeRepositoryFactory).to(LandscapeRepositoryModel).in(Singleton)
		this.bindFactory(ResourceConverterFactory).to(ResourceConverter).in(Singleton)
		
		this.bind(LandscapeExchangeService).to(LandscapeExchangeService).in(Singleton)
	}
}
