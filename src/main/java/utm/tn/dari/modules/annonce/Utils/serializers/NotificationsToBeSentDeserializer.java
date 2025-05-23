package utm.tn.dari.modules.annonce.Utils.serializers;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;

import utm.tn.dari.modules.annonce.events.NotificationsToBeSentEvent;

import java.io.IOException;

public class NotificationsToBeSentDeserializer implements Deserializer<NotificationsToBeSentEvent> {
    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public NotificationsToBeSentEvent deserialize(String s, byte[] bytes) {
        try {
            return objectMapper.readValue(bytes, NotificationsToBeSentEvent.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
