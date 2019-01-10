package net.explorviz.landscape.repository.persistence;

import net.explorviz.landscape.model.application.AggregatedClazzCommunication;
import net.explorviz.landscape.model.application.Application;
import net.explorviz.landscape.model.application.ApplicationCommunication;
import net.explorviz.landscape.model.application.Clazz;
import net.explorviz.landscape.model.application.ClazzCommunication;
import net.explorviz.landscape.model.application.Component;
import net.explorviz.landscape.model.application.DatabaseQuery;
import net.explorviz.landscape.model.application.TraceStep;
import net.explorviz.landscape.model.helper.EProgrammingLanguage;
import net.explorviz.landscape.model.landscape.Landscape;
import net.explorviz.landscape.model.landscape.Node;
import net.explorviz.landscape.model.landscape.NodeGroup;
import net.explorviz.landscape.model.landscape.System;
import org.nustaq.serialization.FSTConfiguration;

/**
 * Helper class for FST.
 *
 *
 */
public class FstHelper {

  /**
   * Creates a new {@link FSTConfiguration} and registers all available model classes.
   *
   * @return the configuration.
   */
  public static FSTConfiguration createFstConfiguration() {
    final FSTConfiguration result = FSTConfiguration.createDefaultConfiguration();
    result.registerClass(Landscape.class);
    result.registerClass(System.class);
    result.registerClass(NodeGroup.class);
    result.registerClass(Node.class);
    result.registerClass(Application.class);
    result.registerClass(ApplicationCommunication.class);
    result.registerClass(AggregatedClazzCommunication.class);
    result.registerClass(DatabaseQuery.class);
    result.registerClass(EProgrammingLanguage.class);
    result.registerClass(Component.class);
    result.registerClass(Clazz.class);
    result.registerClass(ClazzCommunication.class);
    result.registerClass(TraceStep.class);

    return result;
  }


  /**
   * Converts a landscape object to bytes.
   *
   * @param landscape the landscape object
   *
   * @return the given landscape as byte array
   */
  public static byte[] convertLandscapeToBytes(final Landscape landscape,
      final FSTConfiguration conf) {
    // TODO: Move to "ExportHelper"
    return conf.asByteArray(landscape);
  }

}
