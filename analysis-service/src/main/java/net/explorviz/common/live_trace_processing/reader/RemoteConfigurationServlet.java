package net.explorviz.common.live_trace_processing.reader;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashSet;
import java.util.Set;
import net.explorviz.common.live_trace_processing.adaptive_monitoring.AdaptiveMonitoringPatternList;
import net.explorviz.common.live_trace_processing.writer.RemoteConfigurator;

public class RemoteConfigurationServlet implements Runnable {
  private ServerSocketChannel serversocket;

  private final static Set<String> connectedChildren = new HashSet<>(); // TODO
                                                                        // concurrent...

  private boolean open = true;

  public static final int PORT = 10144;

  public static Set<String> getConnectedChildren() {
    return connectedChildren;
  }

  @Override
  public void run() {
    try {
      this.serversocket = ServerSocketChannel.open();
      this.serversocket.socket().bind(new InetSocketAddress(PORT));

      System.out.println("Started remote configuration on port " + PORT);

      while (this.open) {
        final SocketChannel socketChannel = this.serversocket.accept();
        final ByteBuffer buffer = ByteBuffer.allocateDirect(16384);

        if (socketChannel.isConnected()) {
          if (socketChannel.read(buffer) != -1) {
            buffer.flip();
            while (buffer.remaining() > 0) {
              final int action = buffer.getInt();
              if (action == 1 || action == 2) {
                final String application = this.readString(buffer);
                final String pattern = this.readString(buffer);

                if (action == 1) {
                  addPattern(application, pattern);
                } else if (action == 2) {
                  removePattern(application, pattern);
                }
              }
            }
            buffer.clear();
          }
        }
      }
    } catch (final AsynchronousCloseException ace) {
      return;
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  private String readString(final ByteBuffer buffer) {
    final int length = buffer.getInt();

    final byte[] stringByteArray = new byte[length];
    buffer.get(stringByteArray);

    return new String(stringByteArray);
  }

  public static void addPattern(final String application, final String pattern) {
    for (final String child : getConnectedChildren()) {
      RemoteConfigurator.addPattern(child, pattern, application);
    }

    AdaptiveMonitoringPatternList.addPattern(application, pattern);
  }

  public static void removePattern(final String application, final String pattern) {
    for (final String child : getConnectedChildren()) {
      RemoteConfigurator.removePattern(child, pattern, application);
    }

    AdaptiveMonitoringPatternList.removePattern(application, pattern);
  }

  public void stop() {
    this.open = false;
    try {
      this.serversocket.close();
    } catch (final IOException e) {
      e.printStackTrace();
    }

    System.out.println("Stopped remote configuration");
  }
}
