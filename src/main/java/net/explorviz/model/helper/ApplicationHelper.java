package net.explorviz.model.helper;

import java.util.List;

import net.explorviz.model.application.AggregatedClazzCommunication;
import net.explorviz.model.application.Application;
import net.explorviz.model.application.ClazzCommunication;

/**
 * /** Helper class for Applications
 *
 * @author Christian Zirkelbach (czi@informatik.uni-kiel.de)
 *
 */
public final class ApplicationHelper {

	/**
	 * Adds a clazzCommunication to a matching aggregatedClazzCommunication or
	 * creates a new one
	 *
	 * @param application
	 * @param newCommunication
	 */
	public static void updateAggregatedClazzCommunication(final Application application,
			final ClazzCommunication newCommunication) {
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

		// creates a new aggregatedClazzCommunication
		final AggregatedClazzCommunication aggCommu = new AggregatedClazzCommunication();
		aggCommu.initializeID();
		aggCommu.setSourceClazz(newCommunication.getSourceClazz());
		aggCommu.setTargetClazz(newCommunication.getTargetClazz());
		aggCommu.setRequests(newCommunication.getRequests());

		// adds a clazzCommunication if sourceClazz and targetClazz matches
		if (aggCommu.addClazzCommunication(newCommunication)) {
			aggregatedOutgoingClazzCommunications.add(aggCommu);
		}
	}

}
