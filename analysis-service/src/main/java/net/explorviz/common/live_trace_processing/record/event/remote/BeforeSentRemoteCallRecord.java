package net.explorviz.common.live_trace_processing.record.event.remote;

import java.nio.ByteBuffer;
import net.explorviz.common.live_trace_processing.IdNotAvailableException;
import net.explorviz.common.live_trace_processing.StringRegistryReceiver;
import net.explorviz.common.live_trace_processing.StringRegistrySender;
import net.explorviz.common.live_trace_processing.record.IRecord;
import net.explorviz.common.live_trace_processing.record.event.AbstractBeforeEventRecord;
import net.explorviz.common.live_trace_processing.record.trace.HostApplicationMetaDataRecord;
import net.explorviz.common.live_trace_processing.writer.IRecordSender;

public class BeforeSentRemoteCallRecord extends AbstractBeforeEventRecord {
	public static final byte CLAZZ_ID = 14;
	public static final byte CLAZZ_ID_FROM_WORKER = CLAZZ_ID + 64;

	public static final int COMPRESSED_BYTE_LENGTH = AbstractBeforeEventRecord.COMPRESSED_BYTE_LENGTH + 4;
	public static final int COMPRESSED_BYTE_LENGTH_WITH_CLAZZ_ID = 1 + COMPRESSED_BYTE_LENGTH;

	public static final int BYTE_LENGTH = AbstractBeforeEventRecord.BYTE_LENGTH + 4;
	protected static final int BYTE_LENGTH_WITH_CLAZZ_ID = 1 + BYTE_LENGTH;

	private final String technology;

	// HACK
	private final long traceId;

	public BeforeSentRemoteCallRecord(final String technology,
			final long traceId, final int orderIndex,
			final HostApplicationMetaDataRecord thisHost) {
		super(traceId, orderIndex, thisHost);
		this.traceId = traceId;
		this.technology = technology;
	}

	public BeforeSentRemoteCallRecord(final ByteBuffer buffer,
			final StringRegistryReceiver stringRegistry)
			throws IdNotAvailableException {
		super(buffer, stringRegistry);

		traceId = super.getTraceId();
		technology = stringRegistry.getStringFromId(buffer.getInt());
	}

	public String getTechnology() {
		return technology;
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

		buffer.putInt(stringRegistry.getIdForString(technology));
	}

	@Override
	public int compareTo(final IRecord o) {
		if (o instanceof BeforeSentRemoteCallRecord) {
			final BeforeSentRemoteCallRecord record2 = (BeforeSentRemoteCallRecord) o;
			final int cmpTechnology = getTechnology().compareTo(
					record2.getTechnology());
			if (cmpTechnology != 0) {
				return cmpTechnology;
			}

			final long cmpTraceId = traceId - record2.traceId;
			if (cmpTraceId != 0) {
				return (int) cmpTraceId;
			}

			return super.compareTo(o);
		}

		return -1;
	}

	@Override
	public String toString() {
		return BeforeSentRemoteCallRecord.class.getSimpleName() + " - "
				+ super.toString() + ", getTechnology()=" + getTechnology();
	}
}
