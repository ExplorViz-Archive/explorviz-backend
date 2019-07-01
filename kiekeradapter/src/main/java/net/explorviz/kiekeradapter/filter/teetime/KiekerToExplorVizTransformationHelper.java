package net.explorviz.kiekeradapter.filter.teetime;

import kieker.common.record.IMonitoringRecord;
import kieker.common.record.flow.IInterfaceRecord;
import kieker.common.record.flow.trace.operation.BeforeOperationEvent;
import net.explorviz.kiekeradapter.main.KiekerAdapter;

/**
 * Kieker Analysis Transformation Filter Helper.
 */
public final class KiekerToExplorVizTransformationHelper {

  private KiekerToExplorVizTransformationHelper() {
    // Utility class
  }

  public static String getInterface(final IMonitoringRecord kiekerRecord) {
    if (kiekerRecord instanceof IInterfaceRecord) {
      final IInterfaceRecord iInterfaceRecord = (IInterfaceRecord) kiekerRecord;
      final String interfaceImpl = iInterfaceRecord.getInterface();

      if (!interfaceImpl.isEmpty() && !"[]".equals(interfaceImpl)) {
        final int indexOfSeperator = interfaceImpl.indexOf(", ");

        if (indexOfSeperator > 0) {
          return interfaceImpl.substring(1, indexOfSeperator);
        } else {
          return interfaceImpl.substring(1, interfaceImpl.length() - 1);
        }
      }
    }
    return "";
  }

  public static String convertSignatureToExplorViz(final BeforeOperationEvent kiekerBefore) {
    if (KiekerAdapter.getSignatureConverter() != null) {
      return KiekerAdapter.getSignatureConverter().convertSignatureToExplorViz(kiekerBefore);
    }

    return kiekerBefore.getOperationSignature();
  }

}
