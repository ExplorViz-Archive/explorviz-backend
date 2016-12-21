package net.explorviz.injection

import org.glassfish.hk2.api.Factory
import net.explorviz.server.repository.LandscapeRepositoryModel

class LandscapeRepositoryFactory implements Factory<LandscapeRepositoryModel> {
	
	LandscapeRepositoryModel model

	new() {
		this.model = LandscapeRepositoryModel.getInstance()
	}

	override void dispose(LandscapeRepositoryModel arg0) {
	}

	override LandscapeRepositoryModel provide() {
		return model		
	}
}
