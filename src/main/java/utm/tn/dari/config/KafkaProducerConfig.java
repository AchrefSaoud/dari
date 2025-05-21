package utm.tn.dari.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;
import utm.tn.dari.modules.annonce.Dtoes.NewAnnounceEvent;
import utm.tn.dari.modules.annonce.Dtoes.UserEvent;
import utm.tn.dari.modules.annonce.Utils.serializers.NewAnnounceEventSerializer;
import utm.tn.dari.modules.annonce.Utils.serializers.UserEventSerializer;

import java.util.HashMap;
import java.util.Map;

    @Configuration
    public class KafkaProducerConfig {

        @Bean
        public ProducerFactory<String, NewAnnounceEvent> producerFactory() {
            Map<String, Object> configProps = new HashMap<>();
            configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
            configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, NewAnnounceEventSerializer.class); // Jackson
            return new DefaultKafkaProducerFactory<>(configProps);
        }

        @Bean
        public KafkaTemplate<String, NewAnnounceEvent> kafkaTemplate() {
            return new KafkaTemplate<>(producerFactory());
        }
    }
