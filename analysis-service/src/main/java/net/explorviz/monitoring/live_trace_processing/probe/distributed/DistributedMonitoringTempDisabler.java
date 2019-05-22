package net.explorviz.monitoring.live_trace_processing.probe.distributed;


public class DistributedMonitoringTempDisabler {
  private boolean monitoringEnabled = true;

  static final private ThreadLocal<DistributedMonitoringTempDisabler> probeController =
      new ThreadLocal<DistributedMonitoringTempDisabler>() {
        @Override
        public DistributedMonitoringTempDisabler initialValue() {
          return new DistributedMonitoringTempDisabler();
        }
      };

  public static DistributedMonitoringTempDisabler getProbeController() {
    return DistributedMonitoringTempDisabler.probeController.get();
  }

  public boolean isMonitoringEnabled() {
    return this.monitoringEnabled;
  }

  public void enableMonitoring() {
    this.monitoringEnabled = true;
  }

  public void disableMonitoring() {
    this.monitoringEnabled = false;
  }
}
