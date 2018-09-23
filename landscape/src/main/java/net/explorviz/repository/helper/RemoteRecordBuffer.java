package net.explorviz.repository.helper;

import net.explorviz.model.application.Clazz;

/**
 * Buffer for importing records from analysis.
 */
public class RemoteRecordBuffer {

  private final long timestampPutIntoBuffer = System.nanoTime();
  private Clazz belongingClazz;

  public long getTimestampPutIntoBuffer() {
    return timestampPutIntoBuffer;
  }

  public Clazz getBelongingClazz() {
    return belongingClazz;
  }

  public void setBelongingClazz(final Clazz belongingClazz) {
    this.belongingClazz = belongingClazz;
  }
}
