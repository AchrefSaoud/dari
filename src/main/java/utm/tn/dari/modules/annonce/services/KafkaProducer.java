package utm.tn.dari.modules.annonce.services;

import org.springframework.stereotype.Service;
import utm.tn.dari.modules.annonce.Dtoes.UserEvent;

@Service
public interface KafkaProducer {


    public void publishUserEvent(UserEvent userEvent);
}
