package net.explorviz.injection;


import org.glassfish.hk2.api.Factory;

import net.explorviz.server.repository.LandscapeRepositoryModel;

public class LandscapeRepositoryFactory implements Factory<LandscapeRepositoryModel> {

	private LandscapeRepositoryModel model;
	
	public LandscapeRepositoryFactory() {
		this.model = LandscapeRepositoryModel.getInstance();
	}

	@Override
	public void dispose(LandscapeRepositoryModel arg0) {
	}

	@Override
	public LandscapeRepositoryModel provide() {
		return model;
	}

}
