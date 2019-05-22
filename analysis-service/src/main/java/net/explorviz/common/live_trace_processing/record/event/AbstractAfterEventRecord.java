package net.explorviz.common.live_trace_processing.record.event;

import java.nio.ByteBuffer;
import net.explorviz.common.live_trace_processing.IdNotAvailableException;
import net.explorviz.common.live_trace_processing.StringRegistryReceiver;
import net.explorviz.common.live_trace_processing.StringRegistrySender;
import net.explorviz.common.live_trace_processing.record.trace.HostApplicationMetaDataRecord;
import net.explorviz.common.live_trace_processing.writer.IRecordSender;

public abstract class AbstractAfterEventRecord extends AbstractEventRecord {
	public static final int COMPRESSED_BYTE_LENGTH = AbstractEventRecord.COMPRESSED_BYTE_LENGTH + 8;
	public static final int COMPRESSED_BYTE_LENGTH_WITH_CLAZZ_ID = 1 + COMPRESSED_BYTE_LENGTH;

	public static final int BYTE_LENGTH = AbstractEventRecord.BYTE_LENGTH + 8;
	protected static final int BYTE_LENGTH_WITH_CLAZZ_ID = 1 + BYTE_LENGTH;

	private final long methodDuration;

	public AbstractAfterEventRecord(final long methodDuration,
			final long traceId, final int orderIndex,
			final HostApplicationMetaDataRecord hostApplicationMetadata) {
		super(traceId, orderIndex, hostApplicationMetadata);
		this.methodDuration = methodDuration;
	}

	public AbstractAfterEventRecord(final ByteBuffer buffer,
			final StringRegistryReceiver stringRegistry)
			throws IdNotAvailableException {
		super(buffer, stringRegistry);
		methodDuration = buffer.getLong();
	}

	public long getMethodDuration() {
		return methodDuration;
	}

	@Override
	public void putIntoByteBuffer(final ByteBuffer buffer,
			final StringRegistrySender stringRegistry,
			final IRecordSender writer) {
		super.putIntoByteBuffer(buffer, stringRegistry, writer);
		buffer.putLong(getMethodDuration());
	}

	@Override
	public int getRecordSizeInBytes() {
		return 8 + super.getRecordSizeInBytes();
	}

	@Override
	public String toString() {
		return AbstractAfterEventRecord.class.getSimpleName() + " - "
				+ super.toString();
	}
}
