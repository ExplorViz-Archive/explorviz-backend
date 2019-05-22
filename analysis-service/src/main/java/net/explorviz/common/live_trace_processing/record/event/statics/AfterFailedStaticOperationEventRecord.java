package net.explorviz.common.live_trace_processing.record.event.statics;

import java.nio.ByteBuffer;
import net.explorviz.common.live_trace_processing.IdNotAvailableException;
import net.explorviz.common.live_trace_processing.StringRegistryReceiver;
import net.explorviz.common.live_trace_processing.StringRegistrySender;
import net.explorviz.common.live_trace_processing.record.IRecord;
import net.explorviz.common.live_trace_processing.record.event.AbstractAfterFailedEventRecord;
import net.explorviz.common.live_trace_processing.record.trace.HostApplicationMetaDataRecord;
import net.explorviz.common.live_trace_processing.writer.IRecordSender;

public class AfterFailedStaticOperationEventRecord extends
		AbstractAfterFailedEventRecord {
	public static final byte CLAZZ_ID = 12;
	public static final byte CLAZZ_ID_FROM_WORKER = CLAZZ_ID + 64;

	public AfterFailedStaticOperationEventRecord(final long timestamp,
			final long traceId, final int orderIndex, final String cause,
			final HostApplicationMetaDataRecord hostApplicationMetadata) {
		super(timestamp, traceId, orderIndex, cause, hostApplicationMetadata);
	}

	public AfterFailedStaticOperationEventRecord(final ByteBuffer buffer,
			final StringRegistryReceiver stringRegistry)
			throws IdNotAvailableException {
		super(buffer, stringRegistry);
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
		if (o instanceof AfterFailedStaticOperationEventRecord) {
			return super.compareTo(o);
		}
		return -1;
	}

	@Override
	public String toString() {
		return AfterFailedStaticOperationEventRecord.class.getSimpleName()
				+ " - " + super.toString();
	}
}
