package net.explorviz.common.live_trace_processing.record.event;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import net.explorviz.common.live_trace_processing.IdNotAvailableException;
import net.explorviz.common.live_trace_processing.StringRegistryReceiver;
import net.explorviz.common.live_trace_processing.StringRegistrySender;
import net.explorviz.common.live_trace_processing.record.IRecord;
import net.explorviz.common.live_trace_processing.record.trace.HostApplicationMetaDataRecord;
import net.explorviz.common.live_trace_processing.record.trace.RuntimeStatisticInformation;
import net.explorviz.common.live_trace_processing.writer.IRecordSender;

public abstract class AbstractBeforeEventRecord extends AbstractEventRecord {
	public static final int COMPRESSED_BYTE_LENGTH = AbstractEventRecord.COMPRESSED_BYTE_LENGTH;
	public static final int COMPRESSED_BYTE_LENGTH_WITH_CLAZZ_ID = 1 + COMPRESSED_BYTE_LENGTH;

	protected static final int BYTE_LENGTH = AbstractEventRecord.BYTE_LENGTH
			+ RuntimeStatisticInformation.BYTE_LENGTH;
	protected static final int BYTE_LENGTH_WITH_CLAZZ_ID = 1 + BYTE_LENGTH;

	private List<RuntimeStatisticInformation> runtimeStatisticInformationList;

	public AbstractBeforeEventRecord(final long traceId, final int orderIndex,
			final HostApplicationMetaDataRecord hostApplicationMetadata) {
		super(traceId, orderIndex, hostApplicationMetadata);
	}

	public AbstractBeforeEventRecord(final ByteBuffer buffer,
			final StringRegistryReceiver stringRegistry)
			throws IdNotAvailableException {
		super(buffer, stringRegistry);
		final int runtimeSize = buffer.getInt();

		runtimeStatisticInformationList = new ArrayList<RuntimeStatisticInformation>(
				runtimeSize);

		for (int i = 0; i < runtimeSize; i++) {
			runtimeStatisticInformationList.add(RuntimeStatisticInformation
					.createFromByteBuffer(buffer, stringRegistry));
		}
	}

	public List<RuntimeStatisticInformation> getRuntimeStatisticInformationList() {
		return runtimeStatisticInformationList;
	}

	public void setRuntimeStatisticInformationList(
			final List<RuntimeStatisticInformation> runtimeStatisticInformationList) {
		this.runtimeStatisticInformationList = runtimeStatisticInformationList;
	}

	@Override
	public void putIntoByteBuffer(final ByteBuffer buffer,
			final StringRegistrySender stringRegistry,
			final IRecordSender writer) {
		super.putIntoByteBuffer(buffer, stringRegistry, writer);

		buffer.putInt(getRuntimeStatisticInformationList().size());

		for (final RuntimeStatisticInformation runtime : getRuntimeStatisticInformationList()) {
			runtime.putIntoByteBuffer(buffer, stringRegistry, writer);
		}
	}

	@Override
	public int compareTo(final IRecord o) {
		if (o instanceof AbstractBeforeEventRecord) {
			return super.compareTo(o);
		}
		return -1;
	}

	@Override
	public int getRecordSizeInBytes() {
		return 4 + RuntimeStatisticInformation.BYTE_LENGTH
				* getRuntimeStatisticInformationList().size()
				+ super.getRecordSizeInBytes(); // TODO only
												// without
												// objectids..
	}

	@Override
	public String toString() {
		return AbstractBeforeEventRecord.class.getSimpleName() + " - "
				+ super.toString() + ", getRuntime()="
				+ getRuntimeStatisticInformationList();
	}
}
