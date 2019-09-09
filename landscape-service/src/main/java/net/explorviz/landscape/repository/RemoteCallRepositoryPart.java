package net.explorviz.landscape.repository;

import explorviz.live_trace_processing.record.event.AbstractEventRecord;
import explorviz.live_trace_processing.record.event.remote.BeforeReceivedRemoteCallRecord;
import explorviz.live_trace_processing.record.event.remote.BeforeSentRemoteCallRecord;
import explorviz.live_trace_processing.record.trace.HostApplicationMetaDataRecord;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import net.explorviz.landscape.model.application.Application;
import net.explorviz.landscape.model.application.ApplicationCommunication;
import net.explorviz.landscape.model.application.Clazz;
import net.explorviz.landscape.model.landscape.Landscape;
import net.explorviz.landscape.model.landscape.Node;
import net.explorviz.landscape.repository.helper.RemoteRecordBuffer;



public class RemoteCallRepositoryPart {

  private static final int THIRTY_SECONDS = 30;

  private final Map<BeforeSentRemoteCallRecord, RemoteRecordBuffer> sentRemoteCallRecordCache =
      new HashMap<>();
  private final Map<BeforeReceivedRemoteCallRecord, RemoteRecordBuffer> receivedRemoteCallRecordCache =
      new HashMap<>();

  protected void checkForTimedoutRemoteCalls() {
    final long currentTime = java.lang.System.nanoTime(); // NOPMD

    final Iterator<Entry<BeforeReceivedRemoteCallRecord, RemoteRecordBuffer>> receivedIterator =
        this.receivedRemoteCallRecordCache.entrySet().iterator();
    while (receivedIterator.hasNext()) {
      final Entry<BeforeReceivedRemoteCallRecord, RemoteRecordBuffer> entry =
          receivedIterator.next();

      if (currentTime - TimeUnit.SECONDS.toNanos(THIRTY_SECONDS) > entry.getValue()
          .getTimestampPutIntoBuffer()) {
        receivedIterator.remove();
      }
    }

    final Iterator<Entry<BeforeSentRemoteCallRecord, RemoteRecordBuffer>> sentIterator =
        this.sentRemoteCallRecordCache.entrySet().iterator();

    while (sentIterator.hasNext()) {
      final Entry<BeforeSentRemoteCallRecord, RemoteRecordBuffer> entry = sentIterator.next();

      if (currentTime - TimeUnit.SECONDS.toNanos(THIRTY_SECONDS) > entry.getValue()
          .getTimestampPutIntoBuffer()) {
        sentIterator.remove();
      }
    }
  }

  public void insertSentRecord(final String potentialNewAppCommuId, final Clazz callerClazz,
      final BeforeSentRemoteCallRecord sentRemoteCallRecord, final Landscape landscape,
      final InsertionRepositoryPart inserter, final int runtimeIndex) {
    final BeforeReceivedRemoteCallRecord receivedRecord =
        this.seekMatchingReceivedRemoteRecord(sentRemoteCallRecord);

    if (receivedRecord == null) {
      final RemoteRecordBuffer remoteRecordBuffer = new RemoteRecordBuffer();
      remoteRecordBuffer.setBelongingClazz(callerClazz);

      this.sentRemoteCallRecordCache.put(sentRemoteCallRecord, remoteRecordBuffer);
    } else {
      this.seekOrCreateAppCommunication(potentialNewAppCommuId,
          sentRemoteCallRecord,
          receivedRecord,
          callerClazz,
          this.receivedRemoteCallRecordCache.get(receivedRecord).getBelongingClazz(),
          landscape,
          inserter,
          runtimeIndex);

      this.receivedRemoteCallRecordCache.remove(receivedRecord);
    }
  }

  public void insertReceivedRecord(final String potentialNewAppCommuId,
      final BeforeReceivedRemoteCallRecord receivedRemoteCallRecord, final Clazz firstReceiverClazz,
      final Landscape landscape, final InsertionRepositoryPart inserter, final int runtimeIndex) {
    final BeforeSentRemoteCallRecord sentRecord =
        this.seekSentRemoteTraceIdandOrderId(receivedRemoteCallRecord);

    if (sentRecord == null) {
      final RemoteRecordBuffer remoteRecordBuffer = new RemoteRecordBuffer();
      remoteRecordBuffer.setBelongingClazz(firstReceiverClazz);

      this.receivedRemoteCallRecordCache.put(receivedRemoteCallRecord, remoteRecordBuffer);
    } else {
      this.seekOrCreateAppCommunication(potentialNewAppCommuId,
          sentRecord,
          receivedRemoteCallRecord,
          this.sentRemoteCallRecordCache.get(sentRecord).getBelongingClazz(),
          firstReceiverClazz,
          landscape,
          inserter,
          runtimeIndex);

      this.sentRemoteCallRecordCache.remove(sentRecord);
    }
  }

  private BeforeReceivedRemoteCallRecord seekMatchingReceivedRemoteRecord(
      final BeforeSentRemoteCallRecord sentRecord) {
    for (final BeforeReceivedRemoteCallRecord receivedRemoteRecord : this.receivedRemoteCallRecordCache
        .keySet()) {
      if (receivedRemoteRecord.getCallerTraceId() == sentRecord.getTraceId()
          && receivedRemoteRecord.getCallerOrderIndex() == sentRecord.getOrderIndex()) {
        return receivedRemoteRecord;
      }
    }

    return null;
  }

  private BeforeSentRemoteCallRecord seekSentRemoteTraceIdandOrderId(
      final BeforeReceivedRemoteCallRecord remoteRecord) {
    for (final BeforeSentRemoteCallRecord sentRemoteRecord : this.sentRemoteCallRecordCache
        .keySet()) {
      if (sentRemoteRecord.getTraceId() == remoteRecord.getCallerTraceId()
          && sentRemoteRecord.getOrderIndex() == remoteRecord.getCallerOrderIndex()) {
        return sentRemoteRecord;
      }
    }

    return null;
  }

  // Communication between applications (landscape-perspective)
  private void seekOrCreateAppCommunication(final String potentialNewAppCommuId,
      final BeforeSentRemoteCallRecord sentRemoteCallRecord,
      final BeforeReceivedRemoteCallRecord receivedRemoteCallRecord, final Clazz sentRemoteClazz,
      final Clazz receivedRemoteClazz, final Landscape landscape,
      final InsertionRepositoryPart inserter, final int runtimeIndex) {

    final Application callerApplication =
        this.getHostApplication(sentRemoteCallRecord, inserter, landscape);
    final Application currentApplication =
        this.getHostApplication(receivedRemoteCallRecord, inserter, landscape);

    for (final ApplicationCommunication commu : landscape.getTotalApplicationCommunications()) {
      if (commu.getSourceApplication() == callerApplication
          && commu.getTargetApplication() == currentApplication
          || commu.getSourceApplication() == currentApplication
              && commu.getTargetApplication() == callerApplication) {
        commu.setRequests(
            commu.getRequests() + sentRemoteCallRecord.getRuntimeStatisticInformationList()
                .get(runtimeIndex)
                .getCount());

        final float oldAverage = commu.getAverageResponseTime();

        commu.setAverageResponseTime(
            (float) (oldAverage + sentRemoteCallRecord.getRuntimeStatisticInformationList()
                .get(runtimeIndex)
                .getAverage()) / 2f);

        landscape.getTimestamp()
            .setTotalRequests(landscape.getTimestamp().getTotalRequests()
                + sentRemoteCallRecord.getRuntimeStatisticInformationList()
                    .get(runtimeIndex)
                    .getCount());
        return;
      }
    }
    final ApplicationCommunication communication =
        new ApplicationCommunication(potentialNewAppCommuId);
    communication.setSourceApplication(callerApplication);
    communication.setSourceClazz(sentRemoteClazz);

    communication.setTargetApplication(currentApplication);
    communication.setTargetClazz(receivedRemoteClazz);

    communication.setRequests(
        sentRemoteCallRecord.getRuntimeStatisticInformationList().get(runtimeIndex).getCount());
    communication
        .setAverageResponseTime((float) sentRemoteCallRecord.getRuntimeStatisticInformationList()
            .get(runtimeIndex)
            .getAverage());
    communication.setTechnology(sentRemoteCallRecord.getTechnology());

    // add applicationCommunication to caller application
    callerApplication.getApplicationCommunications().add(communication);

    // add applicationCommunication to landscape
    landscape.getTotalApplicationCommunications().add(communication);

    landscape.getTimestamp()
        .setTotalRequests(landscape.getTimestamp().getTotalRequests()
            + sentRemoteCallRecord.getRuntimeStatisticInformationList()
                .get(runtimeIndex)
                .getCount());
  }

  public Application getHostApplication(final AbstractEventRecord record,
      final InsertionRepositoryPart inserter, final Landscape landscape) {
    final HostApplicationMetaDataRecord hostMeta =
        record.getHostApplicationMetadataList().iterator().next();
    final Node host = inserter.seekOrCreateNode(hostMeta, landscape);
    return inserter.seekOrCreateApplication(host, hostMeta, landscape);
  }
}
