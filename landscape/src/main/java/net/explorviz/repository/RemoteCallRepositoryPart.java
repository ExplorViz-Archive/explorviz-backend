package net.explorviz.repository;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import explorviz.live_trace_processing.record.event.AbstractEventRecord;
import explorviz.live_trace_processing.record.event.remote.BeforeReceivedRemoteCallRecord;
import explorviz.live_trace_processing.record.event.remote.BeforeSentRemoteCallRecord;
import explorviz.live_trace_processing.record.trace.HostApplicationMetaDataRecord;
import net.explorviz.model.application.Application;
import net.explorviz.model.application.ApplicationCommunication;
import net.explorviz.model.application.Clazz;
import net.explorviz.model.landscape.Landscape;
import net.explorviz.model.landscape.Node;
import net.explorviz.repository.helper.RemoteRecordBuffer;

public class RemoteCallRepositoryPart {
	private final Map<BeforeSentRemoteCallRecord, RemoteRecordBuffer> sentRemoteCallRecordCache = new HashMap<BeforeSentRemoteCallRecord, RemoteRecordBuffer>();
	private final Map<BeforeReceivedRemoteCallRecord, RemoteRecordBuffer> receivedRemoteCallRecordCache = new HashMap<BeforeReceivedRemoteCallRecord, RemoteRecordBuffer>();

	protected void checkForTimedoutRemoteCalls() {
		final long currentTime = java.lang.System.nanoTime();

		final Iterator<Entry<BeforeReceivedRemoteCallRecord, RemoteRecordBuffer>> receivedIterator = receivedRemoteCallRecordCache
				.entrySet().iterator();
		while (receivedIterator.hasNext()) {
			final Entry<BeforeReceivedRemoteCallRecord, RemoteRecordBuffer> entry = receivedIterator.next();

			if ((currentTime - TimeUnit.SECONDS.toNanos(30)) > entry.getValue().getTimestampPutIntoBuffer()) {
				receivedIterator.remove();
			}
		}

		final Iterator<Entry<BeforeSentRemoteCallRecord, RemoteRecordBuffer>> sentIterator = sentRemoteCallRecordCache
				.entrySet().iterator();

		while (sentIterator.hasNext()) {
			final Entry<BeforeSentRemoteCallRecord, RemoteRecordBuffer> entry = sentIterator.next();

			if ((currentTime - TimeUnit.SECONDS.toNanos(30)) > entry.getValue().getTimestampPutIntoBuffer()) {
				sentIterator.remove();
			}
		}
	}

	public void insertSentRecord(final Clazz callerClazz, final BeforeSentRemoteCallRecord sentRemoteCallRecord,
			final Landscape landscape, final InsertionRepositoryPart inserter, final int runtimeIndex) {
		final BeforeReceivedRemoteCallRecord receivedRecord = seekMatchingReceivedRemoteRecord(sentRemoteCallRecord);

		if (receivedRecord == null) {
			final RemoteRecordBuffer remoteRecordBuffer = new RemoteRecordBuffer();
			remoteRecordBuffer.setBelongingClazz(callerClazz);

			sentRemoteCallRecordCache.put(sentRemoteCallRecord, remoteRecordBuffer);
		} else {
			seekOrCreateCommunication(sentRemoteCallRecord, receivedRecord, callerClazz,
					receivedRemoteCallRecordCache.get(receivedRecord).getBelongingClazz(), landscape, inserter,
					runtimeIndex);

			receivedRemoteCallRecordCache.remove(receivedRecord);
		}
	}

	public void insertReceivedRecord(final BeforeReceivedRemoteCallRecord receivedRemoteCallRecord,
			final Clazz firstReceiverClazz, final Landscape landscape, final InsertionRepositoryPart inserter,
			final int runtimeIndex) {
		final BeforeSentRemoteCallRecord sentRecord = seekSentRemoteTraceIDandOrderID(receivedRemoteCallRecord);

		if (sentRecord == null) {
			final RemoteRecordBuffer remoteRecordBuffer = new RemoteRecordBuffer();
			remoteRecordBuffer.setBelongingClazz(firstReceiverClazz);

			receivedRemoteCallRecordCache.put(receivedRemoteCallRecord, remoteRecordBuffer);
		} else {
			seekOrCreateCommunication(sentRecord, receivedRemoteCallRecord,
					sentRemoteCallRecordCache.get(sentRecord).getBelongingClazz(), firstReceiverClazz, landscape,
					inserter, runtimeIndex);

			sentRemoteCallRecordCache.remove(sentRecord);
		}
	}

	private BeforeReceivedRemoteCallRecord seekMatchingReceivedRemoteRecord(
			final BeforeSentRemoteCallRecord sentRecord) {
		for (final BeforeReceivedRemoteCallRecord receivedRemoteRecord : receivedRemoteCallRecordCache.keySet()) {
			if (receivedRemoteRecord.getCallerTraceId() == sentRecord.getTraceId()
					&& receivedRemoteRecord.getCallerOrderIndex() == sentRecord.getOrderIndex()) {
				return receivedRemoteRecord;
			}
		}

		return null;
	}

	private BeforeSentRemoteCallRecord seekSentRemoteTraceIDandOrderID(
			final BeforeReceivedRemoteCallRecord remoteRecord) {
		for (final BeforeSentRemoteCallRecord sentRemoteRecord : sentRemoteCallRecordCache.keySet()) {
			if (sentRemoteRecord.getTraceId() == remoteRecord.getCallerTraceId()
					&& sentRemoteRecord.getOrderIndex() == remoteRecord.getCallerOrderIndex()) {
				return sentRemoteRecord;
			}
		}

		return null;
	}

	// Communication between applications (landscape-perspective)
	private void seekOrCreateCommunication(final BeforeSentRemoteCallRecord sentRemoteCallRecord,
			final BeforeReceivedRemoteCallRecord receivedRemoteCallRecord, final Clazz sentRemoteClazz,
			final Clazz receivedRemoteClazz, final Landscape landscape, final InsertionRepositoryPart inserter,
			final int runtimeIndex) {

		final Application callerApplication = getHostApplication(sentRemoteCallRecord, inserter, landscape);
		final Application currentApplication = getHostApplication(receivedRemoteCallRecord, inserter, landscape);

		for (final ApplicationCommunication commu : landscape.getOutgoingApplicationCommunications()) {
			if (commu.getSourceApplication() == callerApplication && commu.getTargetApplication() == currentApplication
					|| commu.getSourceApplication() == currentApplication
					&& commu.getTargetApplication() == callerApplication) {
				commu.setRequests(commu.getRequests()
						+ sentRemoteCallRecord.getRuntimeStatisticInformationList().get(runtimeIndex).getCount());

				final float oldAverage = commu.getAverageResponseTime();

				commu.setAverageResponseTime((float) (oldAverage
						+ sentRemoteCallRecord.getRuntimeStatisticInformationList().get(runtimeIndex).getAverage())
						/ 2f);

				landscape.getTimestamp().setCalls(landscape.getTimestamp().getCalls()
						+ sentRemoteCallRecord.getRuntimeStatisticInformationList().get(runtimeIndex).getCount());
				return;
			}
		}
		final ApplicationCommunication communication = new ApplicationCommunication();
		communication.setSourceApplication(callerApplication);
		communication.setSourceClazz(sentRemoteClazz);

		communication.setTargetApplication(currentApplication);
		communication.setTargetClazz(receivedRemoteClazz);

		communication
		.setRequests(sentRemoteCallRecord.getRuntimeStatisticInformationList().get(runtimeIndex).getCount());
		communication.setAverageResponseTime(
				(float) sentRemoteCallRecord.getRuntimeStatisticInformationList().get(runtimeIndex).getAverage());
		communication.setTechnology(sentRemoteCallRecord.getTechnology());

		// add applicationCommunication to caller application
		callerApplication.getOutgoingApplicationCommunications().add(communication);

		// addapplicationCommunication to landscap
		landscape.getOutgoingApplicationCommunications().add(communication);

		landscape.getTimestamp().setCalls(landscape.getTimestamp().getCalls()
				+ sentRemoteCallRecord.getRuntimeStatisticInformationList().get(runtimeIndex).getCount());
	}

	public Application getHostApplication(final AbstractEventRecord record, final InsertionRepositoryPart inserter,
			final Landscape landscape) {
		final HostApplicationMetaDataRecord hostMeta = record.getHostApplicationMetadataList().iterator().next();
		final Node host = inserter.seekOrCreateNode(hostMeta, landscape);
		return inserter.seekOrCreateApplication(host, hostMeta, landscape);
	}
}
