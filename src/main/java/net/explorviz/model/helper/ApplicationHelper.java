package net.explorviz.model.helper;

import java.util.ArrayList;
import java.util.List;

import net.explorviz.model.application.AggregatedClazzCommunication;
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

	/**
	 * Aggregates the outgoing clazz communications for the specific application
	 *
	 * @param application
	 */
	public static void computeAggregatedOutgoingClazzCommunications(final Application application) {

		for (final ClazzCommunication clazzCommu : application.getOutgoingClazzCommunications()) {
			addClazzCommunication(application, clazzCommu);
		}
	}

	/**
	 * Adds a clazzCommunication to a matching aggregatedClazzCommunication or
	 * create a new one
	 *
	 * @param application
	 * @param newCommunication
	 */
	public static void addClazzCommunication(final Application application, final ClazzCommunication newCommunication) {
		final List<AggregatedClazzCommunication> aggregatedOutgoingClazzCommunications = application
				.getAggregatedOutgoingClazzCommunications();

		// matching aggregatedClazzCommunication already exists
		for (final AggregatedClazzCommunication aggClazzCommu : aggregatedOutgoingClazzCommunications) {
			if (aggClazzCommu.getSourceClazz().equals(newCommunication.getSourceClazz())
					&& aggClazzCommu.getTargetClazz().equals(newCommunication.getTargetClazz())) {
				aggClazzCommu.addClazzCommunication(newCommunication);
				return;
			}
		}

		// create new aggregatedClazzCommunication
		final AggregatedClazzCommunication aggCommu = new AggregatedClazzCommunication();
		aggCommu.initializeID();
		aggCommu.setSourceClazz(newCommunication.getSourceClazz());
		aggCommu.setTargetClazz(newCommunication.getTargetClazz());
		aggCommu.setRequests(newCommunication.getRequests());
		aggCommu.addClazzCommunication(newCommunication);
		aggregatedOutgoingClazzCommunications.add(aggCommu);
	}

}
