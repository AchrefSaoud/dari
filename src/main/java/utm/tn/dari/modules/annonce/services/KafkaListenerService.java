package utm.tn.dari.modules.annonce.services;

import org.springframework.context.annotation.Conditional;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import utm.tn.dari.config.KafkaEnableConfig;
import utm.tn.dari.modules.annonce.events.NotificationsToBeSentEvent;


public interface KafkaListenerService {
    void listenToTopic(NotificationsToBeSentEvent notificationsToBeSentEvent);
}
