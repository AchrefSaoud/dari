package utm.tn.dari.modules.annonce.Utils.serializers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;
import utm.tn.dari.modules.annonce.Dtoes.UserEvent;

import java.nio.charset.StandardCharsets;

public class UserEventSerializer implements Serializer<UserEvent> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(String s, UserEvent userEvent) {
        String serializedUserEvent = null;
        try {

           return objectMapper.writeValueAsString(userEvent).getBytes(StandardCharsets.UTF_8);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }
}
