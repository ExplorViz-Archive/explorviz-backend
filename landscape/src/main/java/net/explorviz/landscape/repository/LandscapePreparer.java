package net.explorviz.landscape.repository;

import net.explorviz.shared.landscape.model.application.Application;
import net.explorviz.shared.landscape.model.application.ApplicationCommunication;
import net.explorviz.shared.landscape.model.application.Component;
import net.explorviz.shared.landscape.model.landscape.Landscape;
import net.explorviz.shared.landscape.model.landscape.Node;
import net.explorviz.shared.landscape.model.landscape.NodeGroup;
import net.explorviz.shared.landscape.model.landscape.System;


public final class LandscapePreparer {

  private LandscapePreparer() {
    // Utility Class
  }

  public static Landscape prepareLandscape(final Landscape landscape) {
    if (landscape == null) {
      final Landscape emptyLandscape = new Landscape();
      return emptyLandscape;
    }

    for (final System system : landscape.getSystems()) {
      for (final NodeGroup nodeGroup : system.getNodeGroups()) {
        for (final Node node : nodeGroup.getNodes()) {
          for (final Application application : node.getApplications()) {

            final Component foundationComponent = new Component(); // NOPMD
            // foundationComponent.setFoundation(true);
            // foundationComponent.setOpened(true);
            foundationComponent.setName(application.getName());
            foundationComponent.setFullQualifiedName(application.getName());
            foundationComponent.setBelongingApplication(application);

            foundationComponent.getChildren().addAll(application.getComponents());

            // for (final Component child : foundationComponent.getChildren()) {
            // setComponentAttributes(child, 0, true);
            // }

          }
        }

        // if (nodeGroup.getNodes().size() == 1) {
        // nodeGroup.setOpened(true);
        // } else {
        // nodeGroup.setOpened(false);
        // }
        nodeGroup.updateName();
      }
    }

    // outgoing communication between applications
    for (final ApplicationCommunication commu : landscape.getTotalApplicationCommunications()) {
      createOutgoingApplicationCommunication(commu);
    }
    return landscape;
  }

  private static void createOutgoingApplicationCommunication(
      final ApplicationCommunication communication) {
    final Application sourceApp = communication.getSourceApplication();
    if (sourceApp != null) {
      sourceApp.getApplicationCommunications().add(communication);
    }
  }

}
