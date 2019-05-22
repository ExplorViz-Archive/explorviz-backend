package net.explorviz.common.live_trace_processing.record.event.remote;

import java.nio.ByteBuffer;
import net.explorviz.common.live_trace_processing.IdNotAvailableException;
import net.explorviz.common.live_trace_processing.StringRegistryReceiver;
import net.explorviz.common.live_trace_processing.StringRegistrySender;
import net.explorviz.common.live_trace_processing.record.IRecord;
import net.explorviz.common.live_trace_processing.record.event.AbstractBeforeEventRecord;
import net.explorviz.common.live_trace_processing.record.trace.HostApplicationMetaDataRecord;
import net.explorviz.common.live_trace_processing.writer.IRecordSender;

public class BeforeReceivedRemoteCallRecord extends AbstractBeforeEventRecord {
	public static final byte CLAZZ_ID = 10;
	public static final byte CLAZZ_ID_FROM_WORKER = CLAZZ_ID + 64;

	public static final int COMPRESSED_BYTE_LENGTH = AbstractBeforeEventRecord.COMPRESSED_BYTE_LENGTH + 8 + 4;
	public static final int COMPRESSED_BYTE_LENGTH_WITH_CLAZZ_ID = 1 + COMPRESSED_BYTE_LENGTH;

	public static final int BYTE_LENGTH = AbstractBeforeEventRecord.BYTE_LENGTH + 8 + 4;
	protected static final int BYTE_LENGTH_WITH_CLAZZ_ID = 1 + BYTE_LENGTH;

	private final long callerTraceId;
	private final int callerOrderIndex;

	public BeforeReceivedRemoteCallRecord(final long callerTraceId,
			final int callerOrderIndex, final long traceId,
			final int orderIndex, final HostApplicationMetaDataRecord thisHost) {
		super(traceId, orderIndex, thisHost);
		this.callerTraceId = callerTraceId;
		this.callerOrderIndex = callerOrderIndex;
	}

	public BeforeReceivedRemoteCallRecord(final ByteBuffer buffer,
			final StringRegistryReceiver stringRegistry)
			throws IdNotAvailableException {
		super(buffer, stringRegistry);

		callerTraceId = buffer.getLong();
		callerOrderIndex = buffer.getInt();
	}

	public long getCallerTraceId() {
		return callerTraceId;
	}

	public int getCallerOrderIndex() {
		return callerOrderIndex;
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

		buffer.putLong(callerTraceId);
		buffer.putInt(callerOrderIndex);
	}

	@Override
	public int compareTo(final IRecord o) {
		if (o instanceof BeforeReceivedRemoteCallRecord) {
			final BeforeReceivedRemoteCallRecord record2 = (BeforeReceivedRemoteCallRecord) o;

			final int cmpOrderIndex = getCallerOrderIndex()
					- record2.getCallerOrderIndex();
			if (cmpOrderIndex != 0) {
				return cmpOrderIndex;
			}

			final long cmpCallerTraceId = getCallerTraceId()
					- record2.getCallerTraceId();
			if (cmpCallerTraceId != 0) {
				return (int) cmpCallerTraceId;
			}

			return super.compareTo(o);
		}
		return -1;
	}

	@Override
	public String toString() {
		return BeforeReceivedRemoteCallRecord.class.getSimpleName() + " - "
				+ super.toString() + ", getCallerTraceId()="
				+ getCallerTraceId() + ", getCallerOrderIndex()="
				+ getCallerOrderIndex();
	}
}
