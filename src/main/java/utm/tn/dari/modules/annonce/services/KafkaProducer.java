package utm.tn.dari.modules.annonce.services;

import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;
import utm.tn.dari.config.KafkaEnableConfig;
import utm.tn.dari.modules.annonce.Dtoes.NewAnnounceEvent;
import utm.tn.dari.modules.annonce.Dtoes.UserEvent;


public interface KafkaProducer {


    public void publishNewAnnonceEvent(NewAnnounceEvent newAnnounceEvent);
}
