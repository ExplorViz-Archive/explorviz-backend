package net.explorviz.common.live_trace_processing;

import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.hash.TObjectIntHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import net.explorviz.common.live_trace_processing.record.misc.StringRegistryRecord;
import net.explorviz.common.live_trace_processing.writer.IStringRecordSender;

public class StringRegistrySender {
  private final TObjectIntHashMap<String> stringReg = new TObjectIntHashMap<>();

  private final AtomicInteger stringRegIndex = new AtomicInteger(1);

  private final IStringRecordSender sender;

  public StringRegistrySender(final IStringRecordSender sender) {
    this.sender = sender;
  }

  public int getSize() {
    return this.stringReg.size();
  }

  public final Integer getIdForString(final String value) {
    return this.getIdForStringGeneric(value, true);
  }

  public final Integer getIdForStringWithoutSending(final String value) {
    return this.getIdForStringGeneric(value, false);
  }

  private final Integer getIdForStringGeneric(final String value, final boolean withSending) {
    int result = this.stringReg.get(value);
    if (result == 0) {
      result = this.stringRegIndex.getAndIncrement();
      synchronized (this.stringReg) {
        this.stringReg.put(value, result);
        // System.out.println("stringRegistrySender size is now " +
        // getSize());

        if (withSending) {
          this.sender.sendOutStringRecord(new StringRegistryRecord(value, result));
        }
      }
    }

    return result;
  }

  public final void sendOutAllStringRegistryRecords() {
    synchronized (this.stringReg) {
      System.out.println("Sending out all " + this.stringReg.keys().length + " strings");

      for (final TObjectIntIterator<String> it = this.stringReg.iterator(); it.hasNext();) {
        it.advance();
        if (it.hasNext()) {
          this.sender.sendOutStringRecordAll(new StringRegistryRecord(it.key(), it.value()));
        } else {
          this.sender.sendOutStringRecordAllSingle(new StringRegistryRecord(it.key(), it.value()));
        }
      }
    }
  }
}
