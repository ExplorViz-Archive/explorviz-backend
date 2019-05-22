package net.explorviz.common.live_trace_processing.writer.load_balancer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;

public class LoadBalancerIPFetcher extends Thread {
  private final URL loadBalancerProviderURL;
  private final int timeIntervalToWait;
  private final ILoadBalancerIPReceiver receiver;

  public LoadBalancerIPFetcher(final URL loadBalancerProviderURL, final int timeIntervalToWait,
      final ILoadBalancerIPReceiver receiver) {
    this.loadBalancerProviderURL = loadBalancerProviderURL;
    this.timeIntervalToWait = timeIntervalToWait;
    this.receiver = receiver;
  }

  @Override
  public void run() {
    while (!Thread.interrupted()) {
      try {
        final URL newProviderURL = this.fetchNewProviderURL();
        this.receiver.receivedNewIp(newProviderURL);
        Thread.sleep(this.timeIntervalToWait);
      } catch (final InterruptedException e) {
        return;
      } catch (final IOException e) {
        return;
      }
    }
  }

  private URL fetchNewProviderURL() throws IOException, UnsupportedEncodingException {
    final BufferedReader in = new BufferedReader(
        new InputStreamReader(this.loadBalancerProviderURL.openStream(), "UTF-8"));
    final String newProviderURL = in.readLine();
    in.close();
    return new URL("http://" + newProviderURL + ":" + 10133);
  }
}
