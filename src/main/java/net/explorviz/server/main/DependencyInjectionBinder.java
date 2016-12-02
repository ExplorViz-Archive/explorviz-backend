package net.explorviz.server.main;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

import com.github.jasminb.jsonapi.ResourceConverter;

import net.explorviz.injection.LandscapeRepositoryFactory;
import net.explorviz.injection.ResourceConverterFactory;
import net.explorviz.server.repository.LandscapeRepositoryModel;

import javax.inject.Singleton;

public class DependencyInjectionBinder extends AbstractBinder {
	@Override
	public void configure() {		
		this.bindFactory(LandscapeRepositoryFactory.class).to(LandscapeRepositoryModel.class).in(Singleton.class);
		this.bindFactory(ResourceConverterFactory.class).to(ResourceConverter.class).in(Singleton.class);
	}
}
