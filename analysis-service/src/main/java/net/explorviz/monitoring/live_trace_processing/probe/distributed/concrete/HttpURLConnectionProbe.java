package net.explorviz.monitoring.live_trace_processing.probe.distributed.concrete;

import java.net.HttpURLConnection;
import net.explorviz.monitoring.live_trace_processing.main.MonitoringController;
import net.explorviz.monitoring.live_trace_processing.probe.distributed.DistributedMonitoringRecordWriter;
import net.explorviz.monitoring.live_trace_processing.probe.distributed.DistributedMonitoringTempDisabler;
import net.explorviz.monitoring.live_trace_processing.probe.tracemanagement.ProbeTraceMetaData;
import net.explorviz.monitoring.live_trace_processing.probe.tracemanagement.TraceRegistry;
import org.aspectj.lang.ProceedingJoinPoint;

// @Aspect
public class HttpURLConnectionProbe {
  // @Around("call(void java.net.URLConnection+.connect()) || call(*
  // java.net.HttpURLConnection+.getResponseCode())")
  public Object messageAction(final ProceedingJoinPoint thisJoinPoint) throws Throwable {
    final DistributedMonitoringTempDisabler probeController =
        DistributedMonitoringTempDisabler.getProbeController();
    if (!probeController.isMonitoringEnabled() || !MonitoringController.isMonitoringEnabled()) {
      return thisJoinPoint.proceed();
    }

    probeController.disableMonitoring();

    if (!(thisJoinPoint.getTarget() instanceof HttpURLConnection)) {
      probeController.enableMonitoring();
      return thisJoinPoint.proceed();
    }
    final HttpURLConnection target = (HttpURLConnection) thisJoinPoint.getTarget();

    final ProbeTraceMetaData trace = TraceRegistry.getTrace();
    trace.incrementStackDepth();

    final Long ownTraceId = trace.getTraceId();
    boolean sentRecordWasSent = true;

    try {
      target.addRequestProperty(ServletProbe.TRACE_ID_HEADER, ownTraceId.toString());
      final Integer ownOrderId = trace.getNextOrderId();
      target.addRequestProperty(ServletProbe.ORDER_ID_HEADER, ownOrderId.toString());

      DistributedMonitoringRecordWriter
          .writeBeforeSentRecord(ownTraceId, ownOrderId, target.getURL().toString());
    } catch (final Exception e) {
      sentRecordWasSent = false;
    }

    probeController.enableMonitoring();
    final Object result = thisJoinPoint.proceed();
    probeController.disableMonitoring();

    if (sentRecordWasSent) {
      final Integer ownOrderId = trace.getNextOrderId();
      boolean unknownReceivedRecord = true;
      try {
        final long traceId = Long.parseLong(target.getHeaderField(ServletProbe.TRACE_ID_HEADER));
        final int orderId = Integer.parseInt(target.getHeaderField(ServletProbe.ORDER_ID_HEADER));

        DistributedMonitoringRecordWriter
            .writeBeforeReceivedRecord(ownTraceId, ownOrderId, traceId, orderId);
        unknownReceivedRecord = false;
      } catch (final Exception e) {
      } finally {
        if (unknownReceivedRecord) {
          DistributedMonitoringRecordWriter.writeBeforeUnknownReceivedRecord(ownTraceId,
              ownOrderId,
              "",
              target.getURL().toString());
        }
      }
    }

    probeController.enableMonitoring();
    return result;
  }
}
