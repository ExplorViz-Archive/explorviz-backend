package net.explorviz.server.main;

import javax.inject.Singleton;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

import com.github.jasminb.jsonapi.ResourceConverter;

import net.explorviz.model.helper.ErrorObjectHelper;
import net.explorviz.repository.LandscapeExchangeService;
import net.explorviz.repository.LandscapeRepositoryModel;
import net.explorviz.server.injection.LandscapeRepositoryFactory;
import net.explorviz.server.injection.ResourceConverterFactory;
import net.explorviz.server.security.HibernateSessionFactory;

/**
 * Configures the dependency binding setup for inject during runtime
 *
 * @author Christian Zirkelbach (czi@informatik.uni-kiel.de)
 *
 */
public class DependencyInjectionBinder extends AbstractBinder {

	@Override
	public void configure() {
		this.bindFactory(LandscapeRepositoryFactory.class).to(LandscapeRepositoryModel.class).in(Singleton.class);
		this.bindFactory(ResourceConverterFactory.class).to(ResourceConverter.class).in(Singleton.class);

		this.bind(LandscapeExchangeService.class).to(LandscapeExchangeService.class).in(Singleton.class);
		this.bind(HibernateSessionFactory.class).to(HibernateSessionFactory.class).in(Singleton.class);
		this.bind(ErrorObjectHelper.class).to(ErrorObjectHelper.class).in(Singleton.class);
	}
}