package utm.tn.dari.modules.annonce.Utils.serializers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;
import utm.tn.dari.modules.eventHandler.dtoes.EventDto;


public class EventHandlerSerializer implements Serializer<EventDto<?>> {
    ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public byte[] serialize(String s, EventDto<?> eventDto) {
        try {
            return objectMapper.writeValueAsBytes(eventDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
