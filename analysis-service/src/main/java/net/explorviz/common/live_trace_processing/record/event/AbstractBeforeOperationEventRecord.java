package net.explorviz.common.live_trace_processing.record.event;

import java.nio.ByteBuffer;
import net.explorviz.common.live_trace_processing.IdNotAvailableException;
import net.explorviz.common.live_trace_processing.StringRegistryReceiver;
import net.explorviz.common.live_trace_processing.StringRegistrySender;
import net.explorviz.common.live_trace_processing.record.IRecord;
import net.explorviz.common.live_trace_processing.record.trace.HostApplicationMetaDataRecord;
import net.explorviz.common.live_trace_processing.writer.IRecordSender;

public abstract class AbstractBeforeOperationEventRecord extends
		AbstractBeforeEventRecord {
	public static final int COMPRESSED_BYTE_LENGTH = AbstractBeforeEventRecord.COMPRESSED_BYTE_LENGTH
			+ 4 + 4 + 4 + 4;
	public static final int COMPRESSED_BYTE_LENGTH_WITH_CLAZZ_ID = 1 + COMPRESSED_BYTE_LENGTH;

	public static final int BYTE_LENGTH = AbstractBeforeEventRecord.BYTE_LENGTH
			+ 4 + 4 + 4 + 4;
	protected static final int BYTE_LENGTH_WITH_CLAZZ_ID = 1 + BYTE_LENGTH;

	private final int objectId;
	private final String operationSignature;
	private final String clazz;
	private final String implementedInterface;

	public AbstractBeforeOperationEventRecord(final long traceId,
			final int orderIndex, final int objectId,
			final String operationSignature, final String clazz,
			final String implementedInterface,
			final HostApplicationMetaDataRecord hostApplicationMetadata) {
		super(traceId, orderIndex, hostApplicationMetadata);
		this.objectId = objectId;
		this.operationSignature = operationSignature;
		this.clazz = clazz;
		this.implementedInterface = implementedInterface;
	}

	public AbstractBeforeOperationEventRecord(final ByteBuffer buffer,
			final StringRegistryReceiver stringRegistry)
			throws IdNotAvailableException {
		super(buffer, stringRegistry);
		objectId = buffer.getInt();
		operationSignature = stringRegistry.getStringFromId(buffer.getInt());
		clazz = stringRegistry.getStringFromId(buffer.getInt());
		implementedInterface = stringRegistry.getStringFromId(buffer.getInt());
	}

	public int getObjectId() {
		return objectId;
	}

	public String getOperationSignature() {
		return operationSignature;
	}

	public String getClazz() {
		return clazz;
	}

	public String getImplementedInterface() {
		return implementedInterface;
	}

	@Override
	public void putIntoByteBuffer(final ByteBuffer buffer,
			final StringRegistrySender stringRegistry,
			final IRecordSender writer) {
		super.putIntoByteBuffer(buffer, stringRegistry, writer);

		buffer.putInt(getObjectId());
		buffer.putInt(stringRegistry.getIdForString(getOperationSignature()));
		buffer.putInt(stringRegistry.getIdForString(getClazz()));
		buffer.putInt(stringRegistry.getIdForString(getImplementedInterface()));
	}

	@Override
	public int compareTo(final IRecord o) {
		if (o instanceof AbstractBeforeOperationEventRecord) {
			final AbstractBeforeOperationEventRecord record2 = (AbstractBeforeOperationEventRecord) o;

			final int cmpSignature = getOperationSignature().compareTo(
					record2.getOperationSignature());
			if (cmpSignature != 0) {
				return cmpSignature;
			}

			return super.compareTo(record2);
		}
		return -1;
	}

	@Override
	public int getRecordSizeInBytes() {
		return super.getRecordSizeInBytes() + BYTE_LENGTH
				- AbstractBeforeEventRecord.BYTE_LENGTH;
	}

	@Override
	public String toString() {
		return AbstractBeforeOperationEventRecord.class.getSimpleName() + " - "
				+ super.toString() + ", getObjectId()=" + getObjectId()
				+ ", getOperationSignature()=" + getOperationSignature()
				+ ", getClazz()=" + getClazz() + ", getImplementedInterface()="
				+ getImplementedInterface();
	}
}
