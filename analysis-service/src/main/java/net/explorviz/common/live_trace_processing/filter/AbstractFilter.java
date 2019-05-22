package net.explorviz.common.live_trace_processing.filter;

import java.util.Queue;
import net.explorviz.common.live_trace_processing.filter.counting.CountingThroughputFilter;
import net.explorviz.common.live_trace_processing.record.IRecord;

public abstract class AbstractFilter extends Thread implements IPipeReceiver<IRecord> {
  protected final CountingThroughputFilter counter;

  private final Queue<IRecord> receiverQueue;

  public AbstractFilter(final Queue<IRecord> receiverQueue, final String counterString,
      final int outputIntervalInMillisec) {
    this.receiverQueue = receiverQueue;

    this.counter = new CountingThroughputFilter(counterString, true, outputIntervalInMillisec);
  }

  @Override
  public abstract void processRecord(final IRecord record);

  protected final void deliver(final IRecord record) {
    this.counter.inputRecord(record);

    while (!this.receiverQueue.offer(record)) {
      try {
        Thread.sleep(1);
      } catch (final InterruptedException e) {
      }
    }
  }
}
