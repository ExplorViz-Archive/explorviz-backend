package net.explorviz.monitoring.live_trace_processing.main;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.explorviz.common.live_trace_processing.Constants;
import net.explorviz.common.live_trace_processing.configuration.Configuration;
import net.explorviz.common.live_trace_processing.configuration.ConfigurationFactory;
import net.explorviz.common.live_trace_processing.record.misc.SystemMonitoringRecord;
import net.explorviz.common.live_trace_processing.writer.load_balancer.LoadBalancer;
import net.explorviz.monitoring.live_trace_processing.probe.tracemanagement.TraceRegistry;
import net.explorviz.monitoring.live_trace_processing.system_mon.SystemMonitor;
import net.explorviz.monitoring.live_trace_processing.writer.TCPWriter;
import org.aspectj.lang.Signature;

public class MonitoringController {
  private static final SystemMonitor systemMonitor;

  private static final Map<Signature, Integer> signatureReg =
      new ConcurrentSkipListMap<>(new SignatureComperator());

  private static volatile boolean monitoringEnabled = true;

  // private static RemoteConfigurationServlet remoteConfigurationServlet;

  private static boolean initialized = false;

  private static RingBuffer<ByteBufferEvent> ringBuffer;

  static {
    // remoteConfigurationServlet = new RemoteConfigurationServlet();
    // new Thread(remoteConfigurationServlet).start();

    final Configuration configuration = ConfigurationFactory.createSingletonConfiguration();
    setMonitoringEnabled(configuration.getBooleanProperty(ConfigurationFactory.MONITORING_ENABLED));
    final boolean systemMonitorEnabled =
        configuration.getBooleanProperty(ConfigurationFactory.SYSTEM_MONITORING_ENABLED);

    TraceRegistry.init(configuration);
    final ExecutorService exec = Executors.newSingleThreadExecutor();
    final Disruptor<ByteBufferEvent> disruptor = new Disruptor<>(ByteBufferEvent.EVENT_FACTORY,
        Constants.MONITORING_CONTROLLER_DISRUPTOR_SIZE, exec, ProducerType.MULTI,
        new ThreadSleepWaitingStrategy());

    final boolean androidMonitoring =
        configuration.getBooleanProperty(ConfigurationFactory.ANDROID_MONITORING);

    @SuppressWarnings("unchecked")
    final EventHandler<ByteBufferEvent>[] eventHandlers = new EventHandler[1];

    final TCPWriter tcpWriter =
        new TCPWriter(configuration.getStringProperty(ConfigurationFactory.WRITER_TARGET_IP),
            configuration.getIntProperty(ConfigurationFactory.WRITER_TARGET_PORT, 10133),
            androidMonitoring, configuration);

    eventHandlers[0] = tcpWriter;
    disruptor.handleEventsWith(eventHandlers);
    ringBuffer = disruptor.start();

    // final TCPWriter tcpWriter = new TCPWriter(tcpPipesMerger,
    // configuration.getStringProperty(ConfigurationFactory.WRITER_TARGET_IP),
    // configuration.getIntProperty(ConfigurationFactory.WRITER_TARGET_PORT,
    // 10133),
    // androidMonitoring, configuration);

    final boolean loadBalancerEnabled =
        configuration.getBooleanProperty(ConfigurationFactory.LOAD_BALANCER_ENABLED);

    tcpWriter.init();

    if (!androidMonitoring) {
      if (loadBalancerEnabled) {
        new LoadBalancer(configuration.getStringProperty(ConfigurationFactory.LOAD_BALANCER_IP),
            configuration.getIntProperty(ConfigurationFactory.LOAD_BALANCER_PORT, 9999),
            configuration.getIntProperty(ConfigurationFactory.LOAD_BALANCER_WAIT_TIME, 20000),
            configuration.getStringProperty(ConfigurationFactory.LOAD_BALANCER_SCALING_GROUP),
            tcpWriter);
      } else {
        try {
          tcpWriter.connect();
        } catch (final IOException e) {
          e.printStackTrace();
        }
      }
    }

    if (systemMonitorEnabled) {
      systemMonitor = new SystemMonitor(1000);
      systemMonitor.start();
    } else {
      systemMonitor = null;
    }

    // new TimeSignalReader(TimeUnit.SECONDS.toMillis(10), new
    // MonitoringController()).start();

    initialized = true;
  }

  private MonitoringController() {

  }

  public static final boolean isMonitoringEnabled() {
    return monitoringEnabled;
  }

  public static final void setMonitoringEnabled(final boolean monitoringEnabled) {
    MonitoringController.monitoringEnabled = monitoringEnabled;
  }

  public static void sendOutBuffer(final ByteBuffer buffer) {
    if (!initialized) {
      buffer.clear();
      return;
    }

    final long hiseq = ringBuffer.next();

    buffer.flip();

    final ByteBufferEvent valueEvent = ringBuffer.get(hiseq);
    final ByteBuffer oldBuffer = valueEvent.getValue();
    oldBuffer.clear();
    oldBuffer.put(buffer);
    ringBuffer.publish(hiseq);

    buffer.clear();
  }

  public static final Integer getIdForSignature(final Signature sig) {
    Integer result = signatureReg.get(sig);
    if (result == null) {
      final String value = SignatureToStringConverter.signatureToLongString(sig);
      result = MonitoringStringRegistry.getIdForString(value);
      signatureReg.put(sig, result);
    }
    return result;
  }

  public static final void sendOutSystemRecord(final SystemMonitoringRecord record) {
    final long hiseq = ringBuffer.next();
    final ByteBufferEvent valueEvent = ringBuffer.get(hiseq);
    final ByteBuffer buffer = valueEvent.getValue();
    buffer.clear();
    buffer.put(SystemMonitoringRecord.CLAZZ_ID);
    buffer.putDouble(record.getCpuUtilization());
    buffer.putLong(record.getUsedRAM());
    buffer.putLong(record.getAbsoluteRAM());
    ringBuffer.publish(hiseq);
  }

  public static void shutdown() {
    new Thread(new Runnable() {

      @Override
      public void run() {
        try {
          Thread.sleep(2000);
        } catch (final InterruptedException e1) {
        }

        // remoteConfigurationServlet.stop();

        if (systemMonitor != null) {
          systemMonitor.shutdown();
        }
      }
    }).start();
  }
}


final class SignatureComperator implements Comparator<Signature>, Serializable {
  private static final long serialVersionUID = 6319332357717471530L;

  @Override
  public int compare(final Signature o1, final Signature o2) {
    return o1.hashCode() - o2.hashCode();
  }
}
