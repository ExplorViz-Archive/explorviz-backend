package net.explorviz.common.live_trace_processing.writer.load_balancer;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import net.explorviz.common.live_trace_processing.writer.IWriter;

public class LoadBalancer implements ILoadBalancerIPReceiver {

	private URL loadBalancerURL;
	private final int loadBalancerWaitTimeInMillis;
	private LoadBalancerIPFetcher loadBalancerIPFetcher;
	private final IWriter writer;

	public LoadBalancer(final String loadBalancerHostname,
			final int loadBalancerPort, final int loadBalancerWaitTimeInMillis,
			final String scalingGroupName, final IWriter writer) {
		this.writer = writer;

		try {
			loadBalancerURL = new URL("http://" + loadBalancerHostname + ":"
					+ loadBalancerPort + "?group=" + scalingGroupName);
		} catch (final MalformedURLException e) {
			e.printStackTrace();
		}

		this.loadBalancerWaitTimeInMillis = loadBalancerWaitTimeInMillis;

		createLoadBalancer();
	}

	private void createLoadBalancer() {
		writer.setProviderURL(null);
		if (loadBalancerIPFetcher != null) {
			loadBalancerIPFetcher.interrupt();
		}

		loadBalancerIPFetcher = new LoadBalancerIPFetcher(loadBalancerURL,
				loadBalancerWaitTimeInMillis, this);
		loadBalancerIPFetcher.start();
	}

	public final void cleanup() {
		if (loadBalancerIPFetcher != null) {
			loadBalancerIPFetcher.interrupt();
		}

		writer.disconnect();
	}

	@Override
	public final void receivedNewIp(final URL newProviderURL) {
		synchronized (this) {
			if (writer.getProviderURL() == null || writer.isDisconnected()) {
				setProviderURLAndConnect(newProviderURL);
				return;
			}

			if (!newProviderURL.getHost().equals(
					writer.getProviderURL().getHost())
					|| newProviderURL.getPort() != writer.getProviderURL()
							.getPort()) {
				writer.disconnect();
				setProviderURLAndConnect(newProviderURL);
			}

		}
	}

	public void setProviderURLAndConnect(final URL newProviderURL) {
		try {
			writer.setProviderURL(newProviderURL);
			writer.connect();
		} catch (final IOException e) {
			writer.setProviderURL(null);
			e.printStackTrace();
		}
	}
}
