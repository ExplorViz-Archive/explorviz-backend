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

  public LoadBalancer(final String loadBalancerHostname, final int loadBalancerPort,
      final int loadBalancerWaitTimeInMillis, final String scalingGroupName, final IWriter writer) {
    this.writer = writer;

    try {
      this.loadBalancerURL = new URL(
          "http://" + loadBalancerHostname + ":" + loadBalancerPort + "?group=" + scalingGroupName);
    } catch (final MalformedURLException e) {
      e.printStackTrace();
    }

    this.loadBalancerWaitTimeInMillis = loadBalancerWaitTimeInMillis;

    this.createLoadBalancer();
  }

  private void createLoadBalancer() {
    this.writer.setProviderURL(null);
    if (this.loadBalancerIPFetcher != null) {
      this.loadBalancerIPFetcher.interrupt();
    }

    this.loadBalancerIPFetcher =
        new LoadBalancerIPFetcher(this.loadBalancerURL, this.loadBalancerWaitTimeInMillis, this);
    this.loadBalancerIPFetcher.start();
  }

  public final void cleanup() {
    if (this.loadBalancerIPFetcher != null) {
      this.loadBalancerIPFetcher.interrupt();
    }

    this.writer.disconnect();
  }

  @Override
  public final void receivedNewIp(final URL newProviderURL) {
    synchronized (this) {
      if (this.writer.getProviderURL() == null || this.writer.isDisconnected()) {
        this.setProviderURLAndConnect(newProviderURL);
        return;
      }

      if (!newProviderURL.getHost().equals(this.writer.getProviderURL().getHost())
          || newProviderURL.getPort() != this.writer.getProviderURL().getPort()) {
        this.writer.disconnect();
        this.setProviderURLAndConnect(newProviderURL);
      }

    }
  }

  public void setProviderURLAndConnect(final URL newProviderURL) {
    try {
      this.writer.setProviderURL(newProviderURL);
      this.writer.connect();
    } catch (final IOException e) {
      this.writer.setProviderURL(null);
      e.printStackTrace();
    }
  }
}
