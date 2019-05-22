package net.explorviz.common.live_trace_processing.record.misc;

import java.nio.ByteBuffer;
import net.explorviz.common.live_trace_processing.IdNotAvailableException;
import net.explorviz.common.live_trace_processing.StringRegistryReceiver;
import net.explorviz.common.live_trace_processing.StringRegistrySender;
import net.explorviz.common.live_trace_processing.record.IRecord;
import net.explorviz.common.live_trace_processing.record.ISerializableRecord;
import net.explorviz.common.live_trace_processing.record.trace.HostApplicationMetaDataRecord;
import net.explorviz.common.live_trace_processing.writer.IRecordSender;

public class SystemMonitoringRecord implements ISerializableRecord {
  public static final byte CLAZZ_ID = 5;
  public static final byte CLAZZ_ID_FROM_WORKER = CLAZZ_ID + 64;

  public static final int COMPRESSED_BYTE_LENGTH = 8 + 8 + 8;
  public static final int COMPRESSED_BYTE_LENGTH_WITH_CLAZZ_ID = 1 + COMPRESSED_BYTE_LENGTH;

  public static final int BYTE_LENGTH =
      COMPRESSED_BYTE_LENGTH + +HostApplicationMetaDataRecord.BYTE_LENGTH;
  public static final int BYTE_LENGTH_WITH_CLAZZ_ID = 1 + BYTE_LENGTH;

  private final double cpuUtilization;
  private final long usedRAM;
  private final long absoluteRAM;

  private final HostApplicationMetaDataRecord hostApplicationMetadata;

  public SystemMonitoringRecord(final double cpuUtilization, final long usedRAM,
      final long absoluteRAM, final HostApplicationMetaDataRecord hostApplicationMetadata) {
    this.cpuUtilization = cpuUtilization;
    this.usedRAM = usedRAM;
    this.absoluteRAM = absoluteRAM;

    this.hostApplicationMetadata = hostApplicationMetadata;
  }

  public double getCpuUtilization() {
    return this.cpuUtilization;
  }

  public long getUsedRAM() {
    return this.usedRAM;
  }

  public long getAbsoluteRAM() {
    return this.absoluteRAM;
  }

  public HostApplicationMetaDataRecord getHostApplicationMetadata() {
    return this.hostApplicationMetadata;
  }

  @Override
  public void putIntoByteBuffer(final ByteBuffer buffer, final StringRegistrySender stringRegistry,
      final IRecordSender writer) {
    buffer.put(CLAZZ_ID_FROM_WORKER);
    buffer.putDouble(this.getCpuUtilization());
    buffer.putLong(this.getUsedRAM());
    buffer.putLong(this.getAbsoluteRAM());
    this.getHostApplicationMetadata().putIntoByteBuffer(buffer, stringRegistry, writer);
  }

  public static SystemMonitoringRecord createFromByteBuffer(final ByteBuffer buffer,
      final StringRegistryReceiver stringRegistry) throws IdNotAvailableException {
    return new SystemMonitoringRecord(buffer.getDouble(), buffer.getLong(), buffer.getLong(),
        HostApplicationMetaDataRecord.createFromByteBuffer(buffer, stringRegistry));
  }

  @Override
  public String toString() {
    return "SystemMonitoringRecord [cpuUtilization=" + this.cpuUtilization + ", usedRAM="
        + this.usedRAM + ", absoluteRAM=" + this.absoluteRAM + ", getHostApplicationMetadata()="
        + this.getHostApplicationMetadata() + "]";
  }

  @Override
  public int getRecordSizeInBytes() {
    return BYTE_LENGTH_WITH_CLAZZ_ID;
  }

  @Override
  public int compareTo(final IRecord o) {
    throw new UnsupportedOperationException();
  }
}
