package net.explorviz.monitoring.live_trace_processing.writer;

import com.lmax.disruptor.EventHandler;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import net.explorviz.common.live_trace_processing.configuration.Configuration;
import net.explorviz.common.live_trace_processing.configuration.ConfigurationFactory;
import net.explorviz.common.live_trace_processing.filter.IPipeReceiver;
import net.explorviz.common.live_trace_processing.record.trace.HostApplicationMetaDataRecord;
import net.explorviz.common.live_trace_processing.writer.IWriter;
import net.explorviz.monitoring.live_trace_processing.main.ByteBufferEvent;
import net.explorviz.monitoring.live_trace_processing.main.MonitoringStringRegistry;


public class TCPWriter
    implements IPipeReceiver<ByteBufferEvent>, IWriter, EventHandler<ByteBufferEvent> {

  private URL providerURL;

  private SocketChannel socketChannel;

  private int systemnameId;
  private int ipaddressId;
  private int hostnameId;
  private int applicationId;
  private int languageId;

  private final Configuration configuration;

  private volatile boolean shouldDisconnect = false;
  private volatile boolean metaDataSent = false;
  private final boolean androidMonitoring;

  private static String applicationName;

  private final ByteBuffer bufferForMetaData =
      ByteBuffer.allocate(HostApplicationMetaDataRecord.BYTE_LENGTH_WITH_CLAZZ_ID);

  public static String getApplicationName() {
    return applicationName;
  }

  public TCPWriter(final String hostname, final int port, final boolean androidMonitoring,
      final Configuration configuration) {
    this.androidMonitoring = androidMonitoring;
    this.configuration = configuration;
    try {
      this.setProviderURL(new URL("http://" + hostname + ":" + port));
    } catch (final MalformedURLException e) {
      e.printStackTrace();
    }

  }

  @Override
  public URL getProviderURL() {
    return this.providerURL;
  }

  @Override
  public void setProviderURL(final URL providerURL) {
    this.providerURL = providerURL;
  }

  public void init() {
    this.setSystemName(this.configuration.getStringProperty(ConfigurationFactory.SYSTEM_NAME));
    this.setIpAddress(this.configuration.getStringProperty(ConfigurationFactory.IP_ADDRESS));
    this.setHostname(this.configuration.getStringProperty(ConfigurationFactory.HOST_NAME));
    this.setApplicationName(
        this.configuration.getStringProperty(ConfigurationFactory.APPLICATION_NAME));
    this.setProgrammingLanguage(
        this.configuration.getStringProperty(ConfigurationFactory.PROGRAMMING_LANGUAGE));
  }

  public void setSystemName(String systemname) {
    if (systemname.isEmpty()) {
      systemname = "<UNKNOWN-SYSTEM>";
    }

    this.systemnameId = MonitoringStringRegistry.getIdForStringWithoutSending(systemname);
  }

  public void setIpAddress(String ipAddress) {
    if (ipAddress.isEmpty()) {
      if (!this.androidMonitoring) {
        try {
          ipAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (final UnknownHostException e) {
        }
      }

      if (ipAddress.isEmpty()) {
        ipAddress = "<UNKNOWN-IP>";
      }
    }
    this.ipaddressId = MonitoringStringRegistry.getIdForStringWithoutSending(ipAddress);
  }

  public void setHostname(String hostname) {
    if (hostname.isEmpty()) {
      if (!this.androidMonitoring) {
        try {
          hostname = java.net.InetAddress.getLocalHost().getHostName();
        } catch (final UnknownHostException ex) {
          hostname = HostnameFetcher.getHostname();
        }
      }
      if (hostname == null || hostname.isEmpty()) {
        hostname = "<UNKNOWN-HOST>";
      }
    }
    this.hostnameId = MonitoringStringRegistry.getIdForStringWithoutSending(hostname);
  }

  public void setApplicationName(final String applicationParam) {
    if (applicationParam.isEmpty()) {
      applicationName = "<UNKNOWN-APPLICATION>";
    } else {
      applicationName = applicationParam;
    }
    this.applicationId = MonitoringStringRegistry.getIdForStringWithoutSending(applicationName);
  }

  public void setProgrammingLanguage(String programmingLanguageParam) {
    if (programmingLanguageParam.isEmpty()) {
      programmingLanguageParam = "<UNKNOWN-LANGUAGE>";
    }

    this.languageId =
        MonitoringStringRegistry.getIdForStringWithoutSending(programmingLanguageParam);
  }

  @Override
  public void connect() throws IOException {
    while (this.shouldDisconnect) {
      try {
        Thread.sleep(1);
      } catch (final InterruptedException e) {
      }
    }

    this.socketChannel = SocketChannel.open(
        new InetSocketAddress(this.getProviderURL().getHost(), this.getProviderURL().getPort()));

    this.bufferForMetaData.put(HostApplicationMetaDataRecord.CLAZZ_ID);
    this.bufferForMetaData.putInt(this.systemnameId);
    this.bufferForMetaData.putInt(this.ipaddressId);
    this.bufferForMetaData.putInt(this.hostnameId);
    this.bufferForMetaData.putInt(this.applicationId);
    this.bufferForMetaData.putInt(this.languageId);
    this.bufferForMetaData.flip();

    while (!this.socketChannel.isConnected()) {
      try {
        Thread.sleep(1);
      } catch (final InterruptedException e) {
      }
    }

    if (this.socketChannel.isConnected()) {
      this.socketChannel.write(this.bufferForMetaData);
      this.bufferForMetaData.clear();
      MonitoringStringRegistry.sendOutAllStringRegistryRecords(this.socketChannel);
      this.metaDataSent = true;
    } else {
      System.out.println("SEVERE: Could not send meta data buffer");
    }
  }

  @Override
  public void processRecord(final ByteBufferEvent event) {
    final ByteBuffer buffer = event.getValue();
    buffer.flip();
    while (this.socketChannel == null || !this.socketChannel.isConnected() || !this.metaDataSent) {
      try {
        Thread.sleep(1);
      } catch (final InterruptedException e) {
      }

      if (this.androidMonitoring) {
        try {
          this.connect();
        } catch (final IOException e) {
        }
      }
    }
    this.send(buffer);
    buffer.clear();
  }

  private void send(final ByteBuffer buffer) {
    try {
      while (buffer.hasRemaining()) {
        this.socketChannel.write(buffer);
      }
      this.doDisconnectIfNessecary();
    } catch (final IOException e) {
      try {
        this.socketChannel.close();
        this.providerURL = null;
        this.socketChannel = null;
      } catch (final IOException e1) {
      }
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
      this.metaDataSent = false;
      this.shouldDisconnect = false;
    }
  }

  @Override
  public final void disconnect() {
    if (this.socketChannel != null) {
      this.shouldDisconnect = true;
    }
  }

  @Override
  public void onEvent(final ByteBufferEvent arg0, final long arg1, final boolean arg2)
      throws Exception {
    this.processRecord(arg0);
  }

  @Override
  public boolean isDisconnected() {
    return this.socketChannel != null && !this.socketChannel.isConnected();
  }
}
