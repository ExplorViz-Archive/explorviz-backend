package net.explorviz.server.main;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import net.explorviz.injection.LandscapeRepositoryFactory;
import net.explorviz.server.repository.LandscapeRepositoryModel;

import javax.inject.Singleton;

public class DependencyInjectionBinder extends AbstractBinder {
	@Override
	public void configure() {
		
		this.bindFactory(LandscapeRepositoryFactory.class).to(LandscapeRepositoryModel.class).in(Singleton.class);
	}
}
