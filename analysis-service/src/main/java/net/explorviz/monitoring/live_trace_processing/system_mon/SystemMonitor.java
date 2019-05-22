package net.explorviz.monitoring.live_trace_processing.system_mon;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.explorviz.common.live_trace_processing.record.misc.SystemMonitoringRecord;
import net.explorviz.monitoring.live_trace_processing.main.MonitoringController;

public class SystemMonitor {
  private final long period;

  private final ScheduledExecutorService executorService;

  public SystemMonitor(final long periodInMilliSec) {
    this.period = periodInMilliSec;
    this.executorService = Executors.newSingleThreadScheduledExecutor();
  }

  public static void main(final String[] args) {
    MonitoringController.isMonitoringEnabled(); // dummy access for static
    // init
  }

  public void start() {
    this.executorService.scheduleAtFixedRate(new Runnable() {
      @Override
      public void run() {
        try {
          final double cpuUtil = SystemMonitorProbe.getSystemCpuLoad();
          if (cpuUtil >= 0) { // fixes -1 bug in first seconds
            final SystemMonitoringRecord record =
                new SystemMonitoringRecord(cpuUtil, SystemMonitorProbe.getUsedPhysicalMemorySize(),
                    SystemMonitorProbe.getTotalPhysicalMemorySize(), null);
            MonitoringController.sendOutSystemRecord(record);
          }
        } catch (final Exception e) {
          e.printStackTrace();
        }
      }
    }, 0, this.period, TimeUnit.MILLISECONDS);
  }

  public void shutdown() {
    this.executorService.shutdown();

    try {
      this.executorService.awaitTermination(30, TimeUnit.SECONDS);
    } catch (final InterruptedException e) {
    }
  }
}
