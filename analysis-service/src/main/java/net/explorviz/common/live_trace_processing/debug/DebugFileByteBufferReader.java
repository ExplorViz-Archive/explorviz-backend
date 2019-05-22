package net.explorviz.common.live_trace_processing.debug;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import net.explorviz.common.live_trace_processing.record.trace.HostApplicationMetaDataRecord;

public class DebugFileByteBufferReader {
	private static final String DEBUG_TRACE = "debug.trace";

	private static FileChannel channel = null;
	private static long firstTimestamp = -1;
	private static long firstWallclockTimestamp = -1;
	private static boolean replayInRealTime = true;

	@SuppressWarnings("resource")
	public static void main(final String[] args) {
		try {
			channel = new FileInputStream(DEBUG_TRACE).getChannel();
			channel.position(0);
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		}

		try {
			final SocketChannel socketChannel = SocketChannel
					.open(new InetSocketAddress("127.0.0.1", 10133));

			while (channel.position() < channel.size()) {
				final ByteBuffer buffer = readOneByteBuffer();

				while (buffer.hasRemaining()) {
					socketChannel.write(buffer);
				}
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public static ByteBuffer readOneByteBuffer() throws IOException {
		final ByteBuffer metaBuffer = ByteBuffer.allocateDirect(
				HostApplicationMetaDataRecord.BYTE_LENGTH_WITH_CLAZZ_ID);
		try {
			channel.read(metaBuffer);
		} catch (final IOException e) {
			e.printStackTrace();
		}
		metaBuffer.flip();
		final long timestamp = metaBuffer.getLong();

		if (firstTimestamp == -1) {
			firstTimestamp = timestamp;
			firstWallclockTimestamp = System.nanoTime();
		} else {
			final long passedTime = timestamp - firstTimestamp;
			// System.out.println("Replaying timestamp " + timestamp);
			while (replayInRealTime && System.nanoTime()
					- firstWallclockTimestamp < passedTime) {
				if (passedTime > 1000 * 1000) {
					try {
						Thread.sleep(1000);
					} catch (final InterruptedException e) {
					}
				}
			}
		}

		final int bufferSize = metaBuffer.getInt();
		final ByteBuffer buffer = ByteBuffer.allocateDirect(bufferSize);

		try {
			channel.read(buffer);
		} catch (final IOException e) {
			e.printStackTrace();
		}

		buffer.flip();
		return buffer;
	}
}
