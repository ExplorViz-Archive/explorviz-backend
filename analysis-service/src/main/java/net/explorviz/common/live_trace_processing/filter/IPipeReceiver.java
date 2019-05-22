package net.explorviz.common.live_trace_processing.filter;

public interface IPipeReceiver<T> {
  void processRecord(T event);
}
