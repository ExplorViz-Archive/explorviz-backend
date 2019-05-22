package net.explorviz.common.live_trace_processing.record.event;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import net.explorviz.common.live_trace_processing.IdNotAvailableException;
import net.explorviz.common.live_trace_processing.StringRegistryReceiver;
import net.explorviz.common.live_trace_processing.StringRegistrySender;
import net.explorviz.common.live_trace_processing.record.IRecord;
import net.explorviz.common.live_trace_processing.record.ISerializableRecord;
import net.explorviz.common.live_trace_processing.record.trace.HostApplicationMetaDataRecord;
import net.explorviz.common.live_trace_processing.writer.IRecordSender;

public abstract class AbstractEventRecord implements ISerializableRecord {
	public static final int COMPRESSED_BYTE_LENGTH = 8 + 4;
	public static final int COMPRESSED_BYTE_LENGTH_WITH_CLAZZ_ID = 1 + COMPRESSED_BYTE_LENGTH;

	protected static final int BYTE_LENGTH = COMPRESSED_BYTE_LENGTH + 4; // plus
																			// 4
																			// for
																			// bytelength
																			// at
																			// start
	protected static final int BYTE_LENGTH_WITH_CLAZZ_ID = 1 + BYTE_LENGTH;

	private final long traceId;
	private final int orderIndex;

	private final List<HostApplicationMetaDataRecord> hostApplicationMetadataList = new ArrayList<HostApplicationMetaDataRecord>(
			2);

	public AbstractEventRecord(final long traceId, final int orderIndex,
			final HostApplicationMetaDataRecord hostApplicationMetadata) {
		this.traceId = traceId;
		this.orderIndex = orderIndex;
		hostApplicationMetadataList.add(hostApplicationMetadata);
	}

	public AbstractEventRecord(final ByteBuffer buffer,
			final StringRegistryReceiver stringRegistry)
			throws IdNotAvailableException {
		traceId = buffer.getLong();
		orderIndex = buffer.getInt();
		final int hostListSize = buffer.getInt();

		for (int i = 0; i < hostListSize; i++) {
			hostApplicationMetadataList.add(HostApplicationMetaDataRecord
					.createFromByteBuffer(buffer, stringRegistry));
		}
	}

	public long getTraceId() {
		return traceId;
	}

	public int getOrderIndex() {
		return orderIndex;
	}

	public List<HostApplicationMetaDataRecord> getHostApplicationMetadataList() {
		return hostApplicationMetadataList;
	}

	@Override
	public String toString() {
		return "getTraceId()=" + getTraceId() + ", getOrderIndex()="
				+ getOrderIndex() + ", getHostApplicationMetadata()="
				+ getHostApplicationMetadataList();
	}

	@Override
	public void putIntoByteBuffer(final ByteBuffer buffer,
			final StringRegistrySender stringRegistry,
			final IRecordSender writer) {
		buffer.putLong(getTraceId());
		buffer.putInt(getOrderIndex());
		buffer.putInt(getHostApplicationMetadataList().size());

		for (final HostApplicationMetaDataRecord hostMeta : getHostApplicationMetadataList()) {
			if (hostMeta != null) {
				hostMeta.putIntoByteBuffer(buffer, stringRegistry, writer);
			} else {
				System.out
						.println("Wanted to put null hostMeta to buffer output...");
				System.out.println("List is:");
				for (final HostApplicationMetaDataRecord hostMeta2 : getHostApplicationMetadataList()) {
					if (hostMeta2 != null) {
						System.out.println(hostMeta2);
					}
				}
			}
		}
	}

	@Override
	public int getRecordSizeInBytes() {
		return BYTE_LENGTH + 4 + getHostApplicationMetadataList().size()
				* HostApplicationMetaDataRecord.BYTE_LENGTH_WITH_CLAZZ_ID;
	}

	@Override
	public int compareTo(final IRecord o) {
		if (o instanceof AbstractEventRecord) {
			// final AbstractEventRecord record2 = (AbstractEventRecord) o;
			//
			// final int cmpOrderIndex = getOrderIndex() -
			// record2.getOrderIndex();
			//
			// if (cmpOrderIndex != 0) {
			// return cmpOrderIndex;
			// }

			// final int cmpHostApp =
			// getHostApplicationMetadataList().compareTo(
			// record2.getHostApplicationMetadataList());

			// if (cmpHostApp != 0) {
			// return cmpHostApp;
			// }

			return 0;
		}
		return -1;
	}
}
