package net.explorviz.server.main;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import net.explorviz.server.repository.LandscapeExchangeService;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.servlet.ServletRegistration;
import org.glassfish.grizzly.servlet.WebappContext;

public class App {

	private static final URI BASE_URI = URI.create("http://0.0.0.0:8081/");

	public static void main(String[] args) {
		try {
			startHTTPServer();

			// Start ExplorViz Listener
			LandscapeExchangeService.startRepository();
			System.out.println("Server started. Traces should be processed. (Start Test App now)");

			Thread.currentThread().join();
		} catch (InterruptedException | IOException ex) {
			Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	private static void startHTTPServer() throws IOException {

		final ResourceConfig resourceConfig = new ExplorViz();
		HttpServer httpServer = GrizzlyHttpServerFactory.createHttpServer(BASE_URI);

		WebappContext context = new WebappContext("WebappContext", "/" + "webapp");
		context.addListener("net.explorviz.server.main.SetupListener");
		//context.addServlet("ServletContainer", new ServletContainer(resourceConfig));
		
		ServletRegistration registration = context.addServlet("ServletContainer", new ServletContainer(resourceConfig));
		registration.addMapping("/*");

		context.deploy(httpServer);
		httpServer.start();
	}

	public static ResourceConfig createApp() {
		return new ExplorViz();
	}

}
