package utm.tn.dari.modules.annonce.services;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import utm.tn.dari.modules.annonce.events.NotificationsToBeSentEvent;

@Service
public interface KafkaListenerService {
    @KafkaListener(topics = "similar_calc_announces_topic", groupId = "similar_announces_notifs_worker",containerFactory = "notificationsToBeSentEventKafkaListenerContainerFactory")
    void listenToTopic(NotificationsToBeSentEvent notificationsToBeSentEvent);
}
