package net.explorviz.kiekeradapter.configuration;

import kieker.common.record.flow.trace.operation.BeforeOperationEvent;

/**
 * Interface providing a signature converter for Explorviz records.
 */
public interface SignatureConverter {
  String convertSignatureToExplorViz(final BeforeOperationEvent kiekerBefore);
}
