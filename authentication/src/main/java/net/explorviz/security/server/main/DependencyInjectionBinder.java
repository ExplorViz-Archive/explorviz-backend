package net.explorviz.security.server.main;

import javax.inject.Singleton;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

import net.explorviz.security.services.TokenService;
import net.explorviz.security.services.UserService;
import net.explorviz.shared.security.TokenParserService;


public class DependencyInjectionBinder extends AbstractBinder {

	@Override
	public void configure() {

		this.bind(TokenService.class).to(TokenService.class).in(Singleton.class);
		this.bind(UserService.class).to(UserService.class).in(Singleton.class);
		this.bind(TokenParserService.class).to(TokenParserService.class).in(Singleton.class);

	}
}
