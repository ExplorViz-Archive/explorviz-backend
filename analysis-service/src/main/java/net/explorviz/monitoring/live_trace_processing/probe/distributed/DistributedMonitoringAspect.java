package net.explorviz.monitoring.live_trace_processing.probe.distributed;

import net.explorviz.monitoring.live_trace_processing.probe.AbstractAspect;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public abstract class DistributedMonitoringAspect extends AbstractAspect {

	/**
	 * This method checks the ProbeContoller.monitoringEnabled variable to
	 * prevent monitoring of calls that are made from the monitoring probes.
	 */
	@Override
	protected Object createEventRecords(final ProceedingJoinPoint thisJoinPoint,
			final int beforeLength, final byte beforeId, final int afterFailedLength,
			final byte afterFailedId, final int afterLength, final byte afterId,
			final int objectId, final String clazz, final String implementedInterface)
			throws Throwable {
		if (DistributedMonitoringTempDisabler.getProbeController().isMonitoringEnabled()) {
			return super.createEventRecords(thisJoinPoint, beforeLength, beforeId,
					afterFailedLength, afterFailedId, afterLength, afterId, objectId, clazz,
					implementedInterface);
		} else {
			return thisJoinPoint.proceed();
		}
	}
}
