package net.explorviz.monitoring.live_trace_processing.probe.distributed.concrete;

import java.io.DataInput;
import net.explorviz.monitoring.live_trace_processing.probe.distributed.DistributedMonitoringRecordWriter;
import net.explorviz.monitoring.live_trace_processing.probe.distributed.DistributedMonitoringTempDisabler;
import net.explorviz.monitoring.live_trace_processing.probe.tracemanagement.ProbeTraceMetaData;
import net.explorviz.monitoring.live_trace_processing.probe.tracemanagement.TraceRegistry;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.hsqldb.rowio.RowOutputBinary;

// @Aspect
public class HSQLDBProbe {
  private static final int ID_BYTES = 4 + 8;

  // @Around("call(* java.io.DataInput.readFully(byte[], int, int)) && cflow(execution(*
  // org.hsqldb.result.Result.newResult(org.hsqldb.Session, java.io.DataInput,
  // org.hsqldb.rowio.RowInputBinary, int)))")
  public Object receiveResult(final ProceedingJoinPoint thisJoinPoint) throws Throwable {
    final DistributedMonitoringTempDisabler probeController =
        DistributedMonitoringTempDisabler.getProbeController();
    final Object[] args = thisJoinPoint.getArgs();

    if (probeController.isMonitoringEnabled()) {
      probeController.disableMonitoring();

      final ProbeTraceMetaData trace = TraceRegistry.getTrace();
      trace.incrementStackDepth();
      final long ownTraceId = trace.getTraceId();
      final int ownOrderId = trace.getNextOrderId();

      final DataInput in = (DataInput) thisJoinPoint.getTarget();
      final long traceId = in.readLong();
      final int orderId = in.readInt();
      final int length = (Integer) args[2] - ID_BYTES;
      args[2] = Integer.valueOf(length);

      DistributedMonitoringRecordWriter
          .writeBeforeReceivedRecord(ownTraceId, ownOrderId, traceId, orderId);

      probeController.enableMonitoring();
    }

    return thisJoinPoint.proceed(args);
  }

  // @After("execution(* org.hsqldb.rowio.RowOutputBinary.writeSize(int))")
  public void sendResult(final JoinPoint thisJoinPoint) {
    final DistributedMonitoringTempDisabler probeController =
        DistributedMonitoringTempDisabler.getProbeController();
    if (probeController.isMonitoringEnabled()) {
      probeController.disableMonitoring();

      final ProbeTraceMetaData trace = TraceRegistry.getTrace();
      trace.incrementStackDepth();
      final long traceId = trace.getTraceId();
      final int orderId = trace.getNextOrderId();

      final RowOutputBinary buffer = (RowOutputBinary) thisJoinPoint.getTarget();
      buffer.writeLong(traceId);
      buffer.writeInt(orderId);

      DistributedMonitoringRecordWriter.writeBeforeSentRecord(traceId,
          orderId,
          DistributedMonitoringRecordWriter.UNKNOWN_DESTINATION);

      probeController.enableMonitoring();
    }
  }
}
