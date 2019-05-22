package net.explorviz.common.live_trace_processing.record.event.remote;

import java.nio.ByteBuffer;
import net.explorviz.common.live_trace_processing.IdNotAvailableException;
import net.explorviz.common.live_trace_processing.StringRegistryReceiver;
import net.explorviz.common.live_trace_processing.StringRegistrySender;
import net.explorviz.common.live_trace_processing.record.IRecord;
import net.explorviz.common.live_trace_processing.record.event.AbstractAfterEventRecord;
import net.explorviz.common.live_trace_processing.record.trace.HostApplicationMetaDataRecord;
import net.explorviz.common.live_trace_processing.writer.IRecordSender;

public class AfterReceivedRemoteCallRecord extends AbstractAfterEventRecord {
	public static final byte CLAZZ_ID = 18;
	public static final byte CLAZZ_ID_FROM_WORKER = CLAZZ_ID + 64;

	public static final int COMPRESSED_BYTE_LENGTH = AbstractAfterEventRecord.COMPRESSED_BYTE_LENGTH;
	public static final int COMPRESSED_BYTE_LENGTH_WITH_CLAZZ_ID = 1 + COMPRESSED_BYTE_LENGTH;

	public static final int BYTE_LENGTH = AbstractAfterEventRecord.BYTE_LENGTH;
	protected static final int BYTE_LENGTH_WITH_CLAZZ_ID = 1 + BYTE_LENGTH;

	public AfterReceivedRemoteCallRecord(final long timestamp,
			final long traceId, final int orderIndex,
			final HostApplicationMetaDataRecord thisHost) {
		super(timestamp, traceId, orderIndex, thisHost);
	}

	public AfterReceivedRemoteCallRecord(final ByteBuffer buffer,
			final StringRegistryReceiver stringRegistry)
			throws IdNotAvailableException {
		super(buffer, stringRegistry);
	}

	@Override
	public int getRecordSizeInBytes() {
		return BYTE_LENGTH_WITH_CLAZZ_ID;
	}

	@Override
	public void putIntoByteBuffer(final ByteBuffer buffer,
			final StringRegistrySender stringRegistry,
			final IRecordSender writer) {
		buffer.put(CLAZZ_ID_FROM_WORKER);
		buffer.putInt(getRecordSizeInBytes());
		super.putIntoByteBuffer(buffer, stringRegistry, writer);
	}

	@Override
	public int compareTo(final IRecord o) {
		if (o instanceof AfterReceivedRemoteCallRecord) {
			return super.compareTo(o);
		}
		return -1;
	}

	@Override
	public String toString() {
		return AfterReceivedRemoteCallRecord.class.getSimpleName() + " - "
				+ super.toString();
	}
}
