package utm.tn.dari.modules.annonce.Utils.serializers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import utm.tn.dari.modules.eventHandler.dtoes.EventDto;
import utm.tn.dari.modules.eventHandler.dtoes.UserInteractionDTO;

import java.io.IOException;


public class EventHandlerDeserializer implements Deserializer<EventDto<UserInteractionDTO>> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public EventDto<UserInteractionDTO> deserialize(String topic, byte[] data) {
        if (data == null) return null;
        try {
            return objectMapper.readValue(data, new TypeReference<EventDto<UserInteractionDTO>>() {});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
