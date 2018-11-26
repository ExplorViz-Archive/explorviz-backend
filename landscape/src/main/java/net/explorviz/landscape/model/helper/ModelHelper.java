package net.explorviz.landscape.model.helper;

import java.util.ArrayList;
import java.util.List;
import net.explorviz.landscape.model.application.Application;
import net.explorviz.landscape.model.application.BidrectionalClazzCommunication;
import net.explorviz.landscape.model.application.Clazz;
import net.explorviz.landscape.model.application.ClazzCommunication;
import net.explorviz.landscape.model.application.Component;
import net.explorviz.landscape.model.application.UnidirectionalClazzCommunication;

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
   * @param average - Average response time in ns
   * @param overallTraceDuration - In ns
   * @param traceId - Of the reconstructed trace
   * @param tracePosition - Position within the trace
   * @param operationName - Name of the called operation
   */
  public static void addClazzCommunication(final Clazz caller, final Clazz callee,
      final Application application, final int requests, final double average,
      final double overallTraceDuration, final long traceId, final int tracePosition,
      final String operationName) {

    // clazzCommunication already exists
    for (final ClazzCommunication commu : caller.getOutgoingClazzCommunications()) {
      if (commu.getSourceClazz() == caller && commu.getTargetClazz() == callee
          && commu.getOperationName().equalsIgnoreCase(operationName)) {

        commu.addTrace(traceId, tracePosition, requests, (float) average,
            (float) overallTraceDuration);
        return;
      }
    }

    // create new clazzCommunication
    final ClazzCommunication commu = new ClazzCommunication();
    commu.initializeId();
    commu.setSourceClazz(caller);
    commu.setTargetClazz(callee);
    commu.setOperationName(operationName);
    commu.addTrace(traceId, tracePosition, requests, (float) average, (float) overallTraceDuration);

    // add clazzCommunication to calling clazz (sourceClazz)
    caller.getOutgoingClazzCommunications().add(commu);

    // add aggregatedClazzCommunication to application
    ModelHelper.updateAggregatedClazzCommunication(application, commu);
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
        for (final ClazzCommunication clazzCommunication : clazz.getOutgoingClazzCommunications()) {
          outgoingClazzCommuPartialList.add(clazzCommunication);
        }
      }
    }
    // get clazz communications
    for (final Clazz clazz : component.getClazzes()) {
      for (final ClazzCommunication clazzCommunication : clazz.getOutgoingClazzCommunications()) {
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
  public static void updateAggregatedClazzCommunication(final Application application,
      final ClazzCommunication newCommunication) {
    final List<UnidirectionalClazzCommunication> aggregatedOutgoingClazzCommu =
        application.getAggregatedOutgoingClazzCommunications();

    // matching aggregatedClazzCommunication already exists
    for (final UnidirectionalClazzCommunication aggClazzCommu : aggregatedOutgoingClazzCommu) {
      if (aggClazzCommu.getSourceClazz().equals(newCommunication.getSourceClazz())
          && aggClazzCommu.getTargetClazz().equals(newCommunication.getTargetClazz())) {
        aggClazzCommu.addClazzCommunication(newCommunication);
        return;
      }
    }

    // creates a new aggregatedClazzCommunication
    final UnidirectionalClazzCommunication aggCommu = new UnidirectionalClazzCommunication();
    aggCommu.initializeId();
    aggCommu.setSourceClazz(newCommunication.getSourceClazz());
    aggCommu.setTargetClazz(newCommunication.getTargetClazz());
    aggCommu.setRequests(newCommunication.getTotalRequests());

    // adds a clazzCommunication if sourceClazz and targetClazz matches
    if (aggCommu.addClazzCommunication(newCommunication)) {
      aggregatedOutgoingClazzCommu.add(aggCommu);
      updateCumulatedClazzCommunication(application, aggCommu);
    }
  }

  /**
   * Adds an aggregatedClazzCommunication to a matching cumulatedClazzCommunication or creates a new
   * one.
   *
   * @param application - Related application
   * @param newCommunication - The AggregatedClazzCommunication which should be added
   */
  public static void updateCumulatedClazzCommunication(final Application application,
      final UnidirectionalClazzCommunication newCommunication) {
    final List<BidrectionalClazzCommunication> cumulatedClazzCommunications =
        application.getCumulatedClazzCommunications();

    // matching aggregatedClazzCommunication already exists
    for (final BidrectionalClazzCommunication aggClazzCommu : cumulatedClazzCommunications) {
      if (aggClazzCommu.getSourceClazz().equals(newCommunication.getSourceClazz())
          && aggClazzCommu.getTargetClazz().equals(newCommunication.getTargetClazz())
          || aggClazzCommu.getTargetClazz().equals(newCommunication.getSourceClazz())
              && aggClazzCommu.getSourceClazz().equals(newCommunication.getTargetClazz())) {
        aggClazzCommu.addUnidirectionalClazzCommunication(newCommunication);
        return;
      }
    }

    // creates a new aggregatedClazzCommunication
    final BidrectionalClazzCommunication aggCommu = new BidrectionalClazzCommunication();
    aggCommu.initializeId();
    aggCommu.setSourceClazz(newCommunication.getSourceClazz());
    aggCommu.setTargetClazz(newCommunication.getTargetClazz());
    aggCommu.setRequests(newCommunication.getRequests());

    // adds a clazzCommunication if sourceClazz and targetClazz matches
    if (aggCommu.addUnidirectionalClazzCommunication(newCommunication)) {
      cumulatedClazzCommunications.add(aggCommu);
    }
  }

}
