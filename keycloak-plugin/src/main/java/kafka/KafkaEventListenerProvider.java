package kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.jboss.logging.Logger;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;

import java.util.Map;

public class KafkaEventListenerProvider implements EventListenerProvider {

    private static final Logger log = Logger.getLogger(KafkaEventListenerProvider.class);
    private final KafkaProducer<String, String> producer;
    private final String topic;
    private final ObjectMapper mapper;

    public KafkaEventListenerProvider(KafkaProducer<String, String> producer, String topic) {
        this.producer = producer;
        this.topic = topic;
        this.mapper = new ObjectMapper();
    }

    record EventPayload(String userId, String email, String firstName, String lastName, Long timestamp) {}

    @Override
    public void onEvent(Event event) {

        if(event.getType() == EventType.REGISTER){
            try{
                Map<String, String> details = event.getDetails();

                EventPayload payload = new EventPayload(
                        event.getUserId(),
                        details.get("email"),
                        details.get("first_name"),
                        details.get("last_name"),
                        System.currentTimeMillis()
                );

                String json = mapper.writeValueAsString(payload);
                producer.send(new ProducerRecord<>(topic, event.getUserId(), json));
                log.infof("Registration event sent to Kafka for user: %s", event.getUserId());

            }catch(JsonProcessingException e){
                log.error("Failed to serialize event payload", e);
            }catch(Exception e){
                log.error("Failed to send event to Kafka", e);
            }
        }

    }

    @Override
    public void onEvent(AdminEvent adminEvent, boolean b) {

    }

    @Override
    public void close() {

    }
}
