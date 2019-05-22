package net.explorviz.monitoring.live_trace_processing.main;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import net.explorviz.common.live_trace_processing.Constants;
import net.explorviz.common.live_trace_processing.record.misc.StringRegistryRecord;

public class MonitoringStringRegistry {
  private static final ConcurrentHashMap<String, Integer> stringReg = new ConcurrentHashMap<>();
  private static final AtomicInteger stringRegIndex = new AtomicInteger(1);

  private static final ByteBuffer sendOutSingleStringRecordBuffer =
      ByteBuffer.allocate(Constants.SENDING_BUFFER_SIZE);
  private static final ByteBuffer sendOutAllStringRecordsBuffer =
      ByteBuffer.allocate(Constants.SENDING_BUFFER_SIZE);

  public static final Integer getIdForString(final String value) {
    final Integer result = stringReg.get(value);
    if (result == null) {
      final Integer perhapsNewIndex = stringRegIndex.getAndIncrement();
      final Integer wasPresent = stringReg.putIfAbsent(value, perhapsNewIndex);

      if (wasPresent == null) {
        synchronized (sendOutSingleStringRecordBuffer) {
          MonitoringController.sendOutBuffer(buildStringRegistryRecord(value, perhapsNewIndex));
        }

        return perhapsNewIndex;
      } else {
        return wasPresent;
      }
    }

    return result;
  }

  /**
   * only accessed single threaded by TCPWriter
   *
   * @param value
   * @return
   */
  public static final Integer getIdForStringWithoutSending(final String value) {
    Integer result = stringReg.get(value);
    if (result == null) {
      result = stringRegIndex.getAndIncrement();
      stringReg.putIfAbsent(value, result);
    }

    return result;
  }

  protected static ByteBuffer buildStringRegistryRecord(final String value, final int result) {
    final ByteBuffer buffer = sendOutSingleStringRecordBuffer;

    buffer.put(StringRegistryRecord.CLAZZ_ID);
    buffer.putInt(result);
    final byte[] valueAsBytes = value.getBytes();
    buffer.putInt(valueAsBytes.length);
    buffer.put(valueAsBytes);

    return buffer;
  }

  public static final void sendOutAllStringRegistryRecords(final SocketChannel socketChannel)
      throws IOException {
    final List<Entry<String, Integer>> keyValues = new ArrayList<>(stringReg.entrySet());
    int currentIndex = 0;
    int lastWrittenIndex = 0;
    final ByteBuffer buffer = sendOutAllStringRecordsBuffer;

    while (currentIndex < keyValues.size()) {
      Entry<String, Integer> entry = keyValues.get(currentIndex);
      byte[] keyAsBytes = entry.getKey().getBytes();

      while (buffer.remaining() >= keyAsBytes.length
          + StringRegistryRecord.BYTE_LENGTH_WITHOUT_STRING_WITH_CLAZZ_ID) {
        buffer.put(StringRegistryRecord.CLAZZ_ID);
        buffer.putInt(entry.getValue());
        buffer.putInt(keyAsBytes.length);
        buffer.put(keyAsBytes);
        currentIndex++;
        if (currentIndex == keyValues.size()) {
          break;
        }
        entry = keyValues.get(currentIndex);
        keyAsBytes = entry.getKey().getBytes();
      }

      if (lastWrittenIndex == currentIndex) {
        System.out.println("No progress in sendOutAllStringRegistry. Buffer size: "
            + buffer.remaining() + " and wanted to write: "
            + (keyAsBytes.length + StringRegistryRecord.BYTE_LENGTH_WITHOUT_STRING_WITH_CLAZZ_ID));
      }

      lastWrittenIndex = currentIndex;

      buffer.flip();
      while (buffer.hasRemaining()) {
        socketChannel.write(buffer);
      }
      buffer.clear();
    }
  }

  public static void reset() {
    stringReg.clear();
    stringRegIndex.set(1);
  }
}
