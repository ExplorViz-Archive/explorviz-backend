package net.explorviz.analysis.live_trace_processing.main;

import java.io.IOException;
import java.util.Queue;
import net.explorviz.analysis.live_trace_processing.connector.TCPConnector;
import net.explorviz.analysis.live_trace_processing.filter.counting.RecordCountingFilter;
import net.explorviz.common.live_trace_processing.Constants;
import net.explorviz.common.live_trace_processing.configuration.Configuration;
import net.explorviz.common.live_trace_processing.configuration.ConfigurationFactory;
import net.explorviz.common.live_trace_processing.filter.SinglePipeConnector;
import net.explorviz.common.live_trace_processing.record.IRecord;
import net.explorviz.common.live_trace_processing.writer.load_balancer.LoadBalancer;

public class WorkerStarter {

	public static boolean isWorker;

	public static void main(final String[] args) {
		final Configuration configuration = ConfigurationFactory.createSingletonConfiguration();

		isWorker = configuration.getBooleanProperty(ConfigurationFactory.WORKER_ENABLED);

		Queue<IRecord> sink = null;

		if (isWorker) {
			final SinglePipeConnector<IRecord> tcpConnectorConnector = new SinglePipeConnector<IRecord>(
					Constants.TRACE_SUMMARIZATION_DISRUPTOR_SIZE);

			final TCPConnector connector = new TCPConnector(tcpConnectorConnector,
					configuration.getStringProperty(ConfigurationFactory.WRITER_TARGET_IP),
					configuration.getIntProperty(ConfigurationFactory.WRITER_TARGET_PORT, 10133),
					configuration);

			configureLoadBalancerIfEnabled(configuration, connector);

			connector.start();

			sink = tcpConnectorConnector.registerProducer();
		} else {
			final SinglePipeConnector<IRecord> recordCountingConnector = new SinglePipeConnector<IRecord>(
					Constants.TRACE_SUMMARIZATION_DISRUPTOR_SIZE);
			new RecordCountingFilter(recordCountingConnector, null).start();

			// final SinglePipeConnector<IRecord> traceCountingConnector = new
			// SinglePipeConnector<IRecord>(
			// Constants.TRACE_SUMMARIZATION_DISRUPTOR_SIZE);
			// new TraceCountingFilter(traceCountingConnector,
			// recordCountingConnector.registerProducer()).start();

			sink = recordCountingConnector.registerProducer();
		}

		FilterConfiguration.configureAndStartFilters(configuration, sink);
	}

	private static void configureLoadBalancerIfEnabled(final Configuration configuration,
			final TCPConnector tcpConnector) {
		final boolean loadBalancerEnabled = configuration
				.getBooleanProperty(ConfigurationFactory.LOAD_BALANCER_ENABLED);

		if (loadBalancerEnabled) {
			new LoadBalancer(
					configuration.getStringProperty(ConfigurationFactory.LOAD_BALANCER_IP),
					configuration.getIntProperty(ConfigurationFactory.LOAD_BALANCER_PORT, 10200),
					configuration.getIntProperty(ConfigurationFactory.LOAD_BALANCER_WAIT_TIME,
							20000),
					configuration
							.getStringProperty(ConfigurationFactory.LOAD_BALANCER_SCALING_GROUP),
					tcpConnector);
		} else {
			try {
				tcpConnector.connect();
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
	}
}
