package net.explorviz.server.main;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import net.explorviz.injection.LandscapeRepositoryFactory;
import net.explorviz.server.repository.LandscapeRepositoryModel;
import net.explorviz.server.repository.RepositoryStarter;

import javax.inject.Singleton;

public class DependencyInjectionBinder extends AbstractBinder {
	@Override
	public void configure() {

		final LandscapeRepositoryModel model = LandscapeRepositoryModel.getInstance();

		new Thread(new Runnable() {

			@Override
			public void run() {
				new RepositoryStarter().start(model);
			}
		}).start();
		
		this.bindFactory(LandscapeRepositoryFactory.class).to(LandscapeRepositoryModel.class).in(Singleton.class);
	}
}
