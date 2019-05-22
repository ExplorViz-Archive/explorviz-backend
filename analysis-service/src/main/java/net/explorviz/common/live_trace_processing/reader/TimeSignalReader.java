package net.explorviz.common.live_trace_processing.reader;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class TimeSignalReader {
	private final long period;

	private final ScheduledExecutorService executorService = Executors
			.newSingleThreadScheduledExecutor();

	private final IPeriodicTimeSignalReceiver receiver;

	public TimeSignalReader(final long periodInMilliSec,
			final IPeriodicTimeSignalReceiver receiver) {
		period = periodInMilliSec;
		this.receiver = receiver;
	}

	public void start() {
		executorService.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				try {
					receiver.periodicTimeSignal(TimeProvider
							.getCurrentTimestamp());
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		}, 0, period, TimeUnit.MILLISECONDS);
	}
}
