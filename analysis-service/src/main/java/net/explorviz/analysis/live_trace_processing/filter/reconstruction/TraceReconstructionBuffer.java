package net.explorviz.analysis.live_trace_processing.filter.reconstruction;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import net.explorviz.common.live_trace_processing.Constants;
import net.explorviz.common.live_trace_processing.record.event.AbstractAfterEventRecord;
import net.explorviz.common.live_trace_processing.record.event.AbstractAfterFailedEventRecord;
import net.explorviz.common.live_trace_processing.record.event.AbstractBeforeEventRecord;
import net.explorviz.common.live_trace_processing.record.event.AbstractEventRecord;
import net.explorviz.common.live_trace_processing.record.event.remote.BeforeReceivedRemoteCallRecord;
import net.explorviz.common.live_trace_processing.record.event.remote.BeforeSentRemoteCallRecord;
import net.explorviz.common.live_trace_processing.record.trace.RuntimeStatisticInformation;
import net.explorviz.common.live_trace_processing.record.trace.Trace;

class TraceReconstructionBuffer {
  private final List<AbstractEventRecord> events =
      new ArrayList<>(Constants.TRACE_RECONSTRUCTION_BUFFER_INITIAL_SIZE);

  private int openEvents;

  private final int TIMEOUT_IN_SECONDS;
  private int updatedInThisPeriodCounter;

  private int maxOrderIndex = -1;

  public TraceReconstructionBuffer(final int traceTimeoutInSec) {
    this.TIMEOUT_IN_SECONDS = traceTimeoutInSec;
    this.updatedInThisPeriodCounter = this.TIMEOUT_IN_SECONDS;
  }

  public final void insertEvent(final AbstractEventRecord event) {
    this.resetTimeoutCounter();
    this.setMaxOrderIndex(event);

    if (event instanceof AbstractBeforeEventRecord) {
      this.openEvents++;
      final AbstractBeforeEventRecord beforeEvent = (AbstractBeforeEventRecord) event;
      if (beforeEvent.getRuntimeStatisticInformationList() == null) {
        final ArrayList<RuntimeStatisticInformation> runtime = new ArrayList<>(1);
        runtime.add(new RuntimeStatisticInformation(1, -1, -1));
        beforeEvent.setRuntimeStatisticInformationList(runtime);
      }
    } else if (event instanceof AbstractAfterFailedEventRecord
        || event instanceof AbstractAfterEventRecord) {
      this.openEvents--;
    }

    this.events.add(event);
  }

  public boolean isTimedout() {
    return this.updatedInThisPeriodCounter <= 0;
  }

  public void decreaseTimeoutCounter() {
    this.updatedInThisPeriodCounter--;
  }

  public void resetTimeoutCounter() {
    this.updatedInThisPeriodCounter = this.TIMEOUT_IN_SECONDS;
  }

  private final int setMaxOrderIndex(final AbstractEventRecord event) {
    final int orderIndex = event.getOrderIndex();
    if (orderIndex > this.maxOrderIndex) {
      this.maxOrderIndex = orderIndex;
    }
    return orderIndex;
  }

  public final boolean isFinished() {
    return !this.isInvalid();
  }

  public final boolean isInvalid() {
    return this.openEvents != 0 || this.events.isEmpty()
        || this.maxOrderIndex + 1 != this.events.size();
  }

  public final Trace toTrace(final boolean valid) {
    final Stack<AbstractBeforeEventRecord> stack = new Stack<>();
    boolean containsRemoteRecord = false;

    for (final AbstractEventRecord event : this.events) {
      if (event instanceof AbstractBeforeEventRecord) {
        final AbstractBeforeEventRecord beforeEvent = (AbstractBeforeEventRecord) event;
        stack.push(beforeEvent);
        if (event instanceof BeforeReceivedRemoteCallRecord
            || event instanceof BeforeSentRemoteCallRecord) {
          containsRemoteRecord = true;
        }
      } else if (event instanceof AbstractAfterEventRecord) {
        this.initRuntimeIfNeccessary(stack, ((AbstractAfterEventRecord) event).getMethodDuration());
      } else if (event instanceof AbstractAfterFailedEventRecord) {
        this.initRuntimeIfNeccessary(stack,
            ((AbstractAfterFailedEventRecord) event).getMethodDuration());
      }
    }
    return new Trace(new ArrayList<>(this.events), valid, containsRemoteRecord);
  }

  private void initRuntimeIfNeccessary(final Stack<AbstractBeforeEventRecord> stack,
      final long methodDuration) {
    if (!stack.isEmpty()) {
      final AbstractBeforeEventRecord beforeEvent = stack.pop();

      final List<RuntimeStatisticInformation> runtimeStatisticInformationList =
          beforeEvent.getRuntimeStatisticInformationList();
      if (runtimeStatisticInformationList.size() == 1) { // only on first
        // reconstruction
        final RuntimeStatisticInformation runtimeStatisticInformation =
            runtimeStatisticInformationList.get(0);
        if (!runtimeStatisticInformation.isInitialized()) {
          runtimeStatisticInformation.set(1, methodDuration, methodDuration * methodDuration);
        }
      }
    }
  }
}
