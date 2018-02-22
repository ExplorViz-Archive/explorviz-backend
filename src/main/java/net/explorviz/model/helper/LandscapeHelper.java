package net.explorviz.model.helper;

import java.util.ArrayList;
import java.util.List;

import net.explorviz.model.application.Application;
import net.explorviz.model.application.ApplicationCommunication;
import net.explorviz.model.landscape.Landscape;
import net.explorviz.model.landscape.Node;
import net.explorviz.model.landscape.NodeGroup;
import net.explorviz.model.landscape.System;

/**
 * /** Helper class for Landscapes
 *
 * @author Christian Zirkelbach (czi@informatik.uni-kiel.de)
 *
 */
public final class LandscapeHelper {

	/**
	 * Returns all outgoing communication between applications within the landscape
	 */
	public static void computeOutgoingApplicationCommunications(final Landscape landscape) {

		final List<ApplicationCommunication> outgoingCommunicationList = new ArrayList<ApplicationCommunication>();

		for (final System system : landscape.getSystems()) {
			for (final NodeGroup nodegroup : system.getNodeGroups()) {
				for (final Node node : nodegroup.getNodes()) {
					for (final Application application : node.getApplications()) {
						for (final ApplicationCommunication outgoingCommunication : application
								.getOutgoingApplicationCommunications()) {
							outgoingCommunicationList.add(outgoingCommunication);
						}
					}
				}
			}
		}

		landscape.setOutgoingApplicationCommunications(outgoingCommunicationList);
	}
}
