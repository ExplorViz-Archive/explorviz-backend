package net.explorviz.history.server.main;

import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.inject.Inject;
import javax.servlet.annotation.WebListener;
import net.explorviz.history.repository.persistence.mongo.LandscapeSerializationHelper;
import net.explorviz.history.repository.persistence.mongo.MongoLandscapeJsonApiRepository;
import net.explorviz.shared.common.idgen.IdGenerator;
import net.explorviz.shared.landscape.model.application.AggregatedClazzCommunication;
import net.explorviz.shared.landscape.model.application.Application;
import net.explorviz.shared.landscape.model.application.ApplicationCommunication;
import net.explorviz.shared.landscape.model.helper.BaseEntity;
import net.explorviz.shared.landscape.model.landscape.Landscape;
import net.explorviz.shared.landscape.model.landscape.Node;
import net.explorviz.shared.landscape.model.landscape.NodeGroup;
import net.explorviz.shared.landscape.model.landscape.System;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEvent.Type;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Primary starting class - executed, when the servlet context is started.
 */
@WebListener
public class SetupApplicationListener implements ApplicationEventListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(SetupApplicationListener.class);

  @Inject
  private KafkaConsumer<String, String> kafkaConsumer;

  @Inject
  private LandscapeSerializationHelper serializationHelper;

  @Inject
  private MongoLandscapeJsonApiRepository mongoLandscapeRepo;
  
  @Inject
  private IdGenerator idGenerator;

  @Override
  public void onEvent(final ApplicationEvent event) {

    // After this type, CDI (e.g. injected LandscapeExchangeService) has been
    // fullfilled
    final Type t = Type.INITIALIZATION_FINISHED;

    if (event.getType().equals(t)) {
      BaseEntity.initialize(idGenerator);
      this.startHistoryBackend();
    }
  }

  @Override
  public RequestEventListener onRequest(final RequestEvent requestEvent) {
    return null;
  }

  private void startHistoryBackend() {
    LOGGER.info("\n");
    LOGGER.info("* * * * * * * * * * * * * * * * * * *\n"); // NOCS
    LOGGER.info("Server (ExplorViz History) sucessfully started.\n");
    LOGGER.info("* * * * * * * * * * * * * * * * * * *\n");
    
    ExecutorService executor = Executors.newSingleThreadExecutor();
    executor.submit(() -> {
      LOGGER.info("Starting Kafka Exchange \n");

      kafkaConsumer.subscribe(Arrays.asList("landscape-update"));

      while (true) {
        ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofMillis(100));

        for (ConsumerRecord<String, String> record : records) {

          LOGGER.info("Recevied landscape Kafka record: {}", record.value());
          java.lang.System.out.println("Size history: " +  record.value().getBytes().length);

          String serializedLandscape = record.value();
          java.lang.System.out.println(serializedLandscape);
          
          final Landscape l = this.serializationHelper
              .deserialize(serializedLandscape.replaceAll("(?U)\\p{Cntrl}|\\p{Gc=Cf}", ""));
          
          
          mongoLandscapeRepo.save(l.getTimestamp().getTimestamp(), l, calculateTotalRequests(l));
        
        }
      }
    });
  }

  // TODO put into ModelHelper or something
  private static int calculateTotalRequests(final Landscape landscape) {

    int totalRequests = 0;

    for (final System system : landscape.getSystems()) {
      for (final NodeGroup nodegroup : system.getNodeGroups()) {
        for (final Node node : nodegroup.getNodes()) {
          for (final Application application : node.getApplications()) {
            // aggClazzCommunication
            for (final AggregatedClazzCommunication clazzCommu : application
                .getAggregatedClazzCommunications()) {
              totalRequests += clazzCommu.getTotalRequests();

            }
            // applicationCommunication
            for (final ApplicationCommunication appCommu : application
                .getApplicationCommunications()) {
              totalRequests += appCommu.getRequests();
            }
          }
        }
      }
    }
    return totalRequests;
  }

}
