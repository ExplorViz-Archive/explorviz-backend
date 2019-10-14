package net.explorviz.landscape.repository;

import explorviz.live_trace_processing.reader.IPeriodicTimeSignalReceiver;
import explorviz.live_trace_processing.record.IRecord;
import javax.annotation.PostConstruct;

public interface LandscapeRepositoryModel extends IPeriodicTimeSignalReceiver {

  @Override void periodicTimeSignal(long timestamp);

  void insertIntoModel(IRecord inputIRecord);
}
