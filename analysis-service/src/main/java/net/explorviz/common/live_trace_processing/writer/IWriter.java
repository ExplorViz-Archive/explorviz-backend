package net.explorviz.common.live_trace_processing.writer;

import java.io.IOException;
import java.net.URL;

public interface IWriter {
  URL getProviderURL();

  void setProviderURL(final URL providerURL);

  void connect() throws IOException;

  void disconnect();

  boolean isDisconnected();
}
