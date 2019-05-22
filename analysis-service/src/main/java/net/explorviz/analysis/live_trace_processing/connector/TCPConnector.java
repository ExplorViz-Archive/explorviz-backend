package net.explorviz.analysis.live_trace_processing.connector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.List;
import net.explorviz.common.live_trace_processing.Constants;
import net.explorviz.common.live_trace_processing.StringRegistrySender;
import net.explorviz.common.live_trace_processing.configuration.Configuration;
import net.explorviz.common.live_trace_processing.filter.AbstractSink;
import net.explorviz.common.live_trace_processing.filter.SinglePipeConnector;
import net.explorviz.common.live_trace_processing.record.IRecord;
import net.explorviz.common.live_trace_processing.record.ISerializableRecord;
import net.explorviz.common.live_trace_processing.record.event.AbstractEventRecord;
import net.explorviz.common.live_trace_processing.record.misc.StringRegistryRecord;
import net.explorviz.common.live_trace_processing.record.misc.TerminateRecord;
import net.explorviz.common.live_trace_processing.record.misc.TimedPeriodRecord;
import net.explorviz.common.live_trace_processing.record.trace.Trace;
import net.explorviz.common.live_trace_processing.writer.IRecordSender;
import net.explorviz.common.live_trace_processing.writer.IStringRecordSender;
import net.explorviz.common.live_trace_processing.writer.IWriter;

public class TCPConnector extends AbstractSink implements IWriter, IStringRecordSender,
IRecordSender {
	private URL providerURL;

	private SocketChannel socketChannel;

	private final StringRegistrySender stringRegistry = new StringRegistrySender(this);

	private final ByteBuffer sendingBuffer = ByteBuffer.allocate(Constants.SENDING_BUFFER_SIZE);
	private final ByteBuffer stringBuffer = ByteBuffer.allocate(Constants.SENDING_BUFFER_SIZE);

	private volatile boolean shouldDisconnect = false;
	private volatile boolean finishedSendingStrings = false;

	private final SinglePipeConnector<IRecord> tcpConnectorConnector;

	public TCPConnector(final SinglePipeConnector<IRecord> tcpConnectorConnector,
			final String hostname, final int port, final Configuration configuration) {
		this.tcpConnectorConnector = tcpConnectorConnector;
		sendingBuffer.clear();
		try {
			setProviderURL(new URL("http://" + hostname + ":" + port));
		} catch (final MalformedURLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		tcpConnectorConnector.process(this);
	}

	@Override
	public URL getProviderURL() {
		return providerURL;
	}

	@Override
	public void setProviderURL(final URL providerURL) {
		this.providerURL = providerURL;
	}

	@Override
	public void connect() throws IOException {
		while (shouldDisconnect) {
			try {
				Thread.sleep(1);
			} catch (final InterruptedException e) {
			}
		}
		finishedSendingStrings = false;

		socketChannel = SocketChannel.open(new InetSocketAddress(getProviderURL().getHost(),
				getProviderURL().getPort()));
		stringRegistry.sendOutAllStringRegistryRecords();

		finishedSendingStrings = true;
	}

	@Override
	public void sendOutStringRecord(final StringRegistryRecord record) {
		// while (!finishedSendingStrings) {
		// try {
		// Thread.sleep(1);
		// } catch (final InterruptedException e) {
		// }
		// }

		if (record.getRecordSizeInBytes() > stringBuffer.remaining()) {
			prioritizedSend(stringBuffer);
		}
		record.putIntoByteBuffer(stringBuffer, stringRegistry, this);
		prioritizedSend(stringBuffer);
	}

	@Override
	public void sendOutStringRecordAll(final StringRegistryRecord record) {
		if (record.getRecordSizeInBytes() > stringBuffer.remaining()) {
			prioritizedSend(stringBuffer);
		}
		record.putIntoByteBuffer(stringBuffer, stringRegistry, this);
	}

	@Override
	public void sendOutStringRecordAllSingle(final StringRegistryRecord record) {
		sendOutStringRecordAll(record);
		prioritizedSend(stringBuffer);
	}

	@Override
	public void processRecord(final IRecord record) {
		if (record instanceof Trace) {
			final Trace trace = (Trace) record;
			final List<AbstractEventRecord> traceEvents = trace.getTraceEvents();
			for (final AbstractEventRecord event : traceEvents) {
				if (sendingBuffer.remaining() < event.getRecordSizeInBytes()) {
					send(sendingBuffer);
				}
				event.putIntoByteBuffer(sendingBuffer, stringRegistry, this);
			}
			send(sendingBuffer);
		} else if (record instanceof ISerializableRecord) {
			final ISerializableRecord serializableRecord = (ISerializableRecord) record;
			if (sendingBuffer.remaining() < serializableRecord.getRecordSizeInBytes()) {
				send(sendingBuffer);
			}
			serializableRecord.putIntoByteBuffer(sendingBuffer, stringRegistry, this);
		} else if (record instanceof TimedPeriodRecord) {
			// send(buffer);
			// this the end of timedperiodrecords - master has its own
		} else if (record instanceof TerminateRecord) {
			terminate();
		}
	}

	@Override
	public void send(final ByteBuffer buffer) {
		while ((socketChannel == null) || (!socketChannel.isConnected())
				|| (!finishedSendingStrings)) {
			try {
				Thread.sleep(1);
			} catch (final InterruptedException e) {
			}
		}

		try {
			buffer.flip();
			while (buffer.hasRemaining()) {
				socketChannel.write(buffer);
			}
		} catch (final IOException e) {
			System.out.println("WARNING: Connection was closed - possible data loss");
			try {
				socketChannel.close();
			} catch (final IOException e1) {
			}
		} finally {
			buffer.clear();
			doDisconnectIfNessecary();
		}
	}

	public void prioritizedSend(final ByteBuffer buffer) {
		while ((socketChannel == null) || (!socketChannel.isConnected())) {
			try {
				Thread.sleep(1);
			} catch (final InterruptedException e) {
			}
		}

		try {
			buffer.flip();
			while (buffer.hasRemaining()) {
				socketChannel.write(buffer);
			}
		} catch (final IOException e) {
			System.out
					.println("WARNING: Connection was closed during String sending - possible data loss");
			try {
				socketChannel.close();
			} catch (final IOException e1) {
			}
		} finally {
			buffer.clear();
			doDisconnectIfNessecary();
		}
	}

	private void doDisconnectIfNessecary() {
		if (shouldDisconnect) {
			if ((socketChannel != null) && socketChannel.isConnected()) {
				try {
					socketChannel.close();
				} catch (final IOException e) {
					e.printStackTrace();
				}
			}
			shouldDisconnect = false;
		}
	}

	@Override
	public final void disconnect() {
		if (socketChannel != null) {
			shouldDisconnect = true;
		}
	}

	private void terminate() {
		shouldDisconnect = true;
		doDisconnectIfNessecary();
	}

	@Override
	public boolean isDisconnected() {
		if (socketChannel != null) {
			return !socketChannel.isConnected();
		}
		return true;
	}
}
