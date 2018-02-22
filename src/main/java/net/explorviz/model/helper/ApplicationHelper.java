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
	private static void computeOutgoingClazzCommunications(final Application application) {

		final List<ClazzCommunication> outgoingClazzCommunicationFinalList = new ArrayList<ClazzCommunication>();

		for (final Component component : application.getComponents()) {

			// add all found items (for a single component) to the final list
			outgoingClazzCommunicationFinalList
					.addAll(ClazzCommunicationHelper.getChildrenComponentClazzCommunications(component));
		}

		application.setOutgoingClazzCommunications(outgoingClazzCommunicationFinalList);
	}

	/**
	 * Aggregates outgoing clazzCommunications with the same sourceClazz and
	 * targetClazz and updates the attributes
	 */
	public static void calculateAggregatedOutgoingClazzCommunications(final Application application) {

		// compute all outgoing clazz communications
		ApplicationHelper.computeOutgoingClazzCommunications(application);

		// aggregate similar clazz communications
		final List<ClazzCommunication> aggregatedOutgoingClazzCommunications = new ArrayList<ClazzCommunication>();

		for (final ClazzCommunication singleClazzCommunication : application.getOutgoingClazzCommunications()) {
			// if not exists, create an aggregated clazzcommunication
			if (aggregatedOutgoingClazzCommunications.isEmpty()) {
				final ClazzCommunication newAggregatedClazzCommunication = new ClazzCommunication();
				newAggregatedClazzCommunication.setOperationName(singleClazzCommunication.getOperationName());
				newAggregatedClazzCommunication.setRequestsCacheCount(singleClazzCommunication.getRequestsCacheCount());
				newAggregatedClazzCommunication.setSourceClazz(singleClazzCommunication.getSourceClazz());
				newAggregatedClazzCommunication.setTargetClazz(singleClazzCommunication.getTargetClazz());

				aggregatedOutgoingClazzCommunications.add(newAggregatedClazzCommunication);
			}

			else {
				for (final ClazzCommunication aggregatedClazzCommunication : aggregatedOutgoingClazzCommunications) {
					if (aggregatedClazzCommunication.getId() != singleClazzCommunication.getId()
							&& (singleClazzCommunication.getSourceClazz() == aggregatedClazzCommunication
									.getSourceClazz())
							&& (singleClazzCommunication.getTargetClazz() == aggregatedClazzCommunication
									.getTargetClazz())) {

						aggregatedClazzCommunication
								.setRequestsCacheCount(aggregatedClazzCommunication.getRequestsCacheCount()
										+ singleClazzCommunication.getRequestsCacheCount());
						aggregatedOutgoingClazzCommunications.add(singleClazzCommunication);
					}
				}
			}
		}
		application.setAggregatedOutgoingClazzCommunications(aggregatedOutgoingClazzCommunications);
	}
}
