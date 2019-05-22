package net.explorviz.common.live_trace_processing.writer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import net.explorviz.common.live_trace_processing.reader.RemoteConfigurationServlet;

public class RemoteConfigurator {
	public static void addPattern(final String host, final String pattern,
			final String application) {
		sentCommand(host, "addPattern", pattern, application);
	}

	public static void removePattern(final String host, final String pattern,
			final String application) {
		sentCommand(host, "removePattern", pattern, application);
	}

	private static void sentCommand(final String host, final String action,
			final String pattern, final String application) {
		try {

			final SocketChannel socketChannel = SocketChannel
					.open(new InetSocketAddress(host,
							RemoteConfigurationServlet.PORT));

			final ByteBuffer buffer = ByteBuffer.allocateDirect(16384);

			if (action.equalsIgnoreCase("addPattern")) {
				buffer.putInt(1);
			} else if (action.equalsIgnoreCase("removePattern")) {
				buffer.putInt(2);
			} else {
				socketChannel.close();
				return;
			}

			putStringIntoBuffer(application, buffer);
			putStringIntoBuffer(pattern, buffer);

			buffer.flip();
			if (socketChannel.isConnected()) {
				socketChannel.write(buffer);
			}

			socketChannel.close();
		} catch (final MalformedURLException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	private static void putStringIntoBuffer(final String value,
			final ByteBuffer buffer) {
		final byte[] valueAsBytes = value.getBytes();

		buffer.putInt(valueAsBytes.length);
		buffer.put(valueAsBytes);
	}
}
