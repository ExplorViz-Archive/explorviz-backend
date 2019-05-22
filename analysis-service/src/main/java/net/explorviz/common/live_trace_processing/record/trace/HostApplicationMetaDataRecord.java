package net.explorviz.common.live_trace_processing.record.trace;

import java.nio.ByteBuffer;
import net.explorviz.common.live_trace_processing.IdNotAvailableException;
import net.explorviz.common.live_trace_processing.StringRegistryReceiver;
import net.explorviz.common.live_trace_processing.StringRegistrySender;
import net.explorviz.common.live_trace_processing.record.IRecord;
import net.explorviz.common.live_trace_processing.record.ISerializableRecord;
import net.explorviz.common.live_trace_processing.writer.IRecordSender;

public class HostApplicationMetaDataRecord implements ISerializableRecord {
  public static final byte CLAZZ_ID = 0;
  public static final int BYTE_LENGTH = 4 + 4 + 4 + 4 + 4;
  public static final int BYTE_LENGTH_WITH_CLAZZ_ID = 1 + BYTE_LENGTH;

  private final String systemname;
  private final String ipaddress;
  private final String hostname;
  private final String application;
  private final String programmingLanguage;

  public HostApplicationMetaDataRecord(final String systemname, final String ipaddress,
      final String hostname, final String application, final String programmingLanguage) {
    this.systemname = systemname;
    this.ipaddress = ipaddress;
    this.hostname = hostname;
    this.application = application;
    this.programmingLanguage = programmingLanguage;
  }

  public String getSystemname() {
    return this.systemname;
  }

  public String getIpaddress() {
    return this.ipaddress;
  }

  public String getHostname() {
    return this.hostname;
  }

  public String getApplication() {
    return this.application;
  }

  public String getProgrammingLanguage() {
    return this.programmingLanguage;
  }

  @Override
  public void putIntoByteBuffer(final ByteBuffer buffer, final StringRegistrySender stringReg,
      final IRecordSender writer) {
    // buffer.put(CLAZZ_ID);
    buffer.putInt(stringReg.getIdForString(this.systemname));
    buffer.putInt(stringReg.getIdForString(this.ipaddress));
    buffer.putInt(stringReg.getIdForString(this.hostname));
    buffer.putInt(stringReg.getIdForString(this.application));
    buffer.putInt(stringReg.getIdForString(this.programmingLanguage));
  }

  public static HostApplicationMetaDataRecord createFromByteBuffer(final ByteBuffer buffer,
      final StringRegistryReceiver stringRegistry) throws IdNotAvailableException {
    final int systemId = buffer.getInt();
    final int ipaddressId = buffer.getInt();
    final int hostId = buffer.getInt();
    final int appId = buffer.getInt();
    final int languageId = buffer.getInt();
    return new HostApplicationMetaDataRecord(stringRegistry.getStringFromId(systemId),
        stringRegistry.getStringFromId(ipaddressId), stringRegistry.getStringFromId(hostId),
        stringRegistry.getStringFromId(appId), stringRegistry.getStringFromId(languageId));
  }

  @Override
  public int getRecordSizeInBytes() {
    return BYTE_LENGTH;
  }

  @Override
  public int compareTo(final IRecord o) {
    if (o instanceof HostApplicationMetaDataRecord) {
      final HostApplicationMetaDataRecord metaDataRecord2 = (HostApplicationMetaDataRecord) o;
      final int hostCmp = this.getHostname().compareTo(metaDataRecord2.getHostname());
      if (hostCmp == 0) {
        return this.getApplication().compareTo(metaDataRecord2.getApplication());
      }
      return hostCmp;
    }
    return -1;
  }

  public boolean equals(final HostApplicationMetaDataRecord o2) {
    return this.hostname.equals(o2.getHostname()) && this.application.equals(o2.getApplication());
  };

  @Override
  public String toString() {
    return "HostApplicationMetaDataRecord [systemname=" + this.systemname + ", ipaddress="
        + this.ipaddress + ", hostname=" + this.hostname + ", application=" + this.application
        + ", programmingLanguage=" + this.programmingLanguage + "]";
  }
}
