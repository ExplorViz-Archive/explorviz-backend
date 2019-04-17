package net.explorviz.landscape.injection;

import java.util.Properties;
import net.explorviz.shared.config.annotations.Config;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.glassfish.hk2.api.Factory;

public class KafkaProducerFactory implements Factory<KafkaProducer<String, String>> {

  @Config("exchange.kafka.bootstrap.servers")
  private String kafkaBootstrapServers;

  @Override
  public KafkaProducer<String, String> provide() {

    final Properties properties = new Properties();
    properties.put("bootstrap.servers", this.kafkaBootstrapServers);
    properties.put("acks", "all");
    properties.put("retries", "1");
    properties.put("batch.size", "16384");
    properties.put("linger.ms", "1");
    properties.put("max.request.size", "2097152");
    properties.put("buffer.memory", 33_554_432); // NOCS
    properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer"); // NOCS
    properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

    return new KafkaProducer<>(properties);
  }

  @Override
  public void dispose(final KafkaProducer<String, String> instance) {
    // Nothing to do
  }

}
