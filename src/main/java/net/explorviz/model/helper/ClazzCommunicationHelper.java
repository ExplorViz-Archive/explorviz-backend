package net.explorviz.model.helper;

import java.util.ArrayList;
import java.util.List;

import net.explorviz.model.application.Application;
import net.explorviz.model.application.Clazz;
import net.explorviz.model.application.ClazzCommunication;
import net.explorviz.model.application.Component;

/**
 * Helper class for ClazzCommunications
 *
 * @author Christian Zirkelbach (czi@informatik.uni-kiel.de)
 *
 */
public final class ClazzCommunicationHelper {

	/**
	 * Adds a clazz communication or runtime information to a specific clazz within
	 * an application
	 *
	 * @param caller
	 * @param callee
	 * @param application
	 * @param requests
	 * @param average
	 * @param overallTraceDuration
	 * @param traceId
	 * @param orderIndex
	 * @param operationName
	 */
	public static void addClazzCommunication(final Clazz caller, final Clazz callee, final Application application,
			final int requests, final double average, final double overallTraceDuration, final long traceId,
			final int orderIndex, final String operationName) {

		// clazzCommunication already exists
		for (final ClazzCommunication commu : caller.getOutgoingClazzCommunications()) {
			if (((commu.getSourceClazz() == caller) && (commu.getTargetClazz() == callee)
					&& (commu.getOperationName().equalsIgnoreCase(operationName)))) {

				commu.addRuntimeInformation(traceId, orderIndex, requests, (float) average,
						(float) overallTraceDuration);
				return;
			}
		}

		// create new clazzCommunication
		final ClazzCommunication commu = new ClazzCommunication();
		commu.initializeID();
		commu.setSourceClazz(caller);
		commu.setTargetClazz(callee);
		commu.setOperationName(operationName);
		commu.addRuntimeInformation(traceId, orderIndex, requests, (float) average, (float) overallTraceDuration);

		// add clazzCommunication to calling clazz (sourceClazz)
		caller.getOutgoingClazzCommunications().add(commu);

		// add aggregtaedClazzCommunication to application
		ApplicationHelper.updateAggregatedClazzCommunication(application, commu);
	}

	/**
	 * Retrieves recursively all clazzCommunications for a component
	 *
	 * @param component
	 * @return
	 */
	public static List<ClazzCommunication> getChildrenComponentClazzCommunications(final Component component) {

		final List<ClazzCommunication> outgoingClazzCommunicationPartialList = new ArrayList<ClazzCommunication>();
		// get children components -> recursive
		for (final Component child : component.getChildren()) {
			if (!child.getChildren().isEmpty()) {
				outgoingClazzCommunicationPartialList.addAll(getChildrenComponentClazzCommunications(child));
			}
		}
		// get clazz communications
		for (final Clazz clazz : component.getClazzes()) {
			for (final ClazzCommunication clazzCommunication : clazz.getOutgoingClazzCommunications()) {
				outgoingClazzCommunicationPartialList.add(clazzCommunication);
			}
		}

		return outgoingClazzCommunicationPartialList;
	}

}
