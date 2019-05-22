package net.explorviz.common.live_trace_processing.filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import org.jctools.queues.QueueFactory;
import org.jctools.queues.spec.ConcurrentQueueSpec;

public class PipesMerger<T> {
  private final int queueCapacity;
  private volatile boolean terminated = false;

  private volatile boolean isAfterActionsRequired = false;

  private final List<Queue<T>> queues = new ArrayList<>();

  private final List<Queue<T>> queuesToAdd =
      Collections.synchronizedList(new ArrayList<Queue<T>>(4));
  private final List<Queue<T>> queuesToRemove =
      Collections.synchronizedList(new ArrayList<Queue<T>>(4));

  private IPipeReceiver<T> receiver;

  public PipesMerger(final int capacity) {
    this.queueCapacity = capacity;
  }

  public Queue<T> registerProducer() {
    final Queue<T> queue =
        QueueFactory.newQueue(ConcurrentQueueSpec.createBoundedSpsc(this.queueCapacity));

    this.queuesToAdd.add(queue);
    this.isAfterActionsRequired = true;

    return queue;
  }

  /**
   * Deregister the created queue. CAUTION: Do not write to the queue after deregistering it!
   *
   * @param queue
   */
  public void deregisterProducer(final Queue<T> queue) {
    this.queuesToRemove.add(queue);
    this.isAfterActionsRequired = true;
  }

  /**
   * Method that never returns before its termination. Call in a separate thread.
   * 
   * @param receiver
   */
  public void process(final IPipeReceiver<T> receiver) {
    this.receiver = receiver;
    while (!this.terminated) {
      this.processLoop();
    }
  }

  private void processLoop() {
    boolean foundOneEntry = false;
    for (final Queue<T> queue : this.queues) {
      final T maybeArrayEvent = queue.poll();
      if (maybeArrayEvent != null) {
        try {
          this.receiver.processRecord(maybeArrayEvent);
        } catch (final Exception e) {
          e.printStackTrace();
        }
        foundOneEntry = true;
      }
    }

    if (this.isAfterActionsRequired) {
      this.doActionsAfterRound();
    }

    if (!foundOneEntry) {
      try {
        Thread.sleep(1);
      } catch (final InterruptedException e) {
      }
    }
  }

  private void doActionsAfterRound() {
    this.isAfterActionsRequired = false;

    synchronized (this.queuesToAdd) {
      if (!this.queuesToAdd.isEmpty()) {
        this.queues.addAll(this.queuesToAdd);
        this.queuesToAdd.clear();
      }
    }

    synchronized (this.queuesToRemove) {
      final Iterator<Queue<T>> queuesToRemoveIterator = this.queuesToRemove.iterator();

      while (queuesToRemoveIterator.hasNext()) {
        final Queue<T> queue = queuesToRemoveIterator.next();
        // do not delete queues that contain elements
        if (queue.isEmpty()) {
          final boolean removed = this.queues.remove(queue);
          queuesToRemoveIterator.remove();
          if (removed) {
            System.out.println("Warning: Remove a queue failed in merger");
          }
        } else {
          this.isAfterActionsRequired = true;
        }
      }
    }
  }

  public void terminate() {
    this.terminated = true;
  }
}
