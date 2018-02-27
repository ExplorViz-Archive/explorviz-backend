package net.explorviz.model.helper;

import java.util.ArrayList;
import java.util.List;

import net.explorviz.model.application.Application;
import net.explorviz.model.application.ClazzCommunication;
import net.explorviz.model.application.Component;

/**
 * /** Helper class for Applications
 *
 * @author Christian Zirkelbach (czi@informatik.uni-kiel.de)
 *
 */
public final class ApplicationHelper {

	/**
	 * Calculates and sets all outgoing clazz communications for the specific
	 * application
	 */
	public static void computeOutgoingClazzCommunications(final Application application) {

		final List<ClazzCommunication> outgoingClazzCommunicationFinalList = new ArrayList<ClazzCommunication>();

		for (final Component component : application.getComponents()) {

			// add all found items (for a single component) to the final list
			outgoingClazzCommunicationFinalList
					.addAll(ClazzCommunicationHelper.getChildrenComponentClazzCommunications(component));
		}

		application.setOutgoingClazzCommunications(outgoingClazzCommunicationFinalList);
	}
}
