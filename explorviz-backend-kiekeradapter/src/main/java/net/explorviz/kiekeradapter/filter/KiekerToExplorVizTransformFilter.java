package net.explorviz.kiekeradapter.filter;

import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kieker.analysis.IProjectContext;
import kieker.analysis.plugin.annotation.InputPort;
import kieker.analysis.plugin.annotation.Plugin;
import kieker.analysis.plugin.filter.AbstractFilterPlugin;
import kieker.common.configuration.Configuration;
import kieker.common.record.flow.IEventRecord;
import kieker.common.record.flow.IInterfaceRecord;
import kieker.common.record.flow.IObjectRecord;
import kieker.common.record.flow.trace.operation.AfterOperationEvent;
import kieker.common.record.flow.trace.operation.AfterOperationFailedEvent;
import kieker.common.record.flow.trace.operation.BeforeOperationEvent;
import kieker.common.record.flow.trace.operation.constructor.AfterConstructorEvent;
import kieker.common.record.flow.trace.operation.constructor.AfterConstructorFailedEvent;
import kieker.common.record.flow.trace.operation.constructor.BeforeConstructorEvent;
import net.explorviz.kiekeradapter.configuration.GenericExplorVizExternalLogAdapter;
import net.explorviz.kiekeradapter.main.KiekerAdapter;

/**
 * Kieker Analysis Filter: Transforms Kieker Records to ExplorViz Records
 * 
 * @author Christian Zirkelbach (czi@informatik.uni-kiel.de)
 *
 */
@Plugin(name = "Kieker to ExplorViz Transformator", description = "Transforms Kieker Records to ExplorViz Records", outputPorts = {}, configuration = {})
public class KiekerToExplorVizTransformFilter extends AbstractFilterPlugin {

	final Logger logger = LoggerFactory.getLogger(KiekerToExplorVizTransformFilter.class.getName());
	public static final String INPUT_PORT_NAME_KIEKER = "kieker";
	private final Configuration configuration;
	private final Stack<IEventRecord> stack;

	public KiekerToExplorVizTransformFilter(final Configuration configuration, final IProjectContext projectContext) {
		super(configuration, projectContext);
		this.configuration = configuration;
		stack = new Stack<IEventRecord>();
	}

	@Override
	public Configuration getCurrentConfiguration() {
		return configuration;
	}

	@InputPort(name = KiekerToExplorVizTransformFilter.INPUT_PORT_NAME_KIEKER, description = "", eventTypes = {
			IEventRecord.class })
	public void inputKiekerRecords(final IEventRecord kiekerRecord) {

		if (kiekerRecord instanceof BeforeConstructorEvent) {
			stack.push(kiekerRecord);
			final BeforeConstructorEvent kiekerBefore = (BeforeConstructorEvent) kiekerRecord;

			int objectId = 0;
			if (kiekerRecord instanceof IObjectRecord) {
				final IObjectRecord iObjectRecord = (IObjectRecord) kiekerRecord;
				objectId = iObjectRecord.getObjectId();
			}

			final String interfaceImpl = getInterface(kiekerRecord);

			GenericExplorVizExternalLogAdapter.sendBeforeConstructorRecord(kiekerBefore.getLoggingTimestamp(),
					kiekerBefore.getTraceId(), kiekerBefore.getOrderIndex(), objectId,
					convertSignatureToExplorViz(kiekerBefore), kiekerBefore.getClassSignature(), interfaceImpl);
		} else if (kiekerRecord instanceof AfterConstructorFailedEvent) {
			final AfterConstructorFailedEvent kiekerAfter = (AfterConstructorFailedEvent) kiekerRecord;

			long methodDuration = 0;
			if (!stack.isEmpty()) {
				final IEventRecord beforeRecord = stack.pop();
				if (beforeRecord instanceof BeforeConstructorEvent) {
					final BeforeConstructorEvent beforeConstructorEvent = (BeforeConstructorEvent) beforeRecord;
					methodDuration = kiekerAfter.getLoggingTimestamp() - beforeConstructorEvent.getLoggingTimestamp();
				}
			}

			GenericExplorVizExternalLogAdapter.sendAfterFailedConstructorRecord(kiekerAfter.getLoggingTimestamp(),
					methodDuration, kiekerAfter.getTraceId(), kiekerAfter.getOrderIndex(), kiekerAfter.getCause());
		} else if (kiekerRecord instanceof AfterConstructorEvent) {
			final AfterConstructorEvent kiekerAfter = (AfterConstructorEvent) kiekerRecord;

			long methodDuration = 0;
			if (!stack.isEmpty()) {
				final IEventRecord beforeRecord = stack.pop();
				if (beforeRecord instanceof BeforeConstructorEvent) {
					final BeforeConstructorEvent beforeConstructorEvent = (BeforeConstructorEvent) beforeRecord;
					methodDuration = kiekerAfter.getLoggingTimestamp() - beforeConstructorEvent.getLoggingTimestamp();
				}
			}

			GenericExplorVizExternalLogAdapter.sendAfterConstructorRecord(kiekerAfter.getLoggingTimestamp(),
					methodDuration, kiekerAfter.getTraceId(), kiekerAfter.getOrderIndex());
		} else if (kiekerRecord instanceof BeforeOperationEvent) {
			stack.push(kiekerRecord);
			final BeforeOperationEvent kiekerBefore = (BeforeOperationEvent) kiekerRecord;

			int objectId = 0;
			if (kiekerRecord instanceof IObjectRecord) {
				final IObjectRecord iObjectRecord = (IObjectRecord) kiekerRecord;
				objectId = iObjectRecord.getObjectId();
			}

			final String interfaceImpl = getInterface(kiekerRecord);

			GenericExplorVizExternalLogAdapter.sendBeforeRecord(kiekerBefore.getLoggingTimestamp(),
					kiekerBefore.getTraceId(), kiekerBefore.getOrderIndex(), objectId,
					convertSignatureToExplorViz(kiekerBefore), kiekerBefore.getClassSignature(), interfaceImpl);
		} else if (kiekerRecord instanceof AfterOperationFailedEvent) {
			final AfterOperationFailedEvent kiekerAfter = (AfterOperationFailedEvent) kiekerRecord;

			long methodDuration = 0;
			if (!stack.isEmpty()) {
				final IEventRecord beforeRecord = stack.pop();
				if (beforeRecord instanceof BeforeOperationEvent) {
					final BeforeOperationEvent beforeOperationEvent = (BeforeOperationEvent) beforeRecord;
					methodDuration = kiekerAfter.getLoggingTimestamp() - beforeOperationEvent.getLoggingTimestamp();
				}
			}

			GenericExplorVizExternalLogAdapter.sendAfterFailedRecord(kiekerAfter.getLoggingTimestamp(), methodDuration,
					kiekerAfter.getTraceId(), kiekerAfter.getOrderIndex(), kiekerAfter.getCause());
		} else if (kiekerRecord instanceof AfterOperationEvent) {
			final AfterOperationEvent kiekerAfter = (AfterOperationEvent) kiekerRecord;

			long methodDuration = 0;
			if (!stack.isEmpty()) {
				final IEventRecord beforeRecord = stack.pop();
				if (beforeRecord instanceof BeforeOperationEvent) {
					final BeforeOperationEvent beforeOperationEvent = (BeforeOperationEvent) beforeRecord;
					methodDuration = kiekerAfter.getLoggingTimestamp() - beforeOperationEvent.getLoggingTimestamp();
				}
			}

			GenericExplorVizExternalLogAdapter.sendAfterRecord(kiekerAfter.getLoggingTimestamp(), methodDuration,
					kiekerAfter.getTraceId(), kiekerAfter.getOrderIndex());
		}
	}

	private String getInterface(final IEventRecord kiekerRecord) {
		if (kiekerRecord instanceof IInterfaceRecord) {
			final IInterfaceRecord iInterfaceRecord = (IInterfaceRecord) kiekerRecord;
			final String interfaceImpl = iInterfaceRecord.getInterface();

			if (!interfaceImpl.isEmpty() && !interfaceImpl.equals("[]")) {
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

	private String convertSignatureToExplorViz(final BeforeOperationEvent kiekerBefore) {
		if (KiekerAdapter.getSignatureConverter() != null) {
			return KiekerAdapter.getSignatureConverter().convertSignatureToExplorViz(kiekerBefore);
		}

		return kiekerBefore.getOperationSignature();
	}
}