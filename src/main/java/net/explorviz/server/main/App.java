package net.explorviz.server.main;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import net.explorviz.server.repository.LandscapeRepositoryModel;
import net.explorviz.server.repository.RepositoryStarter;

import org.glassfish.grizzly.http.server.HttpServer;

public class App {

	private static final URI BASE_URI = URI.create("http://0.0.0.0:8080/");

	public static void main(String[] args) {
		try {

			final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(BASE_URI, createApp(), false);
			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
				@Override
				public void run() {
					server.shutdownNow();
				}
			}));
			server.start();
			
			startRepo();

			System.out.println("Server started. Traces should be processed. (Start Test App now)");

			Thread.currentThread().join();
		} catch (IOException | InterruptedException ex) {
			Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public static ResourceConfig createApp() {
		return new ExplorViz();
	}

	private static void startRepo() {
		final LandscapeRepositoryModel model = LandscapeRepositoryModel.getInstance();

		new Thread(new Runnable() {

			@Override
			public void run() {
				new RepositoryStarter().start(model);
			}
		}).start();
	}
}
