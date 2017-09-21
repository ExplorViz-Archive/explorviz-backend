package net.explorviz.server.main;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import net.explorviz.server.repository.HibernateSessionFactory;
import net.explorviz.server.repository.LandscapeExchangeService;
import net.explorviz.server.security.PasswordStorage;
import net.explorviz.server.security.PasswordStorage.CannotPerformOperationException;
import net.explorviz.server.security.User;

import org.glassfish.grizzly.http.server.HttpServer;

public class App {

	private static final URI BASE_URI = URI.create("http://0.0.0.0:8081/");

	@Inject
	static SessionFactory sessionFactory;

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

			// Start ExplorViz Listener
			LandscapeExchangeService.startRepository();
			System.out.println("Server started. Traces should be processed. (Start Test App now)");

			createDummyUser();

			Thread.currentThread().join();
		} catch (IOException | InterruptedException ex) {
			Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	public static void createDummyUser() {
		try {
			String hashedPassword = PasswordStorage.createHash("admin");
			Session session = HibernateSessionFactory.getInstance().openSession();
			session.beginTransaction();
			session.save(new User("admin", hashedPassword));
			session.getTransaction().commit();
			session.close();
		} catch (CannotPerformOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static ResourceConfig createApp() {
		return new ExplorViz();
	}

}
