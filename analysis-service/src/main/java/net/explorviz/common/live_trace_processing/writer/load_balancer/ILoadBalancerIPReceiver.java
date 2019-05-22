package net.explorviz.common.live_trace_processing.writer.load_balancer;

import java.net.URL;

public interface ILoadBalancerIPReceiver {
	void receivedNewIp(URL newProviderURL);
}
