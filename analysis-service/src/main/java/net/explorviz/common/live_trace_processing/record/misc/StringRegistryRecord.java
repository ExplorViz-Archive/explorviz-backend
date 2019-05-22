package net.explorviz.common.live_trace_processing.record.misc;

import java.nio.ByteBuffer;
import net.explorviz.common.live_trace_processing.StringRegistrySender;
import net.explorviz.common.live_trace_processing.record.IRecord;
import net.explorviz.common.live_trace_processing.record.ISerializableRecord;
import net.explorviz.common.live_trace_processing.writer.IRecordSender;

public class StringRegistryRecord implements ISerializableRecord {
  public static final byte CLAZZ_ID = 4;
  public static final int BYTE_LENGTH_WITHOUT_STRING = 4 + 4;
  public static final int BYTE_LENGTH_WITHOUT_STRING_WITH_CLAZZ_ID = 1 + BYTE_LENGTH_WITHOUT_STRING;

  private final String value;
  private final int id;

  public StringRegistryRecord(final String value, final int id) {
    this.value = value;
    this.id = id;
  }

  public String getValue() {
    return this.value;
  }

  public int getId() {
    return this.id;
  }

  @Override
  public void putIntoByteBuffer(final ByteBuffer buffer, final StringRegistrySender stringRegistry,
      final IRecordSender writer) {
    buffer.put(StringRegistryRecord.CLAZZ_ID);
    buffer.putInt(this.getId());

    final byte[] valueAsBytes = this.getValue().getBytes();
    buffer.putInt(valueAsBytes.length);
    buffer.put(valueAsBytes);
  }

  @Override
  public int getRecordSizeInBytes() {
    final byte[] valueAsBytes = this.getValue().getBytes();

    return StringRegistryRecord.BYTE_LENGTH_WITHOUT_STRING_WITH_CLAZZ_ID + valueAsBytes.length;
  }

  @Override
  public int compareTo(final IRecord o) {
    throw new UnsupportedOperationException();
  }

  @Override
  public String toString() {
    return "StringRegistryRecord [value=" + this.value + ", id=" + this.id + "]";
  }
}
