package net.explorviz.monitoring.live_trace_processing.probe.distributed.concrete;

import java.io.IOException;
import javax.jms.JMSException;
import javax.jms.Message;
import net.explorviz.monitoring.live_trace_processing.probe.distributed.DistributedMonitoringRecordWriter;
import net.explorviz.monitoring.live_trace_processing.probe.distributed.DistributedMonitoringTempDisabler;
import net.explorviz.monitoring.live_trace_processing.probe.tracemanagement.ProbeTraceMetaData;
import net.explorviz.monitoring.live_trace_processing.probe.tracemanagement.TraceRegistry;
import org.apache.activemq.broker.region.MessageReference;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQMessage;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;


/**
 * This aspect intercepts incoming and outgoing ActiveMQ messages. It adds monitoring information
 * into the message header and sends out records to ExplorViz.
 */
@Aspect
public class ActiveMQProbe {
  private static final String TRACE_ID_HEADER_ATTRIBUTE = "ExplorViz:super:header:unique:TraceID";

  private static final String ORDER_ID_HEADER_ATTRIBUTE = "ExplorViz:super:header:unique:OrderID";

  @Before("execution(void org.apache.activemq.command.ActiveMQMessage.onSend())")
  public void messageSent(final JoinPoint thisJoinPoint) {
    final DistributedMonitoringTempDisabler probeController =
        DistributedMonitoringTempDisabler.getProbeController();
    if (probeController.isMonitoringEnabled()) {
      probeController.disableMonitoring();

      final ActiveMQMessage message = (ActiveMQMessage) thisJoinPoint.getThis();
      if (message == null) {
        probeController.enableMonitoring();
        return;
      }

      try {
        if (!message.propertyExists(TRACE_ID_HEADER_ATTRIBUTE)
            && !message.propertyExists(ORDER_ID_HEADER_ATTRIBUTE)) {
          final ProbeTraceMetaData trace = TraceRegistry.getTrace();
          trace.incrementStackDepth();

          final long traceId = trace.getTraceId();
          final int orderId = trace.getNextOrderId();

          message.setLongProperty(TRACE_ID_HEADER_ATTRIBUTE, traceId);
          message.setIntProperty(ORDER_ID_HEADER_ATTRIBUTE, orderId);

          final ActiveMQDestination destination = message.getDestination();
          final String destinationName = destination != null ? destination.getQualifiedName()
              : DistributedMonitoringRecordWriter.UNKNOWN_DESTINATION;
          DistributedMonitoringRecordWriter
              .writeBeforeSentRecord(traceId, orderId, destinationName);
        }
      } catch (final JMSException e) {
      } finally {
        probeController.enableMonitoring();
      }
    }
  }

  @AfterReturning(
      pointcut = "execution(javax.jms.Message org.apache.activemq.ActiveMQMessageConsumer.receive(..))",
      returning = "message")
  public void messageReceived(final Message message) {
    final DistributedMonitoringTempDisabler probeController =
        DistributedMonitoringTempDisabler.getProbeController();
    if (probeController.isMonitoringEnabled()) {
      probeController.disableMonitoring();
      final ActiveMQMessage messageAMQ = (ActiveMQMessage) message;

      if (messageAMQ == null) {
        probeController.enableMonitoring();
        return;
      }

      final ProbeTraceMetaData trace = TraceRegistry.getTrace();
      trace.incrementStackDepth();
      final long ownTraceId = trace.getTraceId();
      final int ownOrderId = trace.getNextOrderId();

      try {
        if (messageAMQ.propertyExists(TRACE_ID_HEADER_ATTRIBUTE)
            && messageAMQ.propertyExists(ORDER_ID_HEADER_ATTRIBUTE)) {
          try {
            // Message has monitoring information
            final long traceId = messageAMQ.getLongProperty(TRACE_ID_HEADER_ATTRIBUTE);
            final int orderId = messageAMQ.getIntProperty(ORDER_ID_HEADER_ATTRIBUTE);
            messageAMQ.removeProperty(TRACE_ID_HEADER_ATTRIBUTE);
            messageAMQ.removeProperty(ORDER_ID_HEADER_ATTRIBUTE);

            DistributedMonitoringRecordWriter
                .writeBeforeReceivedRecord(ownTraceId, ownOrderId, traceId, orderId);
          } catch (final IOException e) {
            this.unknownRecordReceived(messageAMQ, ownTraceId, ownOrderId);
          } catch (final JMSException e) {
            this.unknownRecordReceived(messageAMQ, ownTraceId, ownOrderId);
          }
        } else {
          this.unknownRecordReceived(messageAMQ, ownTraceId, ownOrderId);
        }
      } catch (final JMSException e) {
        this.unknownRecordReceived(messageAMQ, ownTraceId, ownOrderId);
      } finally {
        probeController.enableMonitoring();
      }
    }
  }

  private void unknownRecordReceived(final ActiveMQMessage messageAMQ, final long ownTraceId,
      final int ownOrderId) {
    DistributedMonitoringRecordWriter.writeBeforeUnknownReceivedRecord(ownTraceId,
        ownOrderId,
        DistributedMonitoringRecordWriter.UNKNOWN_SENDER,
        messageAMQ.getDestination().toString());
  }

  @Around("call(void org.apache.activemq.broker.region.Queue.sendMessage(org.apache.activemq.command.Message))")
  public Object brokerMessageReceived(final ProceedingJoinPoint thisJoinPoint) throws Throwable {
    final Object[] args = thisJoinPoint.getArgs();
    final DistributedMonitoringTempDisabler probeController =
        DistributedMonitoringTempDisabler.getProbeController();
    if (probeController.isMonitoringEnabled()) {
      probeController.disableMonitoring();

      final ActiveMQMessage messageAMQ = (ActiveMQMessage) args[0];
      this.brokerAction(thisJoinPoint, messageAMQ);

      probeController.enableMonitoring();
    }
    return thisJoinPoint.proceed(args);
  }

  private void brokerAction(final ProceedingJoinPoint thisJoinPoint,
      final ActiveMQMessage messageAMQ) throws Throwable {
    final ProbeTraceMetaData trace = TraceRegistry.getTrace();
    trace.incrementStackDepth();
    final long ownTraceId = trace.getTraceId();
    final int ownOrderId = trace.getNextOrderId();

    boolean marshal = false;
    try {
      if (messageAMQ.getMarshalledProperties() != null) {
        // get marshaled properties
        marshal = true;
        messageAMQ.clearMarshalledState();
        // add dummy property to call private method
        // Message.lazyCreateProperties()
        messageAMQ.setProperty("dummyblabliblub", "12345");
        messageAMQ.removeProperty("dummyblabliblub");
      }
      if (messageAMQ.propertyExists(TRACE_ID_HEADER_ATTRIBUTE)) {
        // Message has monitoring information
        final long traceId = messageAMQ.getLongProperty(TRACE_ID_HEADER_ATTRIBUTE);
        final int orderId = messageAMQ.getIntProperty(ORDER_ID_HEADER_ATTRIBUTE);

        DistributedMonitoringRecordWriter
            .writeBeforeReceivedRecord(ownTraceId, ownOrderId, traceId, orderId);

      } else {
        // Message has no monitoring information
        DistributedMonitoringRecordWriter.writeBeforeUnknownReceivedRecord(ownTraceId,
            ownOrderId,
            "",
            messageAMQ.getDestination().toString());
      }
    } catch (final JMSException e) {
    }

    try {
      messageAMQ.setProperty(TRACE_ID_HEADER_ATTRIBUTE, ownTraceId);
      messageAMQ.setProperty(ORDER_ID_HEADER_ATTRIBUTE, ownOrderId);
      if (marshal) {
        messageAMQ.beforeMarshall(null);
      }
    } catch (final IOException e) {
    }
    DistributedMonitoringRecordWriter
        .writeBeforeSentRecord(ownTraceId, ownOrderId, messageAMQ.getDestination().toString());
  }

  @Around("execution(boolean org.apache.activemq.broker.region.PrefetchSubscription.dispatch(..))")
  public Object brokerMessageSent(final ProceedingJoinPoint thisJoinPoint) throws Throwable {
    final Object[] args = thisJoinPoint.getArgs();
    final DistributedMonitoringTempDisabler probeController =
        DistributedMonitoringTempDisabler.getProbeController();
    if (probeController.isMonitoringEnabled()) {
      probeController.disableMonitoring();

      final MessageReference reference = (MessageReference) args[0];
      final ActiveMQMessage messageAMQ = (ActiveMQMessage) reference.getMessage();
      this.brokerAction(thisJoinPoint, messageAMQ);

      probeController.enableMonitoring();
    }
    return thisJoinPoint.proceed(args);
  }
}
