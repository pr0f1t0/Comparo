package kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.keycloak.Config;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

import java.util.Properties;

public class KafkaEventListenerProviderFactory implements EventListenerProviderFactory {

    private static final String PROVIDER_ID = "kafka-producer";
    private KafkaProducer<String, String> producer;
    private String topic;


    @Override
    public EventListenerProvider create(KeycloakSession keycloakSession) {
        return new KafkaEventListenerProvider(producer, topic);
    }

    @Override
    public void init(Config.Scope scope) {
        String bootstrapServers = System.getenv().getOrDefault("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092");
        this.topic = System.getenv().getOrDefault("KAFKA_TOPIC", "user.registered");

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.ACKS_CONFIG, "all");

        this.producer = new KafkaProducer<>(props);

    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {

    }

    @Override
    public void close() {
        if (producer != null) {
            producer.close();
        }

    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}
