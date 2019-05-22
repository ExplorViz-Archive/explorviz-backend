package net.explorviz.common.live_trace_processing.record.event.normal;

import java.nio.ByteBuffer;
import net.explorviz.common.live_trace_processing.IdNotAvailableException;
import net.explorviz.common.live_trace_processing.StringRegistryReceiver;
import net.explorviz.common.live_trace_processing.StringRegistrySender;
import net.explorviz.common.live_trace_processing.record.IRecord;
import net.explorviz.common.live_trace_processing.record.event.AbstractBeforeOperationEventRecord;
import net.explorviz.common.live_trace_processing.record.trace.HostApplicationMetaDataRecord;
import net.explorviz.common.live_trace_processing.writer.IRecordSender;

public class BeforeOperationEventRecord extends
		AbstractBeforeOperationEventRecord {
	public static final byte CLAZZ_ID = 1;
	public static final byte CLAZZ_ID_FROM_WORKER = CLAZZ_ID + 64;

	public BeforeOperationEventRecord(final long traceId, final int orderIndex,
			final int objectId, final String operationSignature,
			final String clazz, final String implementedInterface,
			final HostApplicationMetaDataRecord hostApplicationMetadata) {
		super(traceId, orderIndex, objectId, operationSignature, clazz,
				implementedInterface, hostApplicationMetadata);
	}

	public BeforeOperationEventRecord(final ByteBuffer buffer,
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
		if (o instanceof BeforeOperationEventRecord) {
			return super.compareTo(o);
		}
		return -1;
	}

	@Override
	public String toString() {
		return BeforeOperationEventRecord.class.getSimpleName() + " - "
				+ super.toString();
	}
}