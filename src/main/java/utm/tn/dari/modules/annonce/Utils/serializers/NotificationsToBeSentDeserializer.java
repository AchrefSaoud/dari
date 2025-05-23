package utm.tn.dari.modules.annonce.Utils.serializers;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;

import utm.tn.dari.modules.annonce.events.NotificationsToBeSentEvent;

import java.io.IOException;
import java.util.List;

public class NotificationsToBeSentDeserializer implements Deserializer<NotificationsToBeSentEvent> {
    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public NotificationsToBeSentEvent deserialize(String s, byte[] bytes) {
        try {
            JsonNode node = objectMapper.readTree(bytes);
            NotificationsToBeSentEvent event = new NotificationsToBeSentEvent();

            event.setAnnounceId(node.get("announceId").asLong());

            JsonNode similarIdsNode = node.get("similarIds");
            if (similarIdsNode.isTextual()) {
                // similarIds est une chaîne, on parse ce contenu
                String jsonArrayString = similarIdsNode.asText();
                List<Long> list = objectMapper.readValue(jsonArrayString, new TypeReference<List<Long>>() {});
                event.setSimilarIds(list);
            } else if (similarIdsNode.isArray()) {
                List<Long> list = objectMapper.convertValue(similarIdsNode, new TypeReference<List<Long>>() {});
                event.setSimilarIds(list);
            } else {
                event.setSimilarIds(null);
            }

            return event;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
