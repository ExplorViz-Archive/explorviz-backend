package net.explorviz.common.live_trace_processing.debug;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class DebugFileByteBufferWriter {
  private FileChannel channel = null;

  @SuppressWarnings("resource")
  public DebugFileByteBufferWriter(final String filename) {
    try {
      this.channel = new FileOutputStream(filename, false).getChannel();
    } catch (final FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  public void writeBuffer(final ByteBuffer buffer) {
    try {
      final ByteBuffer metaBuffer = this.createMetaData(buffer);
      metaBuffer.flip();

      while (metaBuffer.hasRemaining()) {
        this.channel.write(metaBuffer);
      }

      while (buffer.hasRemaining()) {
        this.channel.write(buffer);
      }
      buffer.flip();
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  private ByteBuffer createMetaData(final ByteBuffer buffer) {
    final ByteBuffer metaBuffer = ByteBuffer.allocateDirect(8 + 4);
    metaBuffer.putLong(System.nanoTime());
    metaBuffer.putInt(buffer.limit());
    return metaBuffer;
  }
}
