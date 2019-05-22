package net.explorviz.common.live_trace_processing.configuration;

interface Keys {
	public static final String PREFIX = "explorviz.live_trace_processing.";

	public static final String CUSTOM_PROPERTIES_LOCATION_CLASSPATH = "META-INF/"
			+ PREFIX + "properties";
	public static final String DEFAULT_PROPERTIES_LOCATION_CLASSPATH = "META-INF/"
			+ PREFIX + "default.properties";

	public static final String CUSTOM_PROPERTIES_LOCATION_JVM = PREFIX
			+ "configuration";

	public static final String SYSTEM_NAME = PREFIX + "system_name";
	public static final String IP_ADDRESS = PREFIX + "ip_address";
	public static final String HOST_NAME = PREFIX + "host_name";
	public static final String APPLICATION_NAME = PREFIX + "application_name";
	public static final String PROGRAMMING_LANGUAGE = PREFIX
			+ "programming_language";

	public static final String CONTINOUS_MONITORING_ENABLED = PREFIX
			+ "continous_monitoring_enabled";

	public static final String WORKER_ENABLED = PREFIX + "worker_enabled";

	public static final String DEBUG = PREFIX + "debug";

	public static final String ANDROID_MONITORING = PREFIX
			+ "android_monitoring";

	public static final String MONITORING_ENABLED = PREFIX
			+ "monitoring_enabled";

	public static final String READER_LISTENING_PORT = PREFIX
			+ "reader_listening_port";

	public static final String WRITER_TARGET_IP = PREFIX + "writer_target_ip";
	public static final String WRITER_TARGET_PORT = PREFIX
			+ "writer_target_port";

	public static final String SYSTEM_MONITORING_ENABLED = PREFIX
			+ "system_monitoring_enabled";

	public static final String LOAD_BALANCER_ENABLED = PREFIX
			+ "writer_load_balancing_enabled";

	public static final String LOAD_BALANCER_IP = PREFIX
			+ "writer_load_balancing_ip";
	public static final String LOAD_BALANCER_PORT = PREFIX
			+ "writer_load_balancing_port";
	public static final String LOAD_BALANCER_WAIT_TIME = PREFIX
			+ "writer_load_balancing_wait_time";
	public static final String LOAD_BALANCER_SCALING_GROUP = PREFIX
			+ "writer_load_balancing_scaling_group";

	public static final String SENDING_BUFFER_SIZE = PREFIX
			+ "sending_buffer_size";
	public static final String MONITORING_CONTROLLER_DISRUPTOR_SIZE = PREFIX
			+ "monitoring_controller_disruptor_size";

	public static final String TCP_READER_DISRUPTOR_SIZE = PREFIX
			+ "tcp_reader_disruptor_size";

	public static final String TRACE_RECONSTRUCTION_DISRUPTOR_SIZE = PREFIX
			+ "trace_reconstruction_disruptor_size";

	public static final String TRACE_RECONSTRUCTION_BUFFER_INITIAL_SIZE = PREFIX
			+ "trace_reconstruction_buffer_initial_size";

	public static final String TRACE_RECONSTRUCTION_TIMEOUT_IN_SEC = PREFIX
			+ "trace_reconstruction_timeout_in_sec";

	public static final String TRACE_SUMMARIZATION_DISRUPTOR_SIZE = PREFIX
			+ "trace_summarization_disruptor_size";

}
