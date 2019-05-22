package net.explorviz.common.live_trace_processing;

import net.explorviz.common.live_trace_processing.configuration.Configuration;
import net.explorviz.common.live_trace_processing.configuration.ConfigurationFactory;

public class Constants {
  public static final int SENDING_BUFFER_SIZE;

  public static final int MONITORING_CONTROLLER_DISRUPTOR_SIZE;

  public static final int TCP_READER_DISRUPTOR_SIZE;

  public static final int TRACE_RECONSTRUCTION_DISRUPTOR_SIZE;

  public static final int TRACE_RECONSTRUCTION_BUFFER_INITIAL_SIZE;

  public static final int TRACE_RECONSTRUCTION_TIMEOUT_IN_SEC;

  public static final int TRACE_SUMMARIZATION_DISRUPTOR_SIZE;

  static {
    final Configuration configuration = ConfigurationFactory.createSingletonConfiguration();
    SENDING_BUFFER_SIZE =
        configuration.getIntProperty(ConfigurationFactory.SENDING_BUFFER_SIZE, 65536);
    MONITORING_CONTROLLER_DISRUPTOR_SIZE =
        configuration.getIntProperty(ConfigurationFactory.MONITORING_CONTROLLER_DISRUPTOR_SIZE, 32);

    TCP_READER_DISRUPTOR_SIZE =
        configuration.getIntProperty(ConfigurationFactory.TCP_READER_DISRUPTOR_SIZE, 32);

    TRACE_RECONSTRUCTION_DISRUPTOR_SIZE =
        configuration.getIntProperty(ConfigurationFactory.TRACE_RECONSTRUCTION_DISRUPTOR_SIZE, 16);

    TRACE_RECONSTRUCTION_BUFFER_INITIAL_SIZE = configuration
        .getIntProperty(ConfigurationFactory.TRACE_RECONSTRUCTION_BUFFER_INITIAL_SIZE, 1024);

    TRACE_RECONSTRUCTION_TIMEOUT_IN_SEC =
        configuration.getIntProperty(ConfigurationFactory.TRACE_RECONSTRUCTION_TIMEOUT_IN_SEC, 4);

    TRACE_SUMMARIZATION_DISRUPTOR_SIZE =
        configuration.getIntProperty(ConfigurationFactory.TRACE_SUMMARIZATION_DISRUPTOR_SIZE, 16);
  }
}
