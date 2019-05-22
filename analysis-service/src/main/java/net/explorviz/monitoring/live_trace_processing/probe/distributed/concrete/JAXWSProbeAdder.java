package net.explorviz.monitoring.live_trace_processing.probe.distributed.concrete;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.jws.HandlerChain;
import javax.xml.ws.Service;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.handler.PortInfo;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

/**
 * This aspect is used to add the JAXWSProbe to the JAX-WS handler chain of endpoints and services.
 * The endpoints have to be published with the static method static Endpoint
 * javax.xml.ws.Endpoint.publish(String, Object)
 */
@Aspect
public class JAXWSProbeAdder {

  /**
   * This advice adds the JAXWSProbe to the JAX-WS hander chain of a javax.xml.ws.Endpoint. The
   * Endpoint has to be published with static method Endpoint javax.xml.ws.Endpoint.publish(String,
   * Object). The handler chain is added by setting a javax.jws.HandlerChain annotation to the
   * Object that will be published.
   *
   * @param thisJoinPoint Information about the join point, provided by AspectJ
   */
  @Before("call(static javax.xml.ws.Endpoint javax.xml.ws.Endpoint.publish(String, Object))")
  public void startEndpoint(final JoinPoint thisJoinPoint) {
    final Object classFileObject = thisJoinPoint.getArgs()[1];

    // Do not remove! This is required to be able to access the annotation
    // field with reflection. Probably a problem with lazy loading.
    classFileObject.getClass().getAnnotations();

    final Annotation newAnnotation = new HandlerChain() {
      @Override
      public Class<? extends Annotation> annotationType() {
        return HandlerChain.class;
      }

      @Override
      public String file() {
        return "META-INF/jaxws_probe_handlers.xml";
      }

      @Override
      public String name() {
        return "";
      }
    };
    try {
      final Field annotationDataField = Class.class.getDeclaredField("annotationData");
      annotationDataField.setAccessible(true);

      final Object annotationData = annotationDataField.get(classFileObject.getClass());

      final Field annotationsField = annotationData.getClass().getDeclaredField("annotations");
      annotationsField.setAccessible(true);

      @SuppressWarnings("unchecked")
      final Map<Class<? extends Annotation>, Annotation> annotations =
          (Map<Class<? extends Annotation>, Annotation>) annotationsField.get(annotationData);

      annotations.put(HandlerChain.class, newAnnotation);
    } catch (final IllegalArgumentException e) {
      e.printStackTrace();
    } catch (final IllegalAccessException e) {
      e.printStackTrace();
    } catch (final NoSuchFieldException e) {
      e.printStackTrace();
    } catch (final SecurityException e) {
      e.printStackTrace();
    }
  }

  /**
   * This advice adds the JAXWSProbe to the JAX-WS hander chain of a javax.xml.ws.Service. The
   * advice is applied when a Service is created with the static method Service
   * javax.xml.ws.Service+.create(..). The handler chain is added by replacing the HandlerResolver
   * of the Service with a prepared ClientHandlerResolver.
   *
   * @param thisJoinPoint information about the join point, provided by AspectJ
   */
  @AfterReturning(pointcut = "call(static javax.xml.ws.Service javax.xml.ws.Service+.create(..))",
      returning = "service")
  public void startService(final JoinPoint thisJoinPoint, final Service service) {
    HandlerResolver resolver = service.getHandlerResolver();
    resolver = new ClientHandlerResolver(resolver);
    service.setHandlerResolver(resolver);
  }

  private static final class ClientHandlerResolver implements HandlerResolver {
    private final HandlerResolver replacedHandlerResolver;

    private ClientHandlerResolver(final HandlerResolver replacedHandlerResolver) {
      super();
      this.replacedHandlerResolver = replacedHandlerResolver;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List<Handler> getHandlerChain(final PortInfo portInfo) {
      List<Handler> hchain = null;
      if (this.replacedHandlerResolver != null) {
        hchain = this.replacedHandlerResolver.getHandlerChain(portInfo);
      }
      if (hchain == null) {
        hchain = new ArrayList<>();
      }
      final JAXWSProbe jaxwsProbe = new JAXWSProbe();
      jaxwsProbe.setClientSide();
      hchain.add(jaxwsProbe);
      return hchain;
    }
  }
}
