package net.explorviz.monitoring.live_trace_processing.probe.distributed.concrete;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import net.explorviz.monitoring.live_trace_processing.probe.distributed.DistributedMonitoringRecordWriter;
import net.explorviz.monitoring.live_trace_processing.probe.distributed.DistributedMonitoringTempDisabler;
import net.explorviz.monitoring.live_trace_processing.probe.tracemanagement.ProbeTraceMetaData;
import net.explorviz.monitoring.live_trace_processing.probe.tracemanagement.TraceRegistry;

/**
 * This Probe monitors incoming and outgoing JAX-WS messages. Therefore it has to be added to the
 * JAX-WS handler chain. The handler chain is added by the JAXWSProbeAdder aspect. It can also be
 * added manually with an javax.jws.HandlerChain annotation.
 */
public class JAXWSProbe implements SOAPHandler<SOAPMessageContext> {
  private boolean isClientSide = false;
  final static QName qnameTraceId = new QName("monitoring", "traceId");
  final static QName qnameOrderId = new QName("monitoring", "orderId");

  public void setClientSide() {
    this.isClientSide = true;
  }

  @Override
  public boolean handleMessage(final SOAPMessageContext context) {
    final DistributedMonitoringTempDisabler probeController =
        DistributedMonitoringTempDisabler.getProbeController();
    if (probeController.isMonitoringEnabled()) {
      probeController.disableMonitoring();

      final Boolean isOutbound = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

      final boolean outgoing = isOutbound.booleanValue();

      SOAPMessage soapMsg = null;
      SOAPEnvelope soapEnv = null;
      SOAPHeader soapHeader = null;
      try {
        soapMsg = context.getMessage();
        soapEnv = soapMsg.getSOAPPart().getEnvelope();
        soapHeader = soapEnv.getHeader();

        if (outgoing) {
          if (soapHeader == null) {
            soapHeader = soapEnv.addHeader();
          }

          final ProbeTraceMetaData trace = TraceRegistry.getTrace();

          if (this.isClientSide) {
            trace.incrementStackDepth();
            final Long ownTraceId = trace.getTraceId();
            final Integer ownOrderId = trace.getNextOrderId();

            final SOAPHeaderElement traceIDElement = soapHeader.addHeaderElement(qnameTraceId);
            traceIDElement.setActor(SOAPConstants.URI_SOAP_ACTOR_NEXT);
            traceIDElement.addTextNode(ownTraceId.toString());

            final SOAPHeaderElement soapHeaderElement2 = soapHeader.addHeaderElement(qnameOrderId);
            soapHeaderElement2.setActor(SOAPConstants.URI_SOAP_ACTOR_NEXT);
            soapHeaderElement2.addTextNode(ownOrderId.toString());

            soapMsg.saveChanges();

            DistributedMonitoringRecordWriter
                .writeBeforeSentRecord(ownTraceId, ownOrderId, "JAX-WS");
          } else {
            final Long ownTraceId = trace.getTraceId();
            final Integer ownOrderId = trace.getNextOrderId();

            DistributedMonitoringRecordWriter.writeAfterReceivedRecord(ownTraceId, ownOrderId);

            trace.decreaseStackDepthAndEndTraceIfNeccessary();
          }
        } else {
          boolean foundTraceInformation = false;
          int remoteOrderId = 0;
          long remoteTraceId = 0;

          if (soapHeader != null) {
            final ProbeTraceMetaData trace = TraceRegistry.getTrace();

            if (!this.isClientSide) {
              trace.incrementStackDepth();

              final Long ownTraceId = trace.getTraceId();
              final Integer ownOrderId = trace.getNextOrderId();

              @SuppressWarnings("unchecked")
              final Iterator<SOAPHeaderElement> headerIterator =
                  soapHeader.extractHeaderElements(SOAPConstants.URI_SOAP_ACTOR_NEXT);
              String traceIdStr = null;
              String orderIdStr = null;

              while (headerIterator.hasNext()) {
                final SOAPHeaderElement headerElem = headerIterator.next();
                final QName elementQName = headerElem.getElementQName();
                if (elementQName != null) {
                  if (headerElem.getElementQName().equals(qnameTraceId)) {
                    traceIdStr = headerElem.getTextContent();
                  }
                  if (headerElem.getElementQName().equals(qnameOrderId)) {
                    orderIdStr = headerElem.getTextContent();
                  }
                }
              }
              if (traceIdStr != null) {
                try {
                  remoteTraceId = Long.parseLong(traceIdStr);
                  foundTraceInformation = true;
                } catch (final NumberFormatException e) {
                  foundTraceInformation = false;
                }
              }
              if (orderIdStr != null) {
                try {
                  remoteOrderId = Integer.parseInt(orderIdStr);
                } catch (final NumberFormatException e) {
                }
              }

              if (foundTraceInformation) {
                DistributedMonitoringRecordWriter.writeBeforeReceivedRecord(ownTraceId,
                    ownOrderId,
                    remoteTraceId,
                    remoteOrderId);
              }
            } else {
              final Long ownTraceId = trace.getTraceId();
              final Integer ownOrderId = trace.getNextOrderId();

              DistributedMonitoringRecordWriter.writeAfterSentRecord(ownTraceId, ownOrderId);

              trace.decreaseStackDepthAndEndTraceIfNeccessary();
            }
          }
        }
      } catch (final SOAPException e) {
      } finally {
        probeController.enableMonitoring();
      }
    }
    return true;
  }

  @Override
  public boolean handleFault(final SOAPMessageContext context) {
    // unused
    return false;
  }

  @Override
  public void close(final MessageContext context) {
    // unused
  }

  @Override
  public Set<QName> getHeaders() {
    return new HashSet<>();
  }
}
