package net.explorviz.common.live_trace_processing.record.event.jdbc;

import java.nio.ByteBuffer;
import net.explorviz.common.live_trace_processing.IdNotAvailableException;
import net.explorviz.common.live_trace_processing.StringRegistryReceiver;
import net.explorviz.common.live_trace_processing.StringRegistrySender;
import net.explorviz.common.live_trace_processing.record.IRecord;
import net.explorviz.common.live_trace_processing.record.event.AbstractAfterEventRecord;
import net.explorviz.common.live_trace_processing.record.trace.HostApplicationMetaDataRecord;
import net.explorviz.common.live_trace_processing.writer.IRecordSender;

public class AfterJDBCOperationEventRecord extends AbstractAfterEventRecord {
	public static final byte CLAZZ_ID = 21;
	public static final byte CLAZZ_ID_FROM_WORKER = 64 + CLAZZ_ID;

	public static final int COMPRESSED_BYTE_LENGTH = AbstractAfterEventRecord.COMPRESSED_BYTE_LENGTH + 4;
	public static final int COMPRESSED_BYTE_LENGTH_WITH_CLAZZ_ID = 1 + COMPRESSED_BYTE_LENGTH;

	public static final int BYTE_LENGTH = AbstractAfterEventRecord.BYTE_LENGTH + 4;
	protected static final int BYTE_LENGTH_WITH_CLAZZ_ID = 1 + BYTE_LENGTH;

	private final String formattedReturnValue;

	public AfterJDBCOperationEventRecord(final long timestamp,
			final long traceId, final int orderIndex,
			final String formattedReturnValue,
			final HostApplicationMetaDataRecord hostApplicationMetadata) {
		super(timestamp, traceId, orderIndex, hostApplicationMetadata);

		this.formattedReturnValue = formattedReturnValue;
	}

	public AfterJDBCOperationEventRecord(final ByteBuffer buffer,
			final StringRegistryReceiver stringRegistry)
			throws IdNotAvailableException {
		super(buffer, stringRegistry);

		formattedReturnValue = stringRegistry.getStringFromId(buffer.getInt());
	}

	@Override
	public void putIntoByteBuffer(final ByteBuffer buffer,
			final StringRegistrySender stringRegistry,
			final IRecordSender writer) {
		buffer.put(CLAZZ_ID_FROM_WORKER);
		buffer.putInt(getRecordSizeInBytes());
		super.putIntoByteBuffer(buffer, stringRegistry, writer);

		buffer.putInt(stringRegistry.getIdForString(getFormattedReturnValue()));
	}

	@Override
	public int compareTo(final IRecord o) {
		if (o instanceof AfterJDBCOperationEventRecord) {
			final AfterJDBCOperationEventRecord record2 = (AfterJDBCOperationEventRecord) o;
			final int cmpReturnVal = getFormattedReturnValue().compareTo(
					record2.getFormattedReturnValue());
			if (cmpReturnVal != 0) {
				return cmpReturnVal;
			}

			return super.compareTo(o);
		}
		return -1;
	}

	@Override
	public String toString() {
		return AfterJDBCOperationEventRecord.class.getSimpleName() + " - "
				+ super.toString() + ", getFormattedReturnValue()="
				+ getFormattedReturnValue();
	}

	public String getFormattedReturnValue() {
		return formattedReturnValue;
	}
}
