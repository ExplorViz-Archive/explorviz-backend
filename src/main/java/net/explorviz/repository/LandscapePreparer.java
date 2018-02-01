package net.explorviz.repository;

import net.explorviz.model.Application;
import net.explorviz.model.Communication;
import net.explorviz.model.Component;
import net.explorviz.model.Landscape;
import net.explorviz.model.Node;
import net.explorviz.model.NodeGroup;
import net.explorviz.model.System;

public class LandscapePreparer {

	public static Landscape prepareLandscape(final Landscape landscape) {
		if (landscape == null) {
			final Landscape emptyLandscape = new Landscape();
			emptyLandscape.initializeID();
			return emptyLandscape;
		}

		for (final System system : landscape.getSystems()) {
			for (final NodeGroup nodeGroup : system.getNodeGroups()) {
				for (final Node node : nodeGroup.getNodes()) {
					for (final Application application : node.getApplications()) {

						final Component foundationComponent = new Component();
						foundationComponent.setFoundation(true);
						foundationComponent.setOpened(true);
						foundationComponent.setName(application.getName());
						foundationComponent.setFullQualifiedName(application.getName());
						foundationComponent.setBelongingApplication(application);
						// foundationComponent.setColor(ColorDefinitions.componentFoundationColor);

						foundationComponent.getChildren().addAll(application.getComponents());

						for (final Component child : foundationComponent.getChildren()) {
							setComponentAttributes(child, 0, true);
						}

					}
				}

				if (nodeGroup.getNodes().size() == 1) {
					nodeGroup.setOpened(true);
				} else {
					nodeGroup.setOpened(false);
				}
				nodeGroup.updateName();
			}
		}

		for (final Communication commu : landscape.getApplicationCommunication()) {
			createApplicationInAndOutgoing(commu);
		}

		return landscape;
	}

	private static void setComponentAttributes(final Component component, final int index,
			final boolean shouldBeOpened) {
		boolean openNextLevel = shouldBeOpened;

		if (!openNextLevel) {
			component.setOpened(false);
		} else if (component.getChildren().size() == 1) {
			component.setOpened(true);
		} else {
			component.setOpened(true);
			openNextLevel = false;
		}

		for (final Component child : component.getChildren()) {
			setComponentAttributes(child, index + 1, openNextLevel);
		}
	}

	private static void createApplicationInAndOutgoing(final Communication communication) {
		final Application sourceApp = communication.getSource();
		if (sourceApp != null) {
			sourceApp.getOutgoingCommunications().add(communication);
		}

		final Application targetApp = communication.getTarget();
		if (targetApp != null) {
			targetApp.getIncomingCommunications().add(communication);
		}
	}
}
