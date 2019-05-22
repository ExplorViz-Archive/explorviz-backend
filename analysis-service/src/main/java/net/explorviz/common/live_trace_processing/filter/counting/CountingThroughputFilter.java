package net.explorviz.common.live_trace_processing.filter.counting;

import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicInteger;
import net.explorviz.common.live_trace_processing.reader.IPeriodicTimeSignalReceiver;
import net.explorviz.common.live_trace_processing.reader.TimeSignalReader;
import net.explorviz.common.live_trace_processing.record.IRecord;
import net.explorviz.common.live_trace_processing.record.misc.SystemMonitoringRecord;
import net.explorviz.common.live_trace_processing.record.misc.TimedPeriodRecord;

public final class CountingThroughputFilter implements IPeriodicTimeSignalReceiver {

  private final AtomicInteger currentCountForCurrentInterval = new AtomicInteger(0);

  private final String beforeOutMessage;

  private boolean enabled;

  public CountingThroughputFilter(final String beforeOutMessage, final boolean enabled,
      final int outputIntervalInMillisec) {
    this.beforeOutMessage = beforeOutMessage;
    this.enabled = enabled;
    new TimeSignalReader(outputIntervalInMillisec, this).start();
  }

  public final void inputRecord(final IRecord record) {
    if (this.enabled && !(record instanceof SystemMonitoringRecord)
        && !(record instanceof TimedPeriodRecord)) {
      this.currentCountForCurrentInterval.incrementAndGet();
    }
  }

  public final void inputObjectsCount(final int increment) {
    this.currentCountForCurrentInterval.addAndGet(increment);
  }

  @Override
  public void periodicTimeSignal(final long timestamp) {
    final int count = this.currentCountForCurrentInterval.getAndSet(0);

    if (count == 0) {
      return;
    }

    final double countK = count / 1000d;
    final double countM = countK / 1000d;

    final DecimalFormat dec = new DecimalFormat(",##0.00");

    String rest = String.valueOf(count);
    if (countM >= 1) {
      rest = dec.format(countM) + " millions";
    } else if (countK >= 1) {
      rest = dec.format(countK) + " thousands";
    }
    System.out.println(timestamp + " " + this.beforeOutMessage + ": " + rest);
  }

  public boolean isEnabled() {
    return this.enabled;
  }

  public void setEnabled(final boolean enabled) {
    this.enabled = enabled;
  }
}
