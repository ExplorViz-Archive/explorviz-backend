package net.explorviz.common.live_trace_processing.record.trace;

import gnu.trove.iterator.TIntIterator;
import gnu.trove.set.hash.TIntHashSet;
import java.nio.ByteBuffer;
import net.explorviz.common.live_trace_processing.StringRegistryReceiver;
import net.explorviz.common.live_trace_processing.StringRegistrySender;
import net.explorviz.common.live_trace_processing.record.IRecord;
import net.explorviz.common.live_trace_processing.record.ISerializableRecord;
import net.explorviz.common.live_trace_processing.writer.IRecordSender;

public final class RuntimeStatisticInformation implements ISerializableRecord {
  public static final int BYTE_LENGTH = 4 + 8 + 8 + 4;
  public static final int BYTE_LENGTH_WITH_CLAZZ_ID = BYTE_LENGTH + 1;

  private static final boolean COLLECT_OBJECT_IDS = true;

  private int count;
  private double sum;
  private double squaredSum;

  private TIntHashSet objectIds;
  private boolean initialized;

  public RuntimeStatisticInformation(final int count, final double sum, final double squaredSum) {
    this.count = count;
    this.sum = sum;
    this.squaredSum = squaredSum;
    this.setInitialized(false);
  }

  public RuntimeStatisticInformation(final int count, final double sum, final double squaredSum,
      final TIntHashSet objectIds) {
    this.count = count;
    this.sum = sum;
    this.squaredSum = squaredSum;
    this.objectIds = objectIds;
    this.setInitialized(true);
  }

  public void makeAccumulator(final int objectId) {
    if (this.objectIds == null) {
      this.objectIds = new TIntHashSet(1);
      if (COLLECT_OBJECT_IDS) {
        this.objectIds.add(objectId);
      }
      this.setInitialized(true);
    }
  }

  public int getCount() {
    return this.count;
  }

  public double getAverage() {
    if (this.count > 0) {
      return this.sum / this.count;
    }
    return -1;
  }

  public TIntHashSet getObjectIds() {
    return this.objectIds;
  }

  public double getStandardDeviation() {
    if (this.count <= 2) {
      return -1;
    } else {
      final double variance =
          (this.squaredSum - this.sum * this.sum / this.count) / (this.count - 1);
      return Math.sqrt(variance);
    }

  }

  public void merge(final RuntimeStatisticInformation statistics, final int objectId) {
    this.count += statistics.getCount();
    if (this.sum > 0) {
      this.sum += statistics.sum;
      this.squaredSum += statistics.squaredSum;
    } else {
      this.sum = statistics.sum;
      this.squaredSum = statistics.squaredSum;
    }
    if (COLLECT_OBJECT_IDS) {
      this.objectIds.add(objectId);
    }
  }

  public void merge(final RuntimeStatisticInformation statistics) {
    this.count += statistics.getCount();
    if (this.sum > 0) {
      this.sum += statistics.sum;
      this.squaredSum += statistics.squaredSum;
    } else {
      this.sum = statistics.sum;
      this.squaredSum = statistics.squaredSum;
    }
    if (COLLECT_OBJECT_IDS) {
      this.objectIds.addAll(statistics.getObjectIds());
    }
  }

  @Override
  public void putIntoByteBuffer(final ByteBuffer buffer, final StringRegistrySender stringRegistry,
      final IRecordSender writer) {
    // buffer.put(CLAZZ_ID);
    buffer.putInt(this.count);
    buffer.putDouble(this.sum);
    buffer.putDouble(this.squaredSum);
    buffer.putInt(this.objectIds.size());

    final TIntIterator iterator = this.objectIds.iterator();

    while (iterator.hasNext()) {
      buffer.putInt(iterator.next());
    }
  }

  public static RuntimeStatisticInformation createFromByteBuffer(final ByteBuffer buffer,
      final StringRegistryReceiver stringRegistry) {
    final int countFromBuffer = buffer.getInt();
    final double sumFromBuffer = buffer.getDouble();
    final double squaredSumFromBuffer = buffer.getDouble();
    final int objectIdsLength = buffer.getInt();

    final TIntHashSet objectIdsFromBuffer = new TIntHashSet(Math.max(objectIdsLength, 1));

    for (int i = 0; i < objectIdsLength; i++) {
      objectIdsFromBuffer.add(buffer.getInt());
    }

    return new RuntimeStatisticInformation(countFromBuffer, sumFromBuffer, squaredSumFromBuffer,
        objectIdsFromBuffer);
  }

  @Override
  public int getRecordSizeInBytes() {
    final int objectIdsLength = this.objectIds.size() * 4;
    return objectIdsLength + BYTE_LENGTH;
  }

  @Override
  public int compareTo(final IRecord o) {
    throw new UnsupportedOperationException();
  }

  @Override
  public String toString() {
    if (this.objectIds != null) {
      return "RuntimeStatisticInformation [count=" + this.count + ", average=" + this.getAverage()
          + ", standard deviation=" + this.getStandardDeviation() + ", objectIds.size()="
          + this.objectIds.size() + "]";
    } else {
      return "RuntimeStatisticInformation [count=" + this.count + ", average=" + this.getAverage()
          + ", standard deviation=" + this.getStandardDeviation() + ", objectIds.size()=" + "null]";
    }
  }

  public void set(final int count, final long time, final long squaredTime) {
    this.count = count;
    this.sum = time;
    this.squaredSum = squaredTime;
  }

  public boolean isInitialized() {
    return this.initialized;
  }

  public void setInitialized(final boolean initialized) {
    this.initialized = initialized;
  }
}
