package net.explorviz.monitoring.live_trace_processing.probe;

import java.nio.ByteBuffer;
import net.explorviz.common.live_trace_processing.Constants;
import net.explorviz.common.live_trace_processing.reader.TimeProvider;
import net.explorviz.common.live_trace_processing.record.event.AbstractAfterEventRecord;
import net.explorviz.common.live_trace_processing.record.event.AbstractAfterFailedEventRecord;
import net.explorviz.common.live_trace_processing.record.event.AbstractBeforeOperationEventRecord;
import net.explorviz.common.live_trace_processing.record.event.constructor.AfterConstructorEventRecord;
import net.explorviz.common.live_trace_processing.record.event.constructor.AfterFailedConstructorEventRecord;
import net.explorviz.common.live_trace_processing.record.event.constructor.BeforeConstructorEventRecord;
import net.explorviz.common.live_trace_processing.record.event.normal.AfterFailedOperationEventRecord;
import net.explorviz.common.live_trace_processing.record.event.normal.AfterOperationEventRecord;
import net.explorviz.common.live_trace_processing.record.event.normal.BeforeOperationEventRecord;
import net.explorviz.common.live_trace_processing.record.event.statics.AfterFailedStaticOperationEventRecord;
import net.explorviz.common.live_trace_processing.record.event.statics.AfterStaticOperationEventRecord;
import net.explorviz.common.live_trace_processing.record.event.statics.BeforeStaticOperationEventRecord;
import net.explorviz.monitoring.live_trace_processing.adaptive_monitoring.AdaptiveMonitoring;
import net.explorviz.monitoring.live_trace_processing.main.MonitoringController;
import net.explorviz.monitoring.live_trace_processing.main.MonitoringStringRegistry;
import net.explorviz.monitoring.live_trace_processing.probe.tracemanagement.ProbeTraceMetaData;
import net.explorviz.monitoring.live_trace_processing.probe.tracemanagement.TraceRegistry;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public abstract class AbstractAspect {
  public static final ThreadLocalByteBuffer bufferStore =
      new ThreadLocalByteBuffer(Constants.SENDING_BUFFER_SIZE);
  private static final ThreadLocalLastSendingTime lastSendingTime =
      new ThreadLocalLastSendingTime();
  private static final AdaptiveMonitoring adaptiveMonitoring = new AdaptiveMonitoring();

  @Pointcut("!within(explorviz..*) && !within(com.lmax..*) && !within(org.hyperic.sigar..*)")
  public void notWithinExplorViz() {}

  @Pointcut
  public abstract void monitoredOperation();

  @Pointcut
  public abstract void monitoredConstructor();

  @Around("monitoredOperation() && this(thisObject) && notWithinExplorViz()")
  public final Object operation(final Object thisObject, final ProceedingJoinPoint thisJoinPoint)
      throws Throwable {
    if (!MonitoringController.isMonitoringEnabled()
        || !adaptiveMonitoring.isMethodEnabled(thisJoinPoint.getSignature())) {
      return thisJoinPoint.proceed();
    }

    return this.createEventRecords(thisJoinPoint,
        AbstractBeforeOperationEventRecord.COMPRESSED_BYTE_LENGTH_WITH_CLAZZ_ID,
        BeforeOperationEventRecord.CLAZZ_ID,
        AbstractAfterFailedEventRecord.COMPRESSED_BYTE_LENGTH_WITH_CLAZZ_ID,
        AfterFailedOperationEventRecord.CLAZZ_ID,
        AbstractAfterEventRecord.COMPRESSED_BYTE_LENGTH_WITH_CLAZZ_ID,
        AfterOperationEventRecord.CLAZZ_ID,
        System.identityHashCode(thisObject),
        thisObject.getClass().getName(),
        this.getInterface(thisJoinPoint));
  }

  @Around("monitoredOperation() && !this(java.lang.Object) && notWithinExplorViz()")
  public final Object staticOperation(final ProceedingJoinPoint thisJoinPoint) throws Throwable {
    if (!MonitoringController.isMonitoringEnabled()
        || !adaptiveMonitoring.isMethodEnabled(thisJoinPoint.getSignature())) {
      return thisJoinPoint.proceed();
    }

    return this.createEventRecords(thisJoinPoint,
        BeforeStaticOperationEventRecord.COMPRESSED_BYTE_LENGTH_WITH_CLAZZ_ID,
        BeforeStaticOperationEventRecord.CLAZZ_ID,
        AbstractAfterFailedEventRecord.COMPRESSED_BYTE_LENGTH_WITH_CLAZZ_ID,
        AfterFailedStaticOperationEventRecord.CLAZZ_ID,
        AbstractAfterEventRecord.COMPRESSED_BYTE_LENGTH_WITH_CLAZZ_ID,
        AfterStaticOperationEventRecord.CLAZZ_ID,
        0,
        thisJoinPoint.getSignature().getDeclaringTypeName(),
        this.getInterface(thisJoinPoint));
  }

  @Around("monitoredConstructor() && this(thisObject) && notWithinExplorViz()")
  public final Object construction(final Object thisObject, final ProceedingJoinPoint thisJoinPoint)
      throws Throwable {
    if (!MonitoringController.isMonitoringEnabled()
        || !adaptiveMonitoring.isMethodEnabled(thisJoinPoint.getSignature())) {
      return thisJoinPoint.proceed();
    }

    return this.createEventRecords(thisJoinPoint,
        AbstractBeforeOperationEventRecord.COMPRESSED_BYTE_LENGTH_WITH_CLAZZ_ID,
        BeforeConstructorEventRecord.CLAZZ_ID,
        AbstractAfterFailedEventRecord.COMPRESSED_BYTE_LENGTH_WITH_CLAZZ_ID,
        AfterFailedConstructorEventRecord.CLAZZ_ID,
        AbstractAfterEventRecord.COMPRESSED_BYTE_LENGTH_WITH_CLAZZ_ID,
        AfterConstructorEventRecord.CLAZZ_ID,
        System.identityHashCode(thisObject),
        thisObject.getClass().getName(),
        this.getInterface(thisJoinPoint));
  }

  @SuppressWarnings("rawtypes")
  private String getInterface(final ProceedingJoinPoint thisJoinPoint) {
    final Class[] interfaces = thisJoinPoint.getSignature().getDeclaringType().getInterfaces();
    if (interfaces.length == 1) {
      return interfaces[0].getName();
    }
    if (interfaces.length == 0) {
      final Class<?> superClass = thisJoinPoint.getSignature().getDeclaringType().getSuperclass();
      if (superClass != null) {
        final String superClassName = superClass.getName();
        if (!superClassName.equals("java.lang.Object")) {
          return superClassName;
        }
      }
    }

    return "";
  }

  protected Object createEventRecords(final ProceedingJoinPoint thisJoinPoint,
      final int beforeLength, final byte beforeId, final int afterFailedLength,
      final byte afterFailedId, final int afterLength, final byte afterId, final int objectId,
      final String clazz, final String implementedInterface) throws Throwable {
    final ByteBuffer buffer = bufferStore.get();

    final ProbeTraceMetaData trace = TraceRegistry.getTrace();
    trace.incrementStackDepth();

    if (beforeLength > buffer.remaining()) {
      // updateLastSendingTimeToCurrent();
      MonitoringController.sendOutBuffer(buffer);
    }

    buffer.put(beforeId);
    final long timeStart = TimeProvider.getCurrentTimestamp();
    buffer.putLong(trace.getTraceId());
    buffer.putInt(trace.getNextOrderId());
    if (objectId != 0) {
      buffer.putInt(objectId);
    }
    buffer.putInt(MonitoringController.getIdForSignature(thisJoinPoint.getSignature()));
    buffer.putInt(MonitoringStringRegistry.getIdForString(clazz));
    buffer.putInt(MonitoringStringRegistry.getIdForString(implementedInterface));

    final Object retval;

    try {
      retval = thisJoinPoint.proceed();
    } catch (final Throwable th) {
      if (afterFailedLength > buffer.remaining()) {
        // updateLastSendingTimeToCurrent();
        MonitoringController.sendOutBuffer(buffer);
      }

      buffer.put(afterFailedId);
      buffer.putLong(TimeProvider.getCurrentTimestamp() - timeStart);
      buffer.putLong(trace.getTraceId());
      buffer.putInt(trace.getNextOrderId());

      // final StringWriter errors = new StringWriter();
      // th.printStackTrace(new PrintWriter(errors));

      String message = th.getMessage();
      if (message == null) {
        message = "<unknown>";
      }

      buffer.putInt(MonitoringStringRegistry.getIdForString(message));

      trace.decreaseStackDepthAndEndTraceIfNeccessary();

      throw th;
    }

    if (afterLength > buffer.remaining()) {
      // updateLastSendingTimeToCurrent();
      MonitoringController.sendOutBuffer(buffer);
    }

    buffer.put(afterId);
    buffer.putLong(TimeProvider.getCurrentTimestamp() - timeStart);
    buffer.putLong(trace.getTraceId());
    buffer.putInt(trace.getNextOrderId());

    trace.decreaseStackDepthAndEndTraceIfNeccessary();

    return retval;
  }

  public static void updateLastSendingTimeToCurrent() {
    lastSendingTime.set(TimeProvider.getCurrentTimestamp());
  }
}
