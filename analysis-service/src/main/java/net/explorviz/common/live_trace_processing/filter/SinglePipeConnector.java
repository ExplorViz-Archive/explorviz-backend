package net.explorviz.common.live_trace_processing.filter;

import java.util.Queue;
import org.jctools.queues.QueueFactory;
import org.jctools.queues.spec.ConcurrentQueueSpec;

public class SinglePipeConnector<T> {
	private boolean terminated = false;

	private final Queue<T> queue;

	private IPipeReceiver<T> receiver;

	public SinglePipeConnector(final int capacity) {
		queue = QueueFactory.newQueue(ConcurrentQueueSpec
				.createBoundedSpsc(capacity));
	}

	public Queue<T> registerProducer() {
		return queue;
	}

	/**
	 * Deregister the created queue. CAUTION: Do not write to the queue after
	 * deregistering it!
	 *
	 * @param queue
	 */
	public void deregisterProducer(final Queue<T> queue) {
		terminate();
	}

	public void process(final IPipeReceiver<T> receiver) {
		this.receiver = receiver;
		while (!terminated) {
			processLoop();
		}
	}

	private void processLoop() {
		final T maybeArrayEvent = queue.poll();
		if (maybeArrayEvent != null) {
			try {
				receiver.processRecord(maybeArrayEvent);
			} catch (final Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				Thread.sleep(1);
			} catch (final InterruptedException e) {
			}
		}
	}

	public void terminate() {
		terminated = true;
	}
}
