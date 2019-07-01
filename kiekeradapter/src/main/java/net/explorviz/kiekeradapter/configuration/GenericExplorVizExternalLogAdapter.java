package net.explorviz.kiekeradapter.configuration;

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
import explorviz.live_trace_processing.record.misc.SystemMonitoringRecord;
import explorviz.live_trace_processing.record.trace.HostApplicationMetaDataRecord;
import java.nio.ByteBuffer;

/**
 * Prepares the ByteBuffer and sends out the buffer to the backend.
 *
 */
public final class GenericExplorVizExternalLogAdapter {

  // private static final Logger LOGGER =
  // LoggerFactory.getLogger(GenericExplorVizExternalLogAdapter.class);

  // private static final boolean REPLAY_IN_REALTIME = false;
  private static final ByteBuffer EXPLORVIZ_BUFFER;
  private static long firstTimestamp = -1;
  // private static long firstWallclockTimestamp;

  private GenericExplorVizExternalLogAdapter() {
    // private constructor
  }

  static {
    // Defines the size of the ByteBuffer: Needs to match the largest record
    // Check record size, if a new record is added!
    EXPLORVIZ_BUFFER = ByteBuffer
        .allocateDirect(BeforeJDBCOperationEventRecord.COMPRESSED_BYTE_LENGTH_WITH_CLAZZ_ID);
  }

  public static void sendBeforeRecord(final long timestamp, final long traceId,
      final int orderIndex, final int objectId, final String operationSignature, final String clazz,
      final String interfaceImpl) {
    sendBeforeGeneric(BeforeOperationEventRecord.CLAZZ_ID,
        timestamp,
        traceId,
        orderIndex,
        objectId,
        operationSignature,
        clazz,
        interfaceImpl);
  }

  private static void sendBeforeGeneric(final byte ID, final long timestamp, final long traceId,
      final int orderIndex, final int objectId, final String operationSignature, final String clazz,
      final String implementedInterface) {
    EXPLORVIZ_BUFFER.put(ID);
    EXPLORVIZ_BUFFER.putLong(traceId);
    EXPLORVIZ_BUFFER.putInt(orderIndex);
    EXPLORVIZ_BUFFER.putInt(objectId);
    EXPLORVIZ_BUFFER.putInt(MonitoringStringRegistry.getIdForString(operationSignature));
    EXPLORVIZ_BUFFER.putInt(MonitoringStringRegistry.getIdForString(clazz));
    if (implementedInterface != null) {
      EXPLORVIZ_BUFFER.putInt(MonitoringStringRegistry.getIdForString(implementedInterface));
    }

    sendBufferIfHasElements(timestamp);
  }

  private static void sendBufferIfHasElements(final long timestamp) {
    if (EXPLORVIZ_BUFFER.position() > 0) {
      if (firstTimestamp == -1) {
        firstTimestamp = timestamp;
        // firstWallclockTimestamp = System.nanoTime();
      } else {
        // final long passedTime = timestamp - firstTimestamp;

        // while (REPLAY_IN_REALTIME && System.nanoTime() - firstWallclockTimestamp < passedTime) {
        // if (passedTime > 1000L * 1000L) {
        // try {
        // Thread.sleep(1000L);
        // } catch (final InterruptedException e) {
        // LOGGER.warn(LOGGER.getName() + ": " + e.getMessage());
        // }
        // }
        // }
      }

      MonitoringController.sendOutBuffer(EXPLORVIZ_BUFFER);
      EXPLORVIZ_BUFFER.clear();
    }
  }

  public static void sendAfterRecord(final long timestamp, final long methodDuration,
      final long traceId, final int orderIndex) {
    sendAfterGeneric(AfterOperationEventRecord.CLAZZ_ID,
        timestamp,
        methodDuration,
        traceId,
        orderIndex);
  }

  private static void sendAfterGeneric(final byte ID, final long timestamp,
      final long methodDuration, final long traceId, final int orderIndex) {
    EXPLORVIZ_BUFFER.put(ID);
    EXPLORVIZ_BUFFER.putLong(methodDuration);
    EXPLORVIZ_BUFFER.putLong(traceId);
    EXPLORVIZ_BUFFER.putInt(orderIndex);

    sendBufferIfHasElements(timestamp);
  }

  public static void sendAfterFailedRecord(final long timestamp, final long methodDuration,
      final long traceId, final int orderIndex, final String cause) {
    sendAfterFailedGeneric(AfterFailedOperationEventRecord.CLAZZ_ID,
        timestamp,
        methodDuration,
        traceId,
        orderIndex,
        cause);
  }

  private static void sendAfterFailedGeneric(final byte ID, final long timestamp,
      final long methodDuration, final long traceId, final int orderIndex, final String cause) {
    EXPLORVIZ_BUFFER.put(ID);
    EXPLORVIZ_BUFFER.putLong(methodDuration);
    EXPLORVIZ_BUFFER.putLong(traceId);
    EXPLORVIZ_BUFFER.putInt(orderIndex);
    EXPLORVIZ_BUFFER.putInt(MonitoringStringRegistry.getIdForString(cause));

    sendBufferIfHasElements(timestamp);
  }

  /**
   * Mapping towards Kieker (ApplicationTraceMetaDataRecord) and ExplorViz
   * (HostApplicationMetaDataRecord)
   *
   * @param timestamp (as configured in Kieker)
   * @param systemName (synthetic clustering of applications and nodes, is configured in ExplorViz)
   * @param ipAddress (of the monitored node)
   * @param hostname (of the monitored node)
   * @param applicationName (name of the application)
   * @param programmingLanguage (of the monitored application, e.g., Java)
   */
  public static void sendApplicationTraceMetaDataRecord(final long timestamp,
      final String systemName, final String ipAddress, final String hostname,
      final String applicationName, final String programmingLanguage) {
    EXPLORVIZ_BUFFER.put(HostApplicationMetaDataRecord.CLAZZ_ID);
    EXPLORVIZ_BUFFER.putInt(MonitoringStringRegistry.getIdForString(systemName));
    EXPLORVIZ_BUFFER.putInt(MonitoringStringRegistry.getIdForString(ipAddress));
    EXPLORVIZ_BUFFER.putInt(MonitoringStringRegistry.getIdForString(hostname));
    EXPLORVIZ_BUFFER.putInt(MonitoringStringRegistry.getIdForString(applicationName));
    EXPLORVIZ_BUFFER.putInt(MonitoringStringRegistry.getIdForString(programmingLanguage));

    sendBufferIfHasElements(timestamp);
  }

  /**
   * Mapping towards Kieker (CPUUtilizationRecord and MemSwapUsageRecord) and ExplorViz
   * (MemSwapUsageRecord)
   *
   * @param timestamp (as configured in Kieker)
   * @param hostname (of the monitored application)
   * @param cpuUtilization (between 0 and 1)
   * @param usedRAM (in byte)
   * @param absoluteRAM (in byte)
   */
  public static void sendSystemMonitoringRecord(final long timestamp, final String hostname,
      final double cpuUtilization, final long usedRAM, final long absoluteRAM) {

    // a value of "0" marks that the information is not available, either memory or
    // cpu usage
    EXPLORVIZ_BUFFER.put(SystemMonitoringRecord.CLAZZ_ID);
    EXPLORVIZ_BUFFER.putDouble(cpuUtilization);
    EXPLORVIZ_BUFFER.putLong(usedRAM);
    EXPLORVIZ_BUFFER.putLong(absoluteRAM);

    sendBufferIfHasElements(timestamp);

  }

  public static void sendBeforeConstructorRecord(final long timestamp, final long traceId,
      final int orderIndex, final int objectId, final String operationSignature, final String clazz,
      final String interfaceImpl) {
    sendBeforeGeneric(BeforeConstructorEventRecord.CLAZZ_ID,
        timestamp,
        traceId,
        orderIndex,
        objectId,
        operationSignature,
        clazz,
        interfaceImpl);
  }

  public static void sendAfterConstructorRecord(final long timestamp, final long methodDuration,
      final long traceId, final int orderIndex) {
    sendAfterGeneric(AfterConstructorEventRecord.CLAZZ_ID,
        timestamp,
        methodDuration,
        traceId,
        orderIndex);
  }

  public static void sendAfterFailedConstructorRecord(final long timestamp,
      final long methodDuration, final long traceId, final int orderIndex, final String cause) {
    sendAfterFailedGeneric(AfterFailedConstructorEventRecord.CLAZZ_ID,
        timestamp,
        methodDuration,
        traceId,
        orderIndex,
        cause);
  }

  public static void sendBeforeStaticRecord(final long timestamp, final long traceId,
      final int orderIndex, final String operationSignature, final String clazz,
      final String implementedInterface) {
    sendBeforeGeneric(BeforeStaticOperationEventRecord.CLAZZ_ID,
        timestamp,
        traceId,
        orderIndex,
        0,
        operationSignature,
        clazz,
        implementedInterface);
  }

  public static void sendAfterStaticRecord(final long timestamp, final long methodDuration,
      final long traceId, final int orderIndex) {
    sendAfterGeneric(AfterStaticOperationEventRecord.CLAZZ_ID,
        timestamp,
        methodDuration,
        traceId,
        orderIndex);
  }

  public static void sendAfterFailedStaticRecord(final long timestamp, final long methodDuration,
      final long traceId, final int orderIndex, final String cause) {
    sendAfterFailedGeneric(AfterFailedStaticOperationEventRecord.CLAZZ_ID,
        timestamp,
        methodDuration,
        traceId,
        orderIndex,
        cause);
  }

  // DatabaseEvents
  public static void sendBeforeDatabaseEvent(final long loggingTimestamp,
      final String classSignature, final long traceId, final int orderIndex, final int objectId,
      final String interfaceImpl, final String parameters, final String technology) {
    EXPLORVIZ_BUFFER.put(BeforeJDBCOperationEventRecord.CLAZZ_ID);
    EXPLORVIZ_BUFFER.putLong(traceId);
    EXPLORVIZ_BUFFER.putInt(orderIndex);
    EXPLORVIZ_BUFFER.putInt(objectId);
    EXPLORVIZ_BUFFER.putInt(MonitoringStringRegistry.getIdForString(classSignature));
    // TODO Extract class? // clazz
    EXPLORVIZ_BUFFER.putInt(MonitoringStringRegistry.getIdForString(classSignature));
    if (interfaceImpl != null) {
      EXPLORVIZ_BUFFER.putInt(MonitoringStringRegistry.getIdForString(interfaceImpl));
    }

    EXPLORVIZ_BUFFER.putInt(MonitoringStringRegistry.getIdForString(parameters));
    // TODO Add technology

    sendBufferIfHasElements(loggingTimestamp);
  }

  public static void sendAfterDatabaseEvent(final long loggingTimestamp, final long methodDuration,
      final String classSignature, final long traceId, final int orderIndex,
      final String returnType, final String returnValue) {
    EXPLORVIZ_BUFFER.put(AfterJDBCOperationEventRecord.CLAZZ_ID);
    EXPLORVIZ_BUFFER.putLong(methodDuration);
    EXPLORVIZ_BUFFER.putLong(traceId);
    EXPLORVIZ_BUFFER.putInt(orderIndex);
    // TODO Add returnType
    EXPLORVIZ_BUFFER.putInt(MonitoringStringRegistry.getIdForString(returnValue));

    sendBufferIfHasElements(loggingTimestamp);
  }

  public static void sendDatabaseFailedEvent(final long loggingTimestamp, final long methodDuration,
      final String classSignature, final long traceId, final int orderIndex, final String cause) {
    EXPLORVIZ_BUFFER.put(AfterFailedJDBCOperationEventRecord.CLAZZ_ID);
    EXPLORVIZ_BUFFER.putLong(methodDuration);
    EXPLORVIZ_BUFFER.putLong(traceId);
    EXPLORVIZ_BUFFER.putInt(orderIndex);
    EXPLORVIZ_BUFFER.putInt(MonitoringStringRegistry.getIdForString(cause));

    sendBufferIfHasElements(loggingTimestamp);
  }

}
