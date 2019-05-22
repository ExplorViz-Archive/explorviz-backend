package net.explorviz.monitoring.live_trace_processing.probe.distributed;


public class DistributedMonitoringTempDisabler {
	private boolean monitoringEnabled = true;

	static final private ThreadLocal<DistributedMonitoringTempDisabler> probeController = new ThreadLocal<DistributedMonitoringTempDisabler>() {
		@Override
		public DistributedMonitoringTempDisabler initialValue() {
			return new DistributedMonitoringTempDisabler();
		}
	};

	public static DistributedMonitoringTempDisabler getProbeController() {
		return DistributedMonitoringTempDisabler.probeController.get();
	}

	public boolean isMonitoringEnabled() {
		return monitoringEnabled;
	}

	public void enableMonitoring() {
		monitoringEnabled = true;
	}

	public void disableMonitoring() {
		monitoringEnabled = false;
	}
}
