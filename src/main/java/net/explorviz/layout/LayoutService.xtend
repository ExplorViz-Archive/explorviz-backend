package net.explorviz.layout

import net.explorviz.model.Application
import net.explorviz.model.Landscape
import net.explorviz.layout.application.ApplicationLayoutInterface
import net.explorviz.layout.exceptions.LayoutException
import net.explorviz.layout.landscape.LandscapeKielerInterface

class LayoutService {

	def static Landscape layoutLandscape(Landscape landscape)
			throws LayoutException {
		LandscapeKielerInterface::applyLayout(landscape)
	}
	
	def static Application layoutApplication(Application application)
			throws LayoutException {
		ApplicationLayoutInterface::applyLayout(application)
	}
}