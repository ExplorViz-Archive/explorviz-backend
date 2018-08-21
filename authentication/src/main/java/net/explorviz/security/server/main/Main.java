package net.explorviz.security.server.main;

import java.io.IOException;
import java.net.URI;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.explorviz.shared.server.helper.PropertyService;

public class Main {

	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

	public static void main(final String[] args) {

		try {
			final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(getBaseURI(), createJaxRsApp(), false);
			Runtime.getRuntime().addShutdownHook(new Thread(server::shutdownNow));
			server.start();
			Thread.currentThread().join();
		} catch (IOException | InterruptedException ex) {
			LOGGER.error("Server start failed", ex);
		}

	}

	private static ResourceConfig createJaxRsApp() {
		return new ResourceConfig(new Application());
	}

	private static URI getBaseURI() {
		final String defaultBaseUri = "localhost";
		final String baseUri = PropertyService.getStringProperty("server.baseUri");

		final String defaultPort = "8081";
		final String port = PropertyService.getStringProperty("server.httpPort");

		String finalBaseUri = "http://";
		finalBaseUri += baseUri != null ? baseUri : defaultBaseUri;
		finalBaseUri += ":";
		finalBaseUri += port != null ? port : defaultPort;
		finalBaseUri += "/";

		return URI.create(finalBaseUri);
	}

}
