package net.explorviz.monitoring.live_trace_processing.system_mon;

import com.sun.management.OperatingSystemMXBean;
import java.lang.management.ManagementFactory;


public class SystemMonitorProbe {

  private static final OperatingSystemMXBean osBean =
      ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);

  public static double getSystemCpuLoad() {
    return osBean.getSystemCpuLoad();
  }

  public static long getTotalPhysicalMemorySize() {
    return osBean.getTotalPhysicalMemorySize();
  }

  public static long getFreePhysicalMemorySize() {
    return osBean.getFreePhysicalMemorySize();
  }

  public static long getUsedPhysicalMemorySize() {
    return osBean.getTotalPhysicalMemorySize() - osBean.getFreePhysicalMemorySize();
  }
}
