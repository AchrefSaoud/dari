package utm.tn.dari.modules.annonce.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import utm.tn.dari.modules.annonce.Dtoes.UserEvent;
import utm.tn.dari.modules.annonce.services.KafkaProducer;


@Service
public class KafkaProducerImpl implements KafkaProducer {

    private String USER_EVENT_TOPIC = "user-event-topic";
    @Autowired
    private KafkaTemplate<String, UserEvent> kafkaTemplate;
    @Override
    public void publishUserEvent(UserEvent userEvent) {
        try {
            kafkaTemplate.send(USER_EVENT_TOPIC,userEvent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
