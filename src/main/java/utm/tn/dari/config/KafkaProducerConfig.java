package utm.tn.dari.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import utm.tn.dari.modules.annonce.Dtoes.NewAnnounceEvent;
import utm.tn.dari.modules.annonce.Utils.serializers.NewAnnounceEventSerializer;
import utm.tn.dari.modules.annonce.Utils.serializers.NotificationsToBeSentDeserializer;

import utm.tn.dari.modules.annonce.events.NotificationsToBeSentEvent;

import java.util.HashMap;
import java.util.Map;

    @Configuration
    @Conditional(KafkaEnableConfig.class) // Ensure this configuration is only loaded if Kafka is enabled
    public class KafkaProducerConfig {

        @Bean
        public ProducerFactory<String, NewAnnounceEvent> producerFactory() {
            Map<String, Object> configProps = new HashMap<>();
            configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9093");
            configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, NewAnnounceEventSerializer.class); // Jackson
            return new DefaultKafkaProducerFactory<>(configProps);
        }

        @Bean
        public KafkaTemplate<String, NewAnnounceEvent> kafkaProducerTemplate() {
            return new KafkaTemplate<>(producerFactory());
        }

        @Bean
        public ConsumerFactory<String, NotificationsToBeSentEvent> notificationsToBeSentEventConsumerFactory(){
            Map<String, Object> configProps = new HashMap<>();
            configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9093");
            configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, NotificationsToBeSentDeserializer.class); // <-- Replace with your actual deserializer
            configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "notifications-to-be-sent-group"); // Add a group ID

            return new DefaultKafkaConsumerFactory<>(configProps);
        }
        @Bean
        public ConcurrentKafkaListenerContainerFactory<String,NotificationsToBeSentEvent> notificationsToBeSentEventKafkaListenerContainerFactory() {
            ConcurrentKafkaListenerContainerFactory<String, NotificationsToBeSentEvent> factory =
                    new ConcurrentKafkaListenerContainerFactory<>();
            factory.setConsumerFactory(notificationsToBeSentEventConsumerFactory());
            return factory;
        }
    }
