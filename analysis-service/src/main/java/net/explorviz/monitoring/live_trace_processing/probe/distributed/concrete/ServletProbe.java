package net.explorviz.monitoring.live_trace_processing.probe.distributed.concrete;

// import javax.servlet.http.HttpServletRequest;
// import javax.servlet.http.HttpServletResponse;

import org.aspectj.lang.ProceedingJoinPoint;

// @Aspect
public class ServletProbe {

  public static final String TRACE_ID_HEADER = "ExplorVizHttpHeaderUniqueTraceId";
  public static final String ORDER_ID_HEADER = "ExplorVizHttpHeaderUniqueOrderId";

  // @Around("execution(void javax.servlet.Servlet+.service(javax.servlet.ServletRequest,
  // javax.servlet.ServletResponse))")
  public Object messageReceived(final ProceedingJoinPoint thisJoinPoint) throws Throwable {
    return thisJoinPoint.proceed();

    /*
     * final DistributedMonitoringTempDisabler probeController = DistributedMonitoringTempDisabler
     * .getProbeController(); if (!probeController.isMonitoringEnabled() ||
     * !MonitoringController.isMonitoringEnabled()) { return thisJoinPoint.proceed(); }
     * 
     * probeController.disableMonitoring();
     * 
     * final ProbeTraceMetaData trace = DistributedMonitoringTempDisabler.getTrace();
     * 
     * final Long ownTraceId = trace.getTraceId();
     * 
     * final Object args[] = thisJoinPoint.getArgs(); if ((args == null) || !(args[0] instanceof
     * HttpServletRequest) || !(args[1] instanceof HttpServletResponse)) {
     * probeController.enableMonitoring(); return thisJoinPoint.proceed(); }
     * 
     * final HttpServletRequest req = (HttpServletRequest) args[0]; final HttpServletResponse res =
     * (HttpServletResponse) args[1];
     * 
     * boolean isUnknowSender = true;
     * 
     * try { final long traceId = Long.parseLong(req.getHeader(TRACE_ID_HEADER)); final int orderId
     * = Integer.parseInt(req.getHeader(ORDER_ID_HEADER));
     * 
     * final Integer ownOrderId = trace.getNextOrderId();
     * DistributedMonitoringRecordSender.sendOutReceivedRecord(ownTraceId, ownOrderId, traceId,
     * orderId);
     * 
     * isUnknowSender = false; } catch (final Exception e) { } finally { if (isUnknowSender) { final
     * Integer ownOrderId = trace.getNextOrderId(); DistributedMonitoringRecordSender
     * .sendOutUnknownReceivedRecord(ownTraceId, ownOrderId, req.getRemoteHost(),
     * req.getRequestURL().toString()); } }
     * 
     * try { res.setHeader(TRACE_ID_HEADER, ownTraceId.toString()); final Integer ownOrderId =
     * trace.getNextOrderId(); res.setHeader(ORDER_ID_HEADER, ownOrderId.toString());
     * 
     * DistributedMonitoringRecordSender.sendOutSentRecord(ownTraceId, ownOrderId,
     * req.getRemoteHost() + ":" + req.getRemotePort()); } catch (final Exception e) { } finally {
     * probeController.enableMonitoring(); }
     * 
     * return thisJoinPoint.proceed(args);
     */
  }
}
