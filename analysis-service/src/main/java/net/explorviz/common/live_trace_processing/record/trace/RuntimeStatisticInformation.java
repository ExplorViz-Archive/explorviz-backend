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

	public RuntimeStatisticInformation(final int count, final double sum,
			final double squaredSum) {
		this.count = count;
		this.sum = sum;
		this.squaredSum = squaredSum;
		setInitialized(false);
	}

	public RuntimeStatisticInformation(final int count, final double sum,
			final double squaredSum, final TIntHashSet objectIds) {
		this.count = count;
		this.sum = sum;
		this.squaredSum = squaredSum;
		this.objectIds = objectIds;
		setInitialized(true);
	}

	public void makeAccumulator(final int objectId) {
		if (objectIds == null) {
			objectIds = new TIntHashSet(1);
			if (COLLECT_OBJECT_IDS) {
				objectIds.add(objectId);
			}
			setInitialized(true);
		}
	}

	public int getCount() {
		return count;
	}

	public double getAverage() {
		if (count > 0) {
			return sum / count;
		}
		return -1;
	}

	public TIntHashSet getObjectIds() {
		return objectIds;
	}

	public double getStandardDeviation() {
		if (count <= 2) {
			return -1;
		} else {
			final double variance = (squaredSum - sum * sum / count)
					/ (count - 1);
			return Math.sqrt(variance);
		}

	}

	public void merge(final RuntimeStatisticInformation statistics,
			final int objectId) {
		count += statistics.getCount();
		if (sum > 0) {
			sum += statistics.sum;
			squaredSum += statistics.squaredSum;
		} else {
			sum = statistics.sum;
			squaredSum = statistics.squaredSum;
		}
		if (COLLECT_OBJECT_IDS) {
			objectIds.add(objectId);
		}
	}

	public void merge(final RuntimeStatisticInformation statistics) {
		count += statistics.getCount();
		if (sum > 0) {
			sum += statistics.sum;
			squaredSum += statistics.squaredSum;
		} else {
			sum = statistics.sum;
			squaredSum = statistics.squaredSum;
		}
		if (COLLECT_OBJECT_IDS) {
			objectIds.addAll(statistics.getObjectIds());
		}
	}

	@Override
	public void putIntoByteBuffer(final ByteBuffer buffer,
			final StringRegistrySender stringRegistry,
			final IRecordSender writer) {
		// buffer.put(CLAZZ_ID);
		buffer.putInt(count);
		buffer.putDouble(sum);
		buffer.putDouble(squaredSum);
		buffer.putInt(objectIds.size());

		final TIntIterator iterator = objectIds.iterator();

		while (iterator.hasNext()) {
			buffer.putInt(iterator.next());
		}
	}

	public static RuntimeStatisticInformation createFromByteBuffer(
			final ByteBuffer buffer, final StringRegistryReceiver stringRegistry) {
		final int countFromBuffer = buffer.getInt();
		final double sumFromBuffer = buffer.getDouble();
		final double squaredSumFromBuffer = buffer.getDouble();
		final int objectIdsLength = buffer.getInt();

		final TIntHashSet objectIdsFromBuffer = new TIntHashSet(Math.max(
				objectIdsLength, 1));

		for (int i = 0; i < objectIdsLength; i++) {
			objectIdsFromBuffer.add(buffer.getInt());
		}

		return new RuntimeStatisticInformation(countFromBuffer, sumFromBuffer,
				squaredSumFromBuffer, objectIdsFromBuffer);
	}

	@Override
	public int getRecordSizeInBytes() {
		final int objectIdsLength = objectIds.size() * 4;
		return objectIdsLength + BYTE_LENGTH;
	}

	@Override
	public int compareTo(final IRecord o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String toString() {
		if (objectIds != null) {
			return "RuntimeStatisticInformation [count=" + count + ", average="
					+ getAverage() + ", standard deviation="
					+ getStandardDeviation() + ", objectIds.size()="
					+ objectIds.size() + "]";
		} else {
			return "RuntimeStatisticInformation [count=" + count + ", average="
					+ getAverage() + ", standard deviation="
					+ getStandardDeviation() + ", objectIds.size()=" + "null]";
		}
	}

	public void set(final int count, final long time, final long squaredTime) {
		this.count = count;
		sum = time;
		squaredSum = squaredTime;
	}

	public boolean isInitialized() {
		return initialized;
	}

	public void setInitialized(final boolean initialized) {
		this.initialized = initialized;
	}
}
