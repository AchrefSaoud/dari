package utm.tn.dari.modules.annonce.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import utm.tn.dari.config.KafkaEnableConfig;
import utm.tn.dari.modules.annonce.Dtoes.NewAnnounceEvent;
import utm.tn.dari.modules.annonce.Dtoes.UserEvent;
import utm.tn.dari.modules.annonce.services.KafkaProducer;


@Service
@Conditional(KafkaEnableConfig.class) // Ensure this configuration is only loaded if Kafka is enabled

public class KafkaProducerImpl implements KafkaProducer {

    private String NEW_ANNOUNCE_TOPIC = "new_announce_topic";

    @Autowired
    private KafkaTemplate<String, NewAnnounceEvent> newAnnounceEventKafkaTemplate;



    @Override
    public void publishNewAnnonceEvent(NewAnnounceEvent newAnnounceEvent) {

        try {
            newAnnounceEventKafkaTemplate.send(NEW_ANNOUNCE_TOPIC,newAnnounceEvent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
