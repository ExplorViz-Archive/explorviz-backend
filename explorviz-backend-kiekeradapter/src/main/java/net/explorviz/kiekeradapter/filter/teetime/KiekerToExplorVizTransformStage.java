package net.explorviz.kiekeradapter.filter.teetime;

import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kieker.common.record.IMonitoringRecord;
import kieker.common.record.database.AfterDatabaseEvent;
import kieker.common.record.database.BeforeDatabaseEvent;
import kieker.common.record.database.DatabaseFailedEvent;
import kieker.common.record.flow.IObjectRecord;
import kieker.common.record.flow.trace.ApplicationTraceMetadata;
import kieker.common.record.flow.trace.operation.AfterOperationEvent;
import kieker.common.record.flow.trace.operation.AfterOperationFailedEvent;
import kieker.common.record.flow.trace.operation.BeforeOperationEvent;
import kieker.common.record.flow.trace.operation.constructor.AfterConstructorEvent;
import kieker.common.record.flow.trace.operation.constructor.AfterConstructorFailedEvent;
import kieker.common.record.flow.trace.operation.constructor.BeforeConstructorEvent;
import kieker.common.record.system.CPUUtilizationRecord;
import kieker.common.record.system.MemSwapUsageRecord;
import net.explorviz.kiekeradapter.configuration.GenericExplorVizExternalLogAdapter;
import teetime.framework.AbstractConsumerStage;

/**
 * Kieker Analysis Filter: Transforms Kieker Records to ExplorViz Records
 * 
 * @author Christian Zirkelbach (czi@informatik.uni-kiel.de)
 *
 */
public class KiekerToExplorVizTransformStage extends AbstractConsumerStage<IMonitoringRecord> {

	static final Logger logger = LoggerFactory.getLogger(KiekerToExplorVizTransformStage.class.getName());
	private final Stack<IMonitoringRecord> stack = new Stack<IMonitoringRecord>();

	@Override
	protected void execute(final IMonitoringRecord element) throws Exception {
		inputKiekerRecords(element);
	}

	public void inputKiekerRecords(final IMonitoringRecord kiekerRecord) {

		// TODO Is a generic KiekerMetaDataRecord necessary?

		// ApplicationTraceMetadata (determine the belonging hostname and
		// applicationName)
		if (kiekerRecord instanceof ApplicationTraceMetadata) {
			final ApplicationTraceMetadata kiekerMetaDataRecord = (ApplicationTraceMetadata) kiekerRecord;

			// TODO workaround until those information is gathered via Kieker or provided
			// otherwise
			final String systemName = "System";
			final String ipAddress = kiekerMetaDataRecord.getHostname();
			final String programmingLanguage = "";

			GenericExplorVizExternalLogAdapter.sendApplicationTraceMetaDataRecord(
					kiekerMetaDataRecord.getLoggingTimestamp(), systemName, ipAddress,
					kiekerMetaDataRecord.getHostname(), kiekerMetaDataRecord.getApplicationName(), programmingLanguage);
		} else if (kiekerRecord instanceof CPUUtilizationRecord) {
			final CPUUtilizationRecord kiekerCPUUtilRecord = (CPUUtilizationRecord) kiekerRecord;
			final String hostname = kiekerCPUUtilRecord.getHostname();

			GenericExplorVizExternalLogAdapter.sendSystemMonitoringRecord(kiekerCPUUtilRecord.getLoggingTimestamp(),
					hostname, kiekerCPUUtilRecord.getTotalUtilization(), 0, 0);
		} else if (kiekerRecord instanceof MemSwapUsageRecord) {
			final MemSwapUsageRecord kiekerMemUsageRecord = (MemSwapUsageRecord) kiekerRecord;
			final String hostname = kiekerMemUsageRecord.getHostname();

			GenericExplorVizExternalLogAdapter.sendSystemMonitoringRecord(kiekerMemUsageRecord.getLoggingTimestamp(),
					hostname, 0, kiekerMemUsageRecord.getMemUsed(), kiekerMemUsageRecord.getMemTotal());
		} else if (kiekerRecord instanceof BeforeConstructorEvent) {
			stack.push(kiekerRecord);
			final BeforeConstructorEvent kiekerBefore = (BeforeConstructorEvent) kiekerRecord;

			int objectId = 0;
			if (kiekerRecord instanceof IObjectRecord) {
				final IObjectRecord iObjectRecord = (IObjectRecord) kiekerRecord;
				objectId = iObjectRecord.getObjectId();
			}

			final String interfaceImpl = KiekerToExplorVizTransformationHelper.getInterface(kiekerRecord);

			GenericExplorVizExternalLogAdapter.sendBeforeConstructorRecord(kiekerBefore.getLoggingTimestamp(),
					kiekerBefore.getTraceId(), kiekerBefore.getOrderIndex(), objectId,
					KiekerToExplorVizTransformationHelper.convertSignatureToExplorViz(kiekerBefore), kiekerBefore.getClassSignature(), interfaceImpl);
		} else if (kiekerRecord instanceof AfterConstructorFailedEvent) {
			final AfterConstructorFailedEvent kiekerAfter = (AfterConstructorFailedEvent) kiekerRecord;

			long methodDuration = 0;
			if (!stack.isEmpty()) {
				final IMonitoringRecord beforeRecord = stack.pop();
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
				final IMonitoringRecord beforeRecord = stack.pop();
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

			final String interfaceImpl = KiekerToExplorVizTransformationHelper.getInterface(kiekerRecord);

			GenericExplorVizExternalLogAdapter.sendBeforeRecord(kiekerBefore.getLoggingTimestamp(),
					kiekerBefore.getTraceId(), kiekerBefore.getOrderIndex(), objectId,
					KiekerToExplorVizTransformationHelper.convertSignatureToExplorViz(kiekerBefore), kiekerBefore.getClassSignature(), interfaceImpl);
		} else if (kiekerRecord instanceof AfterOperationFailedEvent) {
			final AfterOperationFailedEvent kiekerAfter = (AfterOperationFailedEvent) kiekerRecord;

			long methodDuration = 0;
			if (!stack.isEmpty()) {
				final IMonitoringRecord beforeRecord = stack.pop();
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
				final IMonitoringRecord beforeRecord = stack.pop();
				if (beforeRecord instanceof BeforeOperationEvent) {
					final BeforeOperationEvent beforeOperationEvent = (BeforeOperationEvent) beforeRecord;
					methodDuration = kiekerAfter.getLoggingTimestamp() - beforeOperationEvent.getLoggingTimestamp();
				}
			}

			GenericExplorVizExternalLogAdapter.sendAfterRecord(kiekerAfter.getLoggingTimestamp(), methodDuration,
					kiekerAfter.getTraceId(), kiekerAfter.getOrderIndex());
		}
		// DatabaseEvent records
		else if (kiekerRecord instanceof BeforeDatabaseEvent) {
			stack.push(kiekerRecord);
			final BeforeDatabaseEvent beforeDatabaseEvent = (BeforeDatabaseEvent) kiekerRecord;

			// TODO deprecated?
			int objectId = 0;
			if (kiekerRecord instanceof IObjectRecord) {
				final IObjectRecord iObjectRecord = (IObjectRecord) kiekerRecord;
				objectId = iObjectRecord.getObjectId();
			}

			// TODO deprecated?
			final String interfaceImpl = KiekerToExplorVizTransformationHelper.getInterface(kiekerRecord);

			GenericExplorVizExternalLogAdapter.sendBeforeDatabaseEvent(beforeDatabaseEvent.getLoggingTimestamp(),
					beforeDatabaseEvent.getClassSignature(), beforeDatabaseEvent.getTraceId(),
					beforeDatabaseEvent.getOrderIndex(), objectId, interfaceImpl, beforeDatabaseEvent.getParameters(),
					beforeDatabaseEvent.getTechnology());

		} else if (kiekerRecord instanceof AfterDatabaseEvent) {
			final AfterDatabaseEvent afterDatabaseEvent = (AfterDatabaseEvent) kiekerRecord;

			long methodDuration = 0;
			if (!stack.isEmpty()) {
				final IMonitoringRecord beforeRecord = stack.pop();
				if (beforeRecord instanceof BeforeDatabaseEvent) {
					final BeforeDatabaseEvent beforeDatabaseEvent = (BeforeDatabaseEvent) beforeRecord;
					methodDuration = afterDatabaseEvent.getLoggingTimestamp()
							- beforeDatabaseEvent.getLoggingTimestamp();
				}
			}
			GenericExplorVizExternalLogAdapter.sendAfterDatabaseEvent(afterDatabaseEvent.getLoggingTimestamp(),
					methodDuration, afterDatabaseEvent.getClassSignature(), afterDatabaseEvent.getTraceId(),
					afterDatabaseEvent.getOrderIndex(), afterDatabaseEvent.getReturnType(),
					afterDatabaseEvent.getReturnValue());
		} else if (kiekerRecord instanceof DatabaseFailedEvent) {
			final DatabaseFailedEvent databaseFailedEvent = (DatabaseFailedEvent) kiekerRecord;

			long methodDuration = 0;
			if (!stack.isEmpty()) {
				final IMonitoringRecord beforeRecord = stack.pop();
				if (beforeRecord instanceof BeforeDatabaseEvent) {
					final BeforeDatabaseEvent beforeOperationEvent = (BeforeDatabaseEvent) beforeRecord;
					methodDuration = databaseFailedEvent.getLoggingTimestamp()
							- beforeOperationEvent.getLoggingTimestamp();
				}
			}
			GenericExplorVizExternalLogAdapter.sendDatabaseFailedEvent(databaseFailedEvent.getLoggingTimestamp(),
					methodDuration, databaseFailedEvent.getClassSignature(), databaseFailedEvent.getTraceId(),
					databaseFailedEvent.getOrderIndex(), databaseFailedEvent.getCause());
		}
	}

}
