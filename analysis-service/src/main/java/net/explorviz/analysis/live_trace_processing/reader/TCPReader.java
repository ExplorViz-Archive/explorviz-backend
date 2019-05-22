package net.explorviz.analysis.live_trace_processing.reader;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import net.explorviz.common.live_trace_processing.filter.PipesMerger;
import net.explorviz.common.live_trace_processing.reader.IPeriodicTimeSignalReceiver;
import net.explorviz.common.live_trace_processing.reader.TimeSignalReader;
import net.explorviz.common.live_trace_processing.record.IRecord;
import net.explorviz.common.live_trace_processing.record.misc.TimedPeriodRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TCPReader implements IPeriodicTimeSignalReceiver {

	private static final Logger LOG = LoggerFactory.getLogger(TCPReader.class);

	private final int listeningPort;
	private boolean active = true;

	private ServerSocketChannel serversocket;

	private final PipesMerger<IRecord> merger;

	private final Queue<IRecord> periodicSignalQueue;

	private final ExecutorService threadPool;

	public TCPReader(final int listeningPort, final PipesMerger<IRecord> traceReconstructionMerger) {
		this.listeningPort = listeningPort;

		merger = traceReconstructionMerger;

		new TimeSignalReader(TimeUnit.SECONDS.toMillis(1), this).start();
		periodicSignalQueue = merger.registerProducer();

		threadPool = Executors.newCachedThreadPool();
	}

	@Override
	public void periodicTimeSignal(final long timestamp) {
		final TimedPeriodRecord periodRecord = new TimedPeriodRecord();
		while ((periodicSignalQueue != null) && !periodicSignalQueue.offer(periodRecord)) {
			try {
				Thread.sleep(1);
			} catch (final InterruptedException e) {
			}
		}
	}

	public final void read() {
		try {
			open();
			while (active) {
				threadPool.execute(new TCPReaderOneClient(serversocket.accept(), merger));
			}
		} catch (final IOException ex) {
			LOG.info("Error in read() " + ex.getMessage());
		} finally {
			try {
				serversocket.close();
			} catch (final IOException e) {
				LOG.info("Error in read()" + e.getMessage());
			}
		}
	}

	private final void open() throws IOException {
		serversocket = ServerSocketChannel.open();
		serversocket.socket().bind(new InetSocketAddress(listeningPort));
		LOG.info("Listening on port " + listeningPort);
	}

	public final void terminate(final boolean error) {
		LOG.info("Shutdown of TCPReader requested.");
		active = false;
		try {
			threadPool.awaitTermination(10, TimeUnit.SECONDS);
		} catch (final InterruptedException e) {
		}
		threadPool.shutdown();
	}
}
