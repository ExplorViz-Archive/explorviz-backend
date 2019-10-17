package net.explorviz.landscape.model.helper;

import java.util.ArrayList;
import java.util.List;
import net.explorviz.landscape.model.application.AggregatedClazzCommunication;
import net.explorviz.landscape.model.application.Application;
import net.explorviz.landscape.model.application.Clazz;
import net.explorviz.landscape.model.application.ClazzCommunication;
import net.explorviz.landscape.model.application.Component;

/**
 * Helper class for several model classes.
 */
public final class ModelHelper {

  private ModelHelper() {
    // Utility Class
  }

  /**
   * Adds a clazz communication or runtime information to a specific clazz within an application.
   *
   * @param caller - Calling clazz
   * @param callee - Called clazz
   * @param application - Related application
   * @param requests - Amount of observed calls
   * @param averageResponseTime - Average response time in ns
   * @param overallTraceDuration - In ns
   * @param traceId - Of the reconstructed trace
   * @param tracePosition - Position within the trace
   * @param operationName - Name of the called operation
   */
  public static void addClazzCommunication(final String potentialNewTraceId,
      final String potentialNewClazzCommuId, final String potentialNewAggClazzCommuId,
      final String traceStepId, final Clazz caller, final Clazz callee,
      final Application application, final int requests, final double averageResponseTime,
      final double overallTraceDuration, final String traceId, final int tracePosition,
      final String operationName) {

    // clazzCommunication already exists
    for (final ClazzCommunication commu : caller.getClazzCommunications()) {
      if (commu.getSourceClazz() == caller && commu.getTargetClazz() == callee
          && commu.getOperationName().equalsIgnoreCase(operationName)) {

        final float currentAverageResponseTime = commu.getAverageResponseTime();
        commu.setAverageResponseTime(
            (currentAverageResponseTime + (float) averageResponseTime) / 2f);
        final int newTotalRequests = commu.getTotalRequests() + requests;
        commu.setTotalRequests(newTotalRequests);
        commu.addTraceStep(potentialNewTraceId, traceStepId, application, traceId, tracePosition,
            requests, (float) averageResponseTime, (float) overallTraceDuration);

        return;
      }
    }

    // create new clazzCommunication
    final ClazzCommunication commu = new ClazzCommunication(potentialNewClazzCommuId);
    commu.setSourceClazz(caller);
    commu.setTargetClazz(callee);
    commu.setOperationName(operationName);
    commu.setAverageResponseTime((float) averageResponseTime);
    commu.setTotalRequests(requests);
    commu.addTraceStep(potentialNewTraceId, traceStepId, application, traceId, tracePosition,
        requests, (float) averageResponseTime, (float) overallTraceDuration);

    // add clazzCommunication to calling clazz (sourceClazz)
    caller.getClazzCommunications().add(commu);

    // add aggregatedClazzCommunication to application
    ModelHelper.addAggregatedClazzCommunication(potentialNewAggClazzCommuId, application, commu);
  }

  /**
   * Retrieves recursively all clazzCommunications for a component.
   *
   * @param component - The passed component
   * @return List of ClazzCommunication
   */
  public static List<ClazzCommunication> getChildrenComponentClazzCommunications(
      final Component component) {

    final List<ClazzCommunication> outgoingClazzCommuPartialList = new ArrayList<>();
    // get children components -> recursive
    for (final Component child : component.getChildren()) {
      if (!child.getChildren().isEmpty()) {
        outgoingClazzCommuPartialList.addAll(getChildrenComponentClazzCommunications(child));
      }

      for (final Clazz clazz : child.getClazzes()) {
        for (final ClazzCommunication clazzCommunication : clazz.getClazzCommunications()) {
          outgoingClazzCommuPartialList.add(clazzCommunication);
        }
      }
    }
    // get clazz communications
    for (final Clazz clazz : component.getClazzes()) {
      for (final ClazzCommunication clazzCommunication : clazz.getClazzCommunications()) {
        outgoingClazzCommuPartialList.add(clazzCommunication);
      }
    }

    return outgoingClazzCommuPartialList;
  }

  /**
   * Retrieves recursively all clazzes for a component.
   *
   * @param component - The passed component
   * @return List of Clazz
   */
  public static List<Clazz> getChildrenComponentClazzes(final Component component) {

    final List<Clazz> retrievedClazzes = new ArrayList<>();
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
   * Adds a clazzCommunication to a matching aggregatedClazzCommunication or creates a new one.
   *
   * @param application - Related application
   * @param newCommunication - The ClazzCommunication which should be added
   */
  public static void addAggregatedClazzCommunication(final String aggregatedCommuId,
      final Application application, final ClazzCommunication newCommunication) {

    final List<AggregatedClazzCommunication> aggregatedClazzCommunications =
        application.getAggregatedClazzCommunications();

    // check if a matching aggregatedClazzCommunication already exists
    for (final AggregatedClazzCommunication aggClazzCommu : aggregatedClazzCommunications) {
      if (aggClazzCommu.getSourceClazz().equals(newCommunication.getSourceClazz())
          && aggClazzCommu.getTargetClazz().equals(newCommunication.getTargetClazz())) {
        aggClazzCommu.addClazzCommunication(newCommunication);

        final float currentAverageResponseTime = aggClazzCommu.getAverageResponseTime();
        aggClazzCommu.setAverageResponseTime(
            (currentAverageResponseTime + newCommunication.getAverageResponseTime()) / 2f);
        final int newTotalRequests =
            aggClazzCommu.getTotalRequests() + newCommunication.getTotalRequests();
        aggClazzCommu.setTotalRequests(newTotalRequests);

        return;
      }
    }

    // creates a new aggregatedClazzCommunication
    final AggregatedClazzCommunication aggCommu =
        new AggregatedClazzCommunication(aggregatedCommuId);
    aggCommu.setSourceClazz(newCommunication.getSourceClazz());
    aggCommu.setTargetClazz(newCommunication.getTargetClazz());
    aggCommu.setAverageResponseTime(newCommunication.getAverageResponseTime());
    aggCommu.setTotalRequests(newCommunication.getTotalRequests());

    // adds a clazzCommunication if sourceClazz and targetClazz matches
    if (aggCommu.addClazzCommunication(newCommunication)) {
      aggregatedClazzCommunications.add(aggCommu);

    }
  }

}
