package net.explorviz.landscape.repository.helper;

import net.explorviz.landscape.model.application.Clazz;

/**
 * Buffer for importing records from analysis.
 */
public class RemoteRecordBuffer {

  // needs to be nano since the processing requires it
  private final long timestampPutIntoBuffer = java.lang.System.nanoTime();
  private Clazz belongingClazz;

  public long getTimestampPutIntoBuffer() {
    return this.timestampPutIntoBuffer;
  }

  public Clazz getBelongingClazz() {
    return this.belongingClazz;
  }

  public void setBelongingClazz(final Clazz belongingClazz) {
    this.belongingClazz = belongingClazz;
  }
}
