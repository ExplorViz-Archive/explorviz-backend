package net.explorviz.server.repository.helper;

import net.explorviz.model.Clazz;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.xbase.lib.Pure;

@SuppressWarnings("all")
public class RemoteRecordBuffer {
  @Accessors
  private long timestampPutIntoBuffer = System.nanoTime();
  
  @Accessors
  private Clazz belongingClazz;
  
  @Pure
  public long getTimestampPutIntoBuffer() {
    return this.timestampPutIntoBuffer;
  }
  
  public void setTimestampPutIntoBuffer(final long timestampPutIntoBuffer) {
    this.timestampPutIntoBuffer = timestampPutIntoBuffer;
  }
  
  @Pure
  public Clazz getBelongingClazz() {
    return this.belongingClazz;
  }
  
  public void setBelongingClazz(final Clazz belongingClazz) {
    this.belongingClazz = belongingClazz;
  }
}
