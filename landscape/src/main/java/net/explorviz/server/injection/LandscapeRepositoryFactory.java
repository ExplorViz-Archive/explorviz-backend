package net.explorviz.server.injection;

import org.glassfish.hk2.api.Factory;

import net.explorviz.repository.LandscapeRepositoryModel;

/**
 * Factory for creating landscape instances
 * 
 * @author Christian Zirkelbach (czi@informatik.uni-kiel.de)
 *
 */
public class LandscapeRepositoryFactory implements Factory<LandscapeRepositoryModel> {

	LandscapeRepositoryModel model;

	public LandscapeRepositoryFactory() {
		this.model = LandscapeRepositoryModel.getInstance();
	}

	@Override
	public void dispose(final LandscapeRepositoryModel arg0) {
	}

	@Override
	public LandscapeRepositoryModel provide() {
		return model;
	}
}
