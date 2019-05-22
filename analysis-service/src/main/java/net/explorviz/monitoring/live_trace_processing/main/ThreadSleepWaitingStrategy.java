package net.explorviz.monitoring.live_trace_processing.main;

import com.lmax.disruptor.AlertException;
import com.lmax.disruptor.Sequence;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.WaitStrategy;

public final class ThreadSleepWaitingStrategy implements WaitStrategy {
  @Override
  public long waitFor(final long sequence, final Sequence cursor, final Sequence dependentSequence,
      final SequenceBarrier barrier) throws AlertException, InterruptedException {
    long availableSequence;

    while ((availableSequence = dependentSequence.get()) < sequence) {
      this.applyWaitMethod(barrier);
    }

    return availableSequence;
  }

  @Override
  public void signalAllWhenBlocking() {}

  private void applyWaitMethod(final SequenceBarrier barrier) throws AlertException {
    barrier.checkAlert();

    try {
      Thread.sleep(1);
    } catch (final InterruptedException e) {
    }
  }
}
