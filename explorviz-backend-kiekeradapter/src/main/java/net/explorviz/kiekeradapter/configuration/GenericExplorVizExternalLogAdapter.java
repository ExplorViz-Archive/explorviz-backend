package net.explorviz.kiekeradapter.configuration;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import explorviz.live_trace_processing.main.MonitoringController;
import explorviz.live_trace_processing.main.MonitoringStringRegistry;
import explorviz.live_trace_processing.record.event.constructor.AfterConstructorEventRecord;
import explorviz.live_trace_processing.record.event.constructor.AfterFailedConstructorEventRecord;
import explorviz.live_trace_processing.record.event.constructor.BeforeConstructorEventRecord;
import explorviz.live_trace_processing.record.event.jdbc.AfterFailedJDBCOperationEventRecord;
import explorviz.live_trace_processing.record.event.jdbc.AfterJDBCOperationEventRecord;
import explorviz.live_trace_processing.record.event.jdbc.BeforeJDBCOperationEventRecord;
import explorviz.live_trace_processing.record.event.normal.AfterFailedOperationEventRecord;
import explorviz.live_trace_processing.record.event.normal.AfterOperationEventRecord;
import explorviz.live_trace_processing.record.event.normal.BeforeOperationEventRecord;
import explorviz.live_trace_processing.record.event.statics.AfterFailedStaticOperationEventRecord;
import explorviz.live_trace_processing.record.event.statics.AfterStaticOperationEventRecord;
import explorviz.live_trace_processing.record.event.statics.BeforeStaticOperationEventRecord;

/**
 * Prepares the ByteBuffer and sends out the buffer to the backend
 * 
 * @author Christian Zirkelbach (czi@informatik.uni-kiel.de)
 *
 */
public class GenericExplorVizExternalLogAdapter {

	static final Logger logger = LoggerFactory.getLogger(GenericExplorVizExternalLogAdapter.class.getName());

	public static boolean replayInRealTime = false;
	private static final ByteBuffer explorVizBuffer;
	private static long firstTimestamp = -1;
	private static long firstWallclockTimestamp;

	static {
		// Defines the size of the ByteBuffer: Needs to match the largest record
		// Check record size, if a new record is added!
		explorVizBuffer = ByteBuffer
				.allocateDirect(BeforeJDBCOperationEventRecord.COMPRESSED_BYTE_LENGTH_WITH_CLAZZ_ID);
	}

	public static void sendBeforeRecord(final long timestamp, final long traceId, final int orderIndex,
			final int objectId, final String operationSignature, final String clazz, final String interfaceImpl) {
		sendBeforeGeneric(BeforeOperationEventRecord.CLAZZ_ID, timestamp, traceId, orderIndex, objectId,
				operationSignature, clazz, interfaceImpl);
	}

	private static void sendBeforeGeneric(final byte ID, final long timestamp, final long traceId, final int orderIndex,
			final int objectId, final String operationSignature, final String clazz,
			final String implementedInterface) {
		explorVizBuffer.put(ID);
		explorVizBuffer.putLong(traceId);
		explorVizBuffer.putInt(orderIndex);
		explorVizBuffer.putInt(objectId);
		explorVizBuffer.putInt(MonitoringStringRegistry.getIdForString(operationSignature));
		explorVizBuffer.putInt(MonitoringStringRegistry.getIdForString(clazz));
		if (implementedInterface != null) {
			explorVizBuffer.putInt(MonitoringStringRegistry.getIdForString(implementedInterface));
		}

		sendBufferIfHasElements(timestamp);
	}

	private static void sendBufferIfHasElements(final long timestamp) {
		if (explorVizBuffer.position() > 0) {
			if (firstTimestamp == -1) {
				firstTimestamp = timestamp;
				firstWallclockTimestamp = System.nanoTime();
			} else {
				final long passedTime = timestamp - firstTimestamp;
				// System.out.println("Replaying timestamp " + timestamp +
				// " passed time "
				// + passedTime);
				while (replayInRealTime && System.nanoTime() - firstWallclockTimestamp < passedTime) {
					if (passedTime > 1000L * 1000L) {
						try {
							Thread.sleep(1000L);
						} catch (final InterruptedException e) {
						}
					}
				}
			}

			MonitoringController.sendOutBuffer(explorVizBuffer);
			explorVizBuffer.clear();
		}
	}

	public static void sendAfterRecord(final long timestamp, final long methodDuration, final long traceId,
			final int orderIndex) {
		sendAfterGeneric(AfterOperationEventRecord.CLAZZ_ID, timestamp, methodDuration, traceId, orderIndex);
	}

	private static void sendAfterGeneric(final byte ID, final long timestamp, final long methodDuration,
			final long traceId, final int orderIndex) {
		explorVizBuffer.put(ID);
		explorVizBuffer.putLong(methodDuration);
		explorVizBuffer.putLong(traceId);
		explorVizBuffer.putInt(orderIndex);

		sendBufferIfHasElements(timestamp);
	}

	public static void sendAfterFailedRecord(final long timestamp, final long methodDuration, final long traceId,
			final int orderIndex, final String cause) {
		sendAfterFailedGeneric(AfterFailedOperationEventRecord.CLAZZ_ID, timestamp, methodDuration, traceId, orderIndex,
				cause);
	}

	private static void sendAfterFailedGeneric(final byte ID, final long timestamp, final long methodDuration,
			final long traceId, final int orderIndex, final String cause) {
		explorVizBuffer.put(ID);
		explorVizBuffer.putLong(methodDuration);
		explorVizBuffer.putLong(traceId);
		explorVizBuffer.putInt(orderIndex);
		explorVizBuffer.putInt(MonitoringStringRegistry.getIdForString(cause));

		sendBufferIfHasElements(timestamp);
	}

	public static void sendBeforeConstructorRecord(final long timestamp, final long traceId, final int orderIndex,
			final int objectId, final String operationSignature, final String clazz, final String interfaceImpl) {
		sendBeforeGeneric(BeforeConstructorEventRecord.CLAZZ_ID, timestamp, traceId, orderIndex, objectId,
				operationSignature, clazz, interfaceImpl);
	}

	public static void sendAfterConstructorRecord(final long timestamp, final long methodDuration, final long traceId,
			final int orderIndex) {
		sendAfterGeneric(AfterConstructorEventRecord.CLAZZ_ID, timestamp, methodDuration, traceId, orderIndex);
	}

	public static void sendAfterFailedConstructorRecord(final long timestamp, final long methodDuration,
			final long traceId, final int orderIndex, final String cause) {
		sendAfterFailedGeneric(AfterFailedConstructorEventRecord.CLAZZ_ID, timestamp, methodDuration, traceId,
				orderIndex, cause);
	}

	public static void sendBeforeStaticRecord(final long timestamp, final long traceId, final int orderIndex,
			final String operationSignature, final String clazz, final String implementedInterface) {
		sendBeforeGeneric(BeforeStaticOperationEventRecord.CLAZZ_ID, timestamp, traceId, orderIndex, 0,
				operationSignature, clazz, implementedInterface);
	}

	public static void sendAfterStaticRecord(final long timestamp, final long methodDuration, final long traceId,
			final int orderIndex) {
		sendAfterGeneric(AfterStaticOperationEventRecord.CLAZZ_ID, timestamp, methodDuration, traceId, orderIndex);
	}

	public static void sendAfterFailedStaticRecord(final long timestamp, final long methodDuration, final long traceId,
			final int orderIndex, final String cause) {
		sendAfterFailedGeneric(AfterFailedStaticOperationEventRecord.CLAZZ_ID, timestamp, methodDuration, traceId,
				orderIndex, cause);
	}

	// DatabaseEvents
	public static void sendBeforeDatabaseEvent(long loggingTimestamp, String classSignature, long traceId,
			int orderIndex, int objectId, String interfaceImpl, String parameters, String technology) {

		explorVizBuffer.put(BeforeJDBCOperationEventRecord.CLAZZ_ID);
		explorVizBuffer.putLong(traceId);
		explorVizBuffer.putInt(orderIndex);
		explorVizBuffer.putInt(objectId);
		explorVizBuffer.putInt(MonitoringStringRegistry.getIdForString(classSignature));
		// TODO Extract class? // clazz
		explorVizBuffer.putInt(MonitoringStringRegistry.getIdForString(classSignature));
		if (interfaceImpl != null) {
			explorVizBuffer.putInt(MonitoringStringRegistry.getIdForString(interfaceImpl));
		}

		explorVizBuffer.putInt(MonitoringStringRegistry.getIdForString(parameters));
		// TODO Add technology

		sendBufferIfHasElements(loggingTimestamp);
	}

	public static void sendAfterDatabaseEvent(long loggingTimestamp, long methodDuration, String classSignature,
			long traceId, int orderIndex, String returnType, String returnValue) {

		explorVizBuffer.put(AfterJDBCOperationEventRecord.CLAZZ_ID);
		explorVizBuffer.putLong(methodDuration);
		explorVizBuffer.putLong(traceId);
		explorVizBuffer.putInt(orderIndex);
		// TODO Add returnType
		explorVizBuffer.putInt(MonitoringStringRegistry.getIdForString(returnValue));

		sendBufferIfHasElements(loggingTimestamp);
	}

	public static void sendDatabaseFailedEvent(long loggingTimestamp, long methodDuration, String classSignature,
			long traceId, int orderIndex, String cause) {

		explorVizBuffer.put(AfterFailedJDBCOperationEventRecord.CLAZZ_ID);
		explorVizBuffer.putLong(methodDuration);
		explorVizBuffer.putLong(traceId);
		explorVizBuffer.putInt(orderIndex);
		explorVizBuffer.putInt(MonitoringStringRegistry.getIdForString(cause));

		sendBufferIfHasElements(loggingTimestamp);
	}

}
