package net.explorviz.common.live_trace_processing.record.event;

import java.nio.ByteBuffer;
import net.explorviz.common.live_trace_processing.IdNotAvailableException;
import net.explorviz.common.live_trace_processing.StringRegistryReceiver;
import net.explorviz.common.live_trace_processing.StringRegistrySender;
import net.explorviz.common.live_trace_processing.record.IRecord;
import net.explorviz.common.live_trace_processing.record.trace.HostApplicationMetaDataRecord;
import net.explorviz.common.live_trace_processing.writer.IRecordSender;

public abstract class AbstractAfterFailedEventRecord extends
		AbstractEventRecord {
	public static final int COMPRESSED_BYTE_LENGTH = 8 + 4 + AbstractEventRecord.COMPRESSED_BYTE_LENGTH;
	public static final int COMPRESSED_BYTE_LENGTH_WITH_CLAZZ_ID = 1 + COMPRESSED_BYTE_LENGTH;

	public static final int BYTE_LENGTH = 8 + 4 + AbstractEventRecord.BYTE_LENGTH;
	protected static final int BYTE_LENGTH_WITH_CLAZZ_ID = 1 + BYTE_LENGTH;

	private final long methodDuration;
	private final String cause;

	public AbstractAfterFailedEventRecord(final long methodDuration,
			final long traceId, final int orderIndex, final String cause,
			final HostApplicationMetaDataRecord hostApplicationMetadata) {
		super(traceId, orderIndex, hostApplicationMetadata);
		this.methodDuration = methodDuration;
		this.cause = cause;
	}

	public AbstractAfterFailedEventRecord(final ByteBuffer buffer,
			final StringRegistryReceiver stringRegistry)
			throws IdNotAvailableException {
		super(buffer, stringRegistry);
		methodDuration = buffer.getLong();
		cause = stringRegistry.getStringFromId(buffer.getInt());
	}

	public long getMethodDuration() {
		return methodDuration;
	}

	public String getCause() {
		return cause;
	}

	@Override
	public void putIntoByteBuffer(final ByteBuffer buffer,
			final StringRegistrySender stringRegistry,
			final IRecordSender writer) {
		super.putIntoByteBuffer(buffer, stringRegistry, writer);
		buffer.putLong(getMethodDuration());
		buffer.putInt(stringRegistry.getIdForString(getCause()));
	}

	@Override
	public int compareTo(final IRecord o) {
		if (o instanceof AbstractAfterFailedEventRecord) {
			// final AbstractAfterFailedEventRecord record2 =
			// (AbstractAfterFailedEventRecord) o;
			//
			// final int cmpError = getCause().compareTo(record2.getCause());
			// if (cmpError != 0) {
			// return cmpError;
			// }
			return super.compareTo(o);
		}
		return -1;
	}

	@Override
	public int getRecordSizeInBytes() {
		return 8 + 4 + super.getRecordSizeInBytes();
	}

	@Override
	public String toString() {
		return AbstractAfterFailedEventRecord.class.getSimpleName() + " - "
				+ super.toString() + ", getCause()=" + getCause();
	}
}
