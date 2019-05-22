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

public class TCPConnector extends AbstractSink
    implements IWriter, IStringRecordSender, IRecordSender {
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
    this.sendingBuffer.clear();
    try {
      this.setProviderURL(new URL("http://" + hostname + ":" + port));
    } catch (final MalformedURLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void run() {
    this.tcpConnectorConnector.process(this);
  }

  @Override
  public URL getProviderURL() {
    return this.providerURL;
  }

  @Override
  public void setProviderURL(final URL providerURL) {
    this.providerURL = providerURL;
  }

  @Override
  public void connect() throws IOException {
    while (this.shouldDisconnect) {
      try {
        Thread.sleep(1);
      } catch (final InterruptedException e) {
      }
    }
    this.finishedSendingStrings = false;

    this.socketChannel = SocketChannel.open(
        new InetSocketAddress(this.getProviderURL().getHost(), this.getProviderURL().getPort()));
    this.stringRegistry.sendOutAllStringRegistryRecords();

    this.finishedSendingStrings = true;
  }

  @Override
  public void sendOutStringRecord(final StringRegistryRecord record) {
    // while (!finishedSendingStrings) {
    // try {
    // Thread.sleep(1);
    // } catch (final InterruptedException e) {
    // }
    // }

    if (record.getRecordSizeInBytes() > this.stringBuffer.remaining()) {
      this.prioritizedSend(this.stringBuffer);
    }
    record.putIntoByteBuffer(this.stringBuffer, this.stringRegistry, this);
    this.prioritizedSend(this.stringBuffer);
  }

  @Override
  public void sendOutStringRecordAll(final StringRegistryRecord record) {
    if (record.getRecordSizeInBytes() > this.stringBuffer.remaining()) {
      this.prioritizedSend(this.stringBuffer);
    }
    record.putIntoByteBuffer(this.stringBuffer, this.stringRegistry, this);
  }

  @Override
  public void sendOutStringRecordAllSingle(final StringRegistryRecord record) {
    this.sendOutStringRecordAll(record);
    this.prioritizedSend(this.stringBuffer);
  }

  @Override
  public void processRecord(final IRecord record) {
    if (record instanceof Trace) {
      final Trace trace = (Trace) record;
      final List<AbstractEventRecord> traceEvents = trace.getTraceEvents();
      for (final AbstractEventRecord event : traceEvents) {
        if (this.sendingBuffer.remaining() < event.getRecordSizeInBytes()) {
          this.send(this.sendingBuffer);
        }
        event.putIntoByteBuffer(this.sendingBuffer, this.stringRegistry, this);
      }
      this.send(this.sendingBuffer);
    } else if (record instanceof ISerializableRecord) {
      final ISerializableRecord serializableRecord = (ISerializableRecord) record;
      if (this.sendingBuffer.remaining() < serializableRecord.getRecordSizeInBytes()) {
        this.send(this.sendingBuffer);
      }
      serializableRecord.putIntoByteBuffer(this.sendingBuffer, this.stringRegistry, this);
    } else if (record instanceof TimedPeriodRecord) {
      // send(buffer);
      // this the end of timedperiodrecords - master has its own
    } else if (record instanceof TerminateRecord) {
      this.terminate();
    }
  }

  @Override
  public void send(final ByteBuffer buffer) {
    while (this.socketChannel == null || !this.socketChannel.isConnected()
        || !this.finishedSendingStrings) {
      try {
        Thread.sleep(1);
      } catch (final InterruptedException e) {
      }
    }

    try {
      buffer.flip();
      while (buffer.hasRemaining()) {
        this.socketChannel.write(buffer);
      }
    } catch (final IOException e) {
      System.out.println("WARNING: Connection was closed - possible data loss");
      try {
        this.socketChannel.close();
      } catch (final IOException e1) {
      }
    } finally {
      buffer.clear();
      this.doDisconnectIfNessecary();
    }
  }

  public void prioritizedSend(final ByteBuffer buffer) {
    while (this.socketChannel == null || !this.socketChannel.isConnected()) {
      try {
        Thread.sleep(1);
      } catch (final InterruptedException e) {
      }
    }

    try {
      buffer.flip();
      while (buffer.hasRemaining()) {
        this.socketChannel.write(buffer);
      }
    } catch (final IOException e) {
      System.out
          .println("WARNING: Connection was closed during String sending - possible data loss");
      try {
        this.socketChannel.close();
      } catch (final IOException e1) {
      }
    } finally {
      buffer.clear();
      this.doDisconnectIfNessecary();
    }
  }

  private void doDisconnectIfNessecary() {
    if (this.shouldDisconnect) {
      if (this.socketChannel != null && this.socketChannel.isConnected()) {
        try {
          this.socketChannel.close();
        } catch (final IOException e) {
          e.printStackTrace();
        }
      }
      this.shouldDisconnect = false;
    }
  }

  @Override
  public final void disconnect() {
    if (this.socketChannel != null) {
      this.shouldDisconnect = true;
    }
  }

  private void terminate() {
    this.shouldDisconnect = true;
    this.doDisconnectIfNessecary();
  }

  @Override
  public boolean isDisconnected() {
    if (this.socketChannel != null) {
      return !this.socketChannel.isConnected();
    }
    return true;
  }
}
