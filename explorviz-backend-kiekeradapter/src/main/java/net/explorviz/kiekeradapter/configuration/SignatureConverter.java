package net.explorviz.kiekeradapter.configuration;

import kieker.common.record.flow.trace.operation.BeforeOperationEvent;

/**
 * Interface providing a signature converter for Explorviz records
 * @author Christian Zirkelbach (czi@informatik.uni-kiel.de)
 *
 */
public interface SignatureConverter {
	public String convertSignatureToExplorViz(final BeforeOperationEvent kiekerBefore);
}
