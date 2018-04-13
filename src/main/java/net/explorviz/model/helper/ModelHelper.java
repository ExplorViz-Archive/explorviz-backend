package net.explorviz.model.helper;

import java.util.ArrayList;
import java.util.List;

import net.explorviz.model.application.AggregatedClazzCommunication;
import net.explorviz.model.application.Application;
import net.explorviz.model.application.Clazz;
import net.explorviz.model.application.ClazzCommunication;
import net.explorviz.model.application.Component;
import net.explorviz.model.application.CumulatedClazzCommunication;

/**
 * Helper class for several model classes
 *
 * @author Christian Zirkelbach (czi@informatik.uni-kiel.de)
 *
 */
public final class ModelHelper {

	/**
	 * Adds a clazz communication or runtime information to a specific clazz within
	 * an application
	 *
	 * @param caller
	 *            (calling clazz)
	 * @param callee
	 *            (called clazz)
	 * @param application
	 *            (related application)
	 * @param requests
	 *            (amount of observed calls)
	 * @param average
	 *            (average response time in ns)
	 * @param overallTraceDuration
	 *            (in ns)
	 * @param traceId
	 *            (of the reconstructed trace)
	 * @param orderIndex
	 *            (position within the trace)
	 * @param operationName
	 *            (name of the called operation)
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

		// add aggregatedClazzCommunication to application
		ModelHelper.updateAggregatedClazzCommunication(application, commu);
	}

	/**
	 * Retrieves recursively all clazzCommunications for a component
	 *
	 * @param component
	 *            (the passed component)
	 * @return List of ClazzCommunication
	 */
	public static List<ClazzCommunication> getChildrenComponentClazzCommunications(final Component component) {

		final List<ClazzCommunication> outgoingClazzCommunicationPartialList = new ArrayList<ClazzCommunication>();
		// get children components -> recursive
		for (final Component child : component.getChildren()) {
			if (!child.getChildren().isEmpty()) {
				outgoingClazzCommunicationPartialList.addAll(getChildrenComponentClazzCommunications(child));
			}

			for (final Clazz clazz : child.getClazzes()) {
				for (final ClazzCommunication clazzCommunication : clazz.getOutgoingClazzCommunications()) {
					outgoingClazzCommunicationPartialList.add(clazzCommunication);
				}
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

	/**
	 * Retrieves recursively all clazzes for a component
	 *
	 * @param component
	 *            (the passed component)
	 * @return List of Clazz
	 */
	public static List<Clazz> getChildrenComponentClazzes(final Component component) {

		final List<Clazz> retrievedClazzes = new ArrayList<Clazz>();
		// get children components -> recursive
		for (final Component child : component.getChildren()) {
			if (!child.getChildren().isEmpty()) {
				retrievedClazzes.addAll(getChildrenComponentClazzes(child));
			}
			for (final Clazz clazz : child.getClazzes()) {
				retrievedClazzes.add(clazz);
			}
		}
		// get clazz communications
		for (final Clazz clazz : component.getClazzes()) {
			retrievedClazzes.add(clazz);
		}

		return retrievedClazzes;
	}

	/**
	 * Adds a clazzCommunication to a matching aggregatedClazzCommunication or
	 * creates a new one
	 *
	 * @param application
	 *            (related application)
	 * @param newCommunication
	 *            (the ClazzCommunication which should be added)
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
			updateCumulatedClazzCommunication(application, aggCommu);
		}
	}

	/**
	 * Adds an aggregatedClazzCommunication to a matching
	 * cumulatedClazzCommunication or creates a new one
	 *
	 * @param application
	 *            (related application)
	 * @param newCommunication
	 *            (the AggregatedClazzCommunication which should be added)
	 */
	public static void updateCumulatedClazzCommunication(final Application application,
			final AggregatedClazzCommunication newCommunication) {
		final List<CumulatedClazzCommunication> cumulatedClazzCommunications = application
				.getCumulatedClazzCommunications();

		// matching aggregatedClazzCommunication already exists
		for (final CumulatedClazzCommunication aggClazzCommu : cumulatedClazzCommunications) {
			if ((aggClazzCommu.getSourceClazz().equals(newCommunication.getSourceClazz())
					&& aggClazzCommu.getTargetClazz().equals(newCommunication.getTargetClazz()))
					|| (aggClazzCommu.getTargetClazz().equals(newCommunication.getSourceClazz())
							&& aggClazzCommu.getSourceClazz().equals(newCommunication.getTargetClazz()))) {
				aggClazzCommu.addAggregatedClazzCommunication(newCommunication);
				return;
			}
		}

		// creates a new aggregatedClazzCommunication
		final CumulatedClazzCommunication aggCommu = new CumulatedClazzCommunication();
		aggCommu.initializeID();
		aggCommu.setSourceClazz(newCommunication.getSourceClazz());
		aggCommu.setTargetClazz(newCommunication.getTargetClazz());
		aggCommu.setRequests(newCommunication.getRequests());

		// adds a clazzCommunication if sourceClazz and targetClazz matches
		if (aggCommu.addAggregatedClazzCommunication(newCommunication)) {
			cumulatedClazzCommunications.add(aggCommu);
		}
	}

}
