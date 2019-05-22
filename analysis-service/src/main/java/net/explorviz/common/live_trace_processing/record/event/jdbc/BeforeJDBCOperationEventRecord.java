package net.explorviz.common.live_trace_processing.record.event.jdbc;

import java.nio.ByteBuffer;
import net.explorviz.common.live_trace_processing.IdNotAvailableException;
import net.explorviz.common.live_trace_processing.StringRegistryReceiver;
import net.explorviz.common.live_trace_processing.StringRegistrySender;
import net.explorviz.common.live_trace_processing.record.IRecord;
import net.explorviz.common.live_trace_processing.record.event.AbstractBeforeOperationEventRecord;
import net.explorviz.common.live_trace_processing.record.trace.HostApplicationMetaDataRecord;
import net.explorviz.common.live_trace_processing.writer.IRecordSender;

public class BeforeJDBCOperationEventRecord extends
		AbstractBeforeOperationEventRecord {
	public static final byte CLAZZ_ID = 19;
	public static final byte CLAZZ_ID_FROM_WORKER = CLAZZ_ID + 64;

	public static final int COMPRESSED_BYTE_LENGTH = AbstractBeforeOperationEventRecord.COMPRESSED_BYTE_LENGTH + 4;
	public static final int COMPRESSED_BYTE_LENGTH_WITH_CLAZZ_ID = 1 + COMPRESSED_BYTE_LENGTH;

	public static final int BYTE_LENGTH = AbstractBeforeOperationEventRecord.BYTE_LENGTH + 4;
	protected static final int BYTE_LENGTH_WITH_CLAZZ_ID = 1 + BYTE_LENGTH;

	private final String sqlStatement;

	public BeforeJDBCOperationEventRecord(final long traceId,
			final int orderIndex, final int objectId,
			final String operationSignature, final String clazz,
			final String implementedInterface, final String sqlStatement,
			final HostApplicationMetaDataRecord hostApplicationMetadata) {
		super(traceId, orderIndex, objectId, operationSignature, clazz,
				implementedInterface, hostApplicationMetadata);

		this.sqlStatement = sqlStatement;
	}

	public BeforeJDBCOperationEventRecord(final ByteBuffer buffer,
			final StringRegistryReceiver stringRegistry)
			throws IdNotAvailableException {
		super(buffer, stringRegistry);

		sqlStatement = stringRegistry.getStringFromId(buffer.getInt());
	}

	@Override
	public void putIntoByteBuffer(final ByteBuffer buffer,
			final StringRegistrySender stringRegistry,
			final IRecordSender writer) {
		buffer.put(CLAZZ_ID_FROM_WORKER);
		buffer.putInt(getRecordSizeInBytes());
		super.putIntoByteBuffer(buffer, stringRegistry, writer);

		buffer.putInt(stringRegistry.getIdForString(getSqlStatement()));
	}

	@Override
	public int compareTo(final IRecord o) {
		if (o instanceof BeforeJDBCOperationEventRecord) {
			final BeforeJDBCOperationEventRecord record2 = (BeforeJDBCOperationEventRecord) o;
			final int cmpSQLStatement = getSqlStatement().compareTo(
					record2.getSqlStatement());
			if (cmpSQLStatement != 0) {
				return cmpSQLStatement;
			}

			return super.compareTo(o);
		}
		return -1;
	}

	@Override
	public String toString() {
		return BeforeJDBCOperationEventRecord.class.getSimpleName() + " - "
				+ super.toString() + ", getSQLStatement()=" + getSqlStatement();
	}

	public String getSqlStatement() {
		return sqlStatement;
	}
}